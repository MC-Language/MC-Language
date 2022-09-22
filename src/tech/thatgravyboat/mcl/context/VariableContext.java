package tech.thatgravyboat.mcl.context;

import java.util.HashSet;
import java.util.Set;

public interface VariableContext {

    Set<String> getVariables();

    default void addVariable(String name) {
        if (containsVariable(name)) {
            throw new RuntimeException("Variable " + name + " already exists in this scope");
        }
        getVariables().add(name);
    }

    default boolean containsVariable(String name) {
        return getVariables().contains(name);
    }

    default Set<String> copyVariables() {
        return new HashSet<>(getVariables());
    }

    default VariableContext copy() {
        var copy = copyVariables();
        return () -> copy;
    }
}
