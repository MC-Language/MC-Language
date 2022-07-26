package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.lang.McFunction;
import tech.thatgravyboat.mcl.lang.Package;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Compiler {

    private final String project;
    private final String path;
    private final Collection<Package> packages;

    public Compiler(String project, Collection<Package> packages) {
        this.project = project;
        this.path = "mcbuild/" + project + "/functions/";
        this.packages = packages;
    }

    public void compile() throws Exception {
        Map<String, ArrayList<String>> tickers = new HashMap<>();
        List<String> loaders = new ArrayList<>();

        deleteFolder(new File(path));

        for (Package pkg : packages) {
            for (McFunction function : pkg.functions()) {
                writeFunction(function.fileName(), function.getData());
                if (function.tick() != null) {
                    tickers.computeIfAbsent(function.tick(), k -> new ArrayList<>()).add(function.fileName());
                }
                if (function.load()) {
                    loaders.add(function.fileName());
                }
                for (Map.Entry<String, String> subFunction : function.getSubFunctions()) {
                    writeFunction(subFunction.getKey(), subFunction.getValue());
                }
            }
        }

        List<String> tickData = new ArrayList<>();
        tickers.forEach((selector, functions) -> functions.forEach(f -> {
            if (selector.isEmpty()) tickData.add(String.format("function %s:%s", project, f));
            else tickData.add(String.format("execute as @e[%s] run function %s:%s", selector, project, f));
        }));

        if (!tickData.isEmpty()) writeFunction("tickers.mcfunction", String.join("\n", tickData));
        if (!loaders.isEmpty()) writeFunction("loaders.mcfunction", loaders.stream().map(f -> "function " + f).collect(Collectors.joining("\n")));
    }

    private void writeFunction(String key, String data) throws IOException {
        File subFunctionFile = new File(path + key);
        subFunctionFile.getParentFile().mkdirs();
        Files.write(subFunctionFile.toPath(), data.getBytes());
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
