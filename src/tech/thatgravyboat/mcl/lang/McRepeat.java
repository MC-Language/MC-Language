package tech.thatgravyboat.mcl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record McRepeat(String project, String path, int count, List<McFunctionData> functions) implements McFunctionData {

    @Override
    public List<String> data() {
        String score = project + "/" + path;
        return List.of(
            "scoreboard objectives add comparable_data dummy",
            String.format("scoreboard players set %s comparable_data 1", score),
            String.format("scoreboard players set %s comparable_data %d", score + ".max", count),
            String.format("function %s:%s/incrementor", project, path)
        );
    }

    @Override
    public Map<String, McFunctionData> otherFunctions() {
        String score = project + "/" + path;
        List<String> data = new ArrayList<>();
        Map<String, McFunctionData> output = new HashMap<>();
        for (McFunctionData function : functions) {
            data.addAll(function.data());
            output.putAll(function.otherFunctions());
        }
        data.add(String.format("scoreboard players add %s comparable_data 1", score));

        output.put(path + "/incrementor.mcfunction", () -> List.of(
                "function " + project + ":" + path,
                String.format("execute if score %1$s comparable_data <= %2$s comparable_data run function %3$s:%1$s/incrementor", score, score + ".max", project)
        ));

        output.put(path + ".mcfunction", () -> data);

        return output;
    }
}
