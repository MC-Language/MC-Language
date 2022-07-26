package tech.thatgravyboat.mcl.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface McFunctionData {

    List<String> data();

    default Map<String, McFunctionData> otherFunctions() {
        return new HashMap<>();
    }
}
