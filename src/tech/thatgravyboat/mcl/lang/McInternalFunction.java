package tech.thatgravyboat.mcl.lang;

import java.util.List;

public record McInternalFunction(String project, String packageId, String function) implements McFunctionData {

    @Override
    public List<String> data() {
        return List.of(String.format("function %s:%s/%s", project, packageId, function));
    }
}
