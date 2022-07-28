package tech.thatgravyboat.mcl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record McIfElse(String project, String path, String statement, List<McFunctionData> iffunctions, List<McFunctionData> elsefunctions) implements McFunctionData {

    @Override
    public List<String> data() {
        String score = project + "/" + path;
        return List.of(
                "scoreboard objectives add comparable_data dummy",
                String.format("scoreboard players set %s comparable_data 1", score),
                String.format("scoreboard players set %s comparable_data 1", score+".max"),
                String.format("execute if %s run function %s:%s/if", statement, project, path),
                String.format("execute if score %s comparable_data = %s comparable_data run function %s:%s/else", score, score + ".max", project, path)
        );
    }

    @Override
    public Map<String, McFunctionData> otherFunctions() {
        String score = project + "/" + path;
        Map<String, McFunctionData> output = new HashMap<>();
        List<String> ifdata = new ArrayList<>();
        for (McFunctionData function : iffunctions) {
            ifdata.addAll(function.data());
            ifdata.add(String.format("scoreboard players set %s comparable_data 0", score));
            output.putAll(function.otherFunctions());
        }
        output.put(path + "/if.mcfunction", () -> ifdata);
        List<String> elsedata = new ArrayList<>();
        for (McFunctionData function : elsefunctions) {
            elsedata.addAll(function.data());
            output.putAll(function.otherFunctions());
        }
        output.put(path + "/else.mcfunction", () -> elsedata);
        return output;
    }
}
