package tech.thatgravyboat.mcl.lang;

import java.util.List;

public interface FuncInstruction {

    String replacement();

    default List<String> data() {
        return List.of();
    }

}
