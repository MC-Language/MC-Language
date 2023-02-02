package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;
import tech.thatgravyboat.mcl.context.functions.FunctionContext;
import tech.thatgravyboat.mcl.utils.ResourceLocation;
import tech.thatgravyboat.mcl.utils.SimpleJson;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class TransCompiler {

    private static final Executor WRITE_POOL = Executors.newFixedThreadPool(10, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    private final Map<ClassContext, ClassContent> classes;
    private final Map<String, Collection<String>> events = new ConcurrentHashMap<>();

    public TransCompiler(Map<ClassContext, ClassContent> classes) {
        this.classes = new ConcurrentHashMap<>(classes);
    }

    public void compile() {
        deleteFolder(new File("mcbuild"));

        List<CompletableFuture<Boolean>> futures = classes.values().stream()
                .filter(content -> !content.functions().isEmpty())
                .map(this::compileClass)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (var entry : events.entrySet()) {
            ResourceLocation loc = new ResourceLocation(entry.getKey());
            writeToTagFile(loc.namespace(), loc.path(), entry.getValue());
        }

    }

    private CompletableFuture<Boolean> compileClass(ClassContent content) {
        return CompletableFuture.supplyAsync(() -> {
            boolean wrote = false;
            for (FunctionContext function : content.functions().values()) {
                for (FileContent fileContent : function.getFileOutput(classes)) {
                    fileContent.writeToFile("mcbuild");
                    wrote = true;
                }
                if (function.event() != null) {
                    this.events.computeIfAbsent(function.event(), s -> new ConcurrentLinkedQueue<>()).add(function.context().pPackage() + ":" + function.context().pClass() + "/" + function.id());
                }
            }
            return wrote;
        }, WRITE_POOL);
    }

    public static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        File[] files = folder.listFiles();
        if(files != null) {
            for(File f : files) {
                if(f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }

    public static void writeToTagFile(String namespace, String path, Collection<String> contents) {
        File file = new File("mcbuild/" + namespace + "/data/tags/" + path + ".json");
        file.getParentFile().mkdirs();
        try {
            SimpleJson.Object object = new SimpleJson.Object();
            SimpleJson.Array array = new SimpleJson.Array();
            for (String content : contents) {
                array.add(new SimpleJson.DefaultValue(content));
            }
            object.put("values", array);
            Files.write(file.toPath(), object.output().getBytes());
        }catch (Exception e) {
            throw new RuntimeException("Failed to write to file " + file.getAbsolutePath(), e);
        }
    }
}
