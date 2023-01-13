package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;
import tech.thatgravyboat.mcl.context.functions.FunctionContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TransCompiler {

    private static final Executor WRITE_POOL = Executors.newFixedThreadPool(10, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });


    private final Map<ClassContext, ClassContent> classes;

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

    }

    private CompletableFuture<Boolean> compileClass(ClassContent content) {
        return CompletableFuture.supplyAsync(() -> {
            boolean wrote = false;
            for (FunctionContext function : content.functions().values()) {
                for (FileContent fileContent : function.getFileOutput(classes)) {
                    fileContent.writeToFile("mcbuild");
                    wrote = true;
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
}
