package tech.thatgravyboat.mcl.context;

import java.util.*;

public class FileContext implements VariableContext {

    private final String id;
    private final Set<String> variables = new HashSet<>();
    private final Map<String, MethodContext> methods = new HashMap<>();

    public FileContext(String id) {
        this.id = id;
    }

    public void addMethod(MethodContext method) {
        if (methods.containsKey(method.getId())) {
            throw new RuntimeException("Method " + method.getId() + " already exists.");
        }
        methods.put(method.getId(), method);
    }

    public String getId() {
        return id;
    }

    @Override
    public Set<String> getVariables() {
        return this.variables;
    }

    @Override
    public String toString() {
        return "FileContext{" +
                "variables=" + variables +
                ", methods=" + methods +
                '}';
    }
}
