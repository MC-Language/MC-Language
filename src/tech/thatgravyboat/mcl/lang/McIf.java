package tech.thatgravyboat.mcl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record McIf(boolean not, String project, String path, String statement, List<McFunctionData> functions) implements McFunctionData {

    @Override
    public List<String> data() {
        if (not) {
            return List.of(String.format("execute unless %s run function %s:%s/if", statement, project, path));
        }
        return List.of(String.format("execute if %s run function %s:%s/if", statement, project, path));
    }

    @Override
    public Map<String, McFunctionData> otherFunctions() {
        List<String> data = new ArrayList<>();
        Map<String, McFunctionData> output = new HashMap<>();
        for (McFunctionData function : functions) {
            data.addAll(function.data());
            output.putAll(function.otherFunctions());
        }
        output.put(path + "/if.mcfunction", () -> data);
        return output;
    }
}
