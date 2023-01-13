package tech.thatgravyboat.mcl.context.functions.instructions;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.macros.MacroContext;
import tech.thatgravyboat.mcl.context.functions.FunctionContext;
import tech.thatgravyboat.mcl.utils.NoDuplicateHashMap;

import java.util.Map;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;
import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertToken;

public record BasicFunctionInstruction(ClassContext context, String function, Map<String, TokenPair> parameters) implements Instruction {

    public static BasicFunctionInstruction of(int ignoredIndex, String id, ClassContext context, String ignoredFunctionId, PeekableIterator<TokenPair> tokens) {
        if (isToken(Token.PERIOD, tokens)) {
            String secondMethodPart = assertIdentifier(tokens, false);
            String methodClass = context.pImports().stream().filter(s -> s.endsWith(id)).findFirst().orElseThrow();
            return new BasicFunctionInstruction(ClassContext.ofFullQualifier(methodClass), secondMethodPart, gatherParameters(tokens));
        }
        return new BasicFunctionInstruction(context, id, gatherParameters(tokens));
    }

    public static Map<String, TokenPair> gatherParameters(PeekableIterator<TokenPair> tokens) {
        Map<String, TokenPair> parameters = new NoDuplicateHashMap<>();
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        while (!isToken(Token.CLOSED_PARENTHESIS, tokens)) {
            String id = assertIdentifier(tokens, false);
            assertToken(Token.EQUALS, tokens);
            parameters.put(id, tokens.next());
            isToken(Token.COMMA, tokens);
        }
        assertToken(Token.SEMICOLON, tokens);
        return parameters;
    }

    @Override
    public String getOutput(Map<ClassContext, ClassContent> contexts) {
        ClassContent content = contexts.get(context);
        if (content == null) {
            throw new IllegalStateException("Class " + context + " does not exist!");
        }
        FunctionContext function = content.functions().get(this.function);
        MacroContext defaultFunction = content.defaults().get(this.function);
        if (function == null && defaultFunction == null) {
            throw new IllegalStateException("Function " + this.function + " does not exist in " + context + "!");
        }
        if (defaultFunction != null) {
            return defaultFunction.getOutput(parameters);
        }
        if (!parameters.isEmpty()) throw new IllegalStateException("Non default functions do not accept parameters!");
        return "function " + context.pPackage() + ":" + context.pClass() + "/" + function.id();
    }
}
