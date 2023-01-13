package tech.thatgravyboat.mcl.context.functions.instructions;

import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;

import java.util.List;
import java.util.Map;

public interface Instruction {

    String getOutput(Map<ClassContext, ClassContent> context);

    default List<FileContent> getAdditionalFileOutput(Map<ClassContext, ClassContent> context) {
        return List.of();
    }
}
