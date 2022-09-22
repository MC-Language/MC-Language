package tech.thatgravyboat.mcl;

import tech.thatgravyboat.mcl.builder.Parser;
import tech.thatgravyboat.mcl.builder.Tokenizer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java Main <project name>");
            return;
        }

        List<Path> srcFiles;

        try (Stream<Path> walk = Files.walk(Path.of("mcsrc"), 1)) {
            srcFiles = walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".mcl") || p.toString().endsWith(".mclang"))
                    .collect(Collectors.toList());
        }

        //Map<String, Package> packages = new LinkedHashMap<>();

        for (Path srcFile : srcFiles) {
            try {
                var tokenize = Tokenizer.tokenize(Files.readString(srcFile));
                Parser.parser("mcl_test", tokenize);

            }catch (Exception e) {
                System.out.println("\u001B[31mError parsing file: " + srcFile);
                System.out.println(e.getMessage());
                System.out.println("\u001B[0m");
                return;
            }
        }

        //new Compiler(projectName, packages.values()).compile();
    }
}