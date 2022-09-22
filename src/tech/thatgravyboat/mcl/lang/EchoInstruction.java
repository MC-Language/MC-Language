package tech.thatgravyboat.mcl.lang;

import tech.thatgravyboat.mcl.context.MethodContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public record EchoInstruction(MethodContext context, String echo) implements FuncInstruction {

    private static final Pattern ECHO_PATTERN = Pattern.compile("[^$]*");

    @Override
    public String replacement() {
        List<String> elements = ECHO_PATTERN.matcher(echo).results().map(MatchResult::group).toList();
        List<String> newElements = new ArrayList<>();
        for (String element : elements) {
            if (element.startsWith("{")) {
                String variable = element.substring(1, element.length() - 1);
                if (!context.containsVariable(variable)) {
                    throw new RuntimeException("Variable " + variable + " does not exist.");
                }
                String scoreVariable = variable; //TODO CHANGE
                newElements.add("{\"score\":{\"name\": \"" + scoreVariable + "\", \"objective\": \"project_params\"}}");
            } else if (!element.isEmpty()) {
                newElements.add("{\"text\": \"" + element + "\"}");
            }
        }

        return "tellraw @a [" + String.join(",", newElements) + "]";
    }

    @Override
    public String toString() {
        return "EchoInstruction{echo='" + replacement() + "'}";
    }
}
