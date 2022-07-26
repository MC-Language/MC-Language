package tech.thatgravyboat.mcl.lang;

import java.util.List;
import java.util.Map;

public record McFunction(boolean load, String tick, String packageId, String name, List<McFunctionData> functionData) {

    public String fileName() {
        return packageId + "/" + name + ".mcfunction";
    }

    public String getData() {
        return functionData.stream()
                .flatMap(f -> f.data().stream())
                .reduce("", (a, b) -> a + b + "\n");
    }

    public List<Map.Entry<String, String>> getSubFunctions() {
        return functionData.stream()
                .flatMap(f -> f.otherFunctions().entrySet().stream())
                .map(f -> Map.entry(f.getKey(), f.getValue().data().stream().reduce("", (a, b) -> a + b + "\n")))
                .toList();
    }
}
