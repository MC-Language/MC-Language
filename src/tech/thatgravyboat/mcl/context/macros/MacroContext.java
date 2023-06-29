package tech.thatgravyboat.mcl.context.macros;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.utils.NoDuplicateHashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;

public record MacroContext(String id, String output, Set<String> requiredKeys, Map<String, MacroParameter> parameters) {

    public static MacroContext of(PeekableIterator<TokenPair> tokens) {
        Map<String, MacroParameter> parameters = new NoDuplicateHashMap<>();
        String type = assertIdentifier(tokens, false);
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        while (!isToken(Token.CLOSED_PARENTHESIS, tokens)) {
            String paramId = assertIdentifier(tokens, true, "macro parameters");
            parameters.put(paramId, getParameterType(tokens));
        }
        Set<String> requiredKeys = new HashSet<>();
        parameters.forEach((key, value) -> {
            if (value.isRequired()) {
                requiredKeys.add(key);
            }
        });
        return new MacroContext(type, assertTypeReturn(tokens), requiredKeys, parameters);
    }

    private static MacroParameter getParameterType(PeekableIterator<TokenPair> tokens) {
        assertToken(Token.COLON, tokens);
        MacroParameterType type = MacroParameterType.fromId(assertIdentifier(tokens, true));
        if (isToken(Token.COMMA, tokens) || tokens.peek().token() == Token.CLOSED_PARENTHESIS) {
            return new MacroParameter(type, null);
        }
        assertToken(Token.EQUALS, tokens);
        var next = tokens.next();
        if (!type.test(next)) {
            throw new RuntimeException("Invalid parameter type default value" + next);
        }

        MacroParameter parameter = new MacroParameter(type, next.value());
        isToken(Token.COMMA, tokens);
        return parameter;
    }

    public String getOutput(Map<String, TokenPair> inputs) {
        Set<String> keys = new HashSet<>(requiredKeys);
        keys.removeAll(inputs.keySet());
        if (!keys.isEmpty()) {
            throw new RuntimeException("Missing required keys " + keys);
        }
        String output = this.output;
        for (var entry : parameters.entrySet()) {
        output = output.replace("${" + entry.getKey() + "}", entry.getValue().get(inputs.get(entry.getKey())).replace("\\\"", "\""));
        }
        return output;
    }

}
