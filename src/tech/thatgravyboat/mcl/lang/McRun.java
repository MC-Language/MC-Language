package tech.thatgravyboat.mcl.lang;

import java.util.List;

public record McRun(String run) implements McFunctionData {

    @Override
    public List<String> data() {
        return List.of(run);
    }

}
