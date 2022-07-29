package tech.thatgravyboat.mcl.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record McIfElse(boolean not, String project, String path, String statement, List<McFunctionData> iffunctions, List<McFunctionData> elsefunctions) implements McFunctionData {

    @Override
    public List<String> data() {
        if (not) {
            return List.of(
                    String.format("execute unless %s run function %s:%s/if", statement, project, path),
                    String.format("execute if %s run function %s:%s/else", statement, project, path)
            );
        }
        return List.of(
                String.format("execute if %s run function %s:%s/if", statement, project, path),
                String.format("execute unless %s run function %s:%s/else", statement, project, path)
        );
    }

    @Override
    public Map<String, McFunctionData> otherFunctions() {
        Map<String, McFunctionData> output = new HashMap<>();
        List<String> ifdata = new ArrayList<>();
        for (McFunctionData function : iffunctions) {
            ifdata.addAll(function.data());
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
