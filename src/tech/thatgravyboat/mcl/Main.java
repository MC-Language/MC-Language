package tech.thatgravyboat.mcl;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Tokenizer;
import tech.thatgravyboat.mcl.builder.TransCompiler;
import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.utils.FetchedFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Main {

    private static final Executor LOAD_POOL = Executors.newFixedThreadPool(10, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java Main <source paths ';' seperated>");
            return;
        }
        List<CompletableFuture<FetchedFile>> files = new ArrayList<>();
        for (String path : args[0].split(";")) {
            try (Stream<Path> walk = Files.walk(Path.of(path))) {
                walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".mcl") || p.toString().endsWith(".mclang"))
                    .map(Main::fetchFile)
                    .forEach(files::add);
            }
        }
        CompletableFuture.allOf(files.toArray(new CompletableFuture[0])).join();

        List<FetchedFile> fetchedFiles = new ArrayList<>();
        for (CompletableFuture<FetchedFile> file : files) {
            fetchedFiles.add(file.get());
        }

        Map<ClassContext, ClassContent> classes = new HashMap<>();

        for (FetchedFile file : fetchedFiles) {
            try {
                var tokenize = Tokenizer.tokenize(file.getContent());
                ClassContent content = ClassContent.create(file.getName(), new PeekableIterator<>(tokenize.iterator()));
                classes.put(content.context(), content);
            }catch (Exception e) {
                System.out.printf("""
                        [31mError parsing file: %s
                        %s
                        [0m
                        %n""", file.getName(), e.getMessage());
                return;
            }
        }

        new TransCompiler(classes).compile();
    }

    private static CompletableFuture<FetchedFile> fetchFile(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new FetchedFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, LOAD_POOL);
    }
}