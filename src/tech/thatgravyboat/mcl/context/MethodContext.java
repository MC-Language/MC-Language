package tech.thatgravyboat.mcl.context;

import tech.thatgravyboat.mcl.lang.FuncInstruction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MethodContext(String service, String id, FileContext parent, Set<String> variables, List<FuncInstruction> data) implements VariableContext {

    public MethodContext(String service, String id, FileContext parent) {
        this(service, id, parent, new HashSet<>(), new ArrayList<>());
    }

    public String getId() {
        return id;
    }

    public String getFileId() {
        return parent.getId();
    }

    @Override
    public Set<String> getVariables() {
        return this.variables;
    }

    @Override
    public boolean containsVariable(String name) {
        return VariableContext.super.containsVariable(name) || parent.containsVariable(name);
    }

    @Override
    public String toString() {
        return "MethodContext{id='" + id + '\'' + ", service='" + service + "', variables=" + variables + ", data=" + data + '}';
    }
}
