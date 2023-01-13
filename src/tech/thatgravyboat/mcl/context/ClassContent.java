package tech.thatgravyboat.mcl.context;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.macros.MacroContext;
import tech.thatgravyboat.mcl.context.functions.FunctionContext;
import tech.thatgravyboat.mcl.utils.ListMap;
import tech.thatgravyboat.mcl.utils.NoDuplicateHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;

public record ClassContent(
        ClassContext context,
        Map<String, MacroContext> defaults,
        Map<String, FunctionContext> functions
) {

    public static ClassContent create(String fileName, PeekableIterator<TokenPair> tokens) {
        ListMap<String, MacroContext> defaults = ListMap.of(MacroContext::id, new NoDuplicateHashMap<>());
        ListMap<String, FunctionContext> functions = ListMap.of(FunctionContext::id, new NoDuplicateHashMap<>());

        assertToken(Token.PACKAGE, tokens);
        String packageName = getFullyQualifiedIdenfier(tokens);
        List<String> imports = captureImports(tokens);
        assertToken(Token.CLASS, tokens);
        String className = assertIdentifier(tokens, true);
        if (!className.equals(fileName)) {
            throw new RuntimeException("Class name " + className + " does not match file name " + fileName);
        }

        ClassContext context = new ClassContext(packageName, className, imports);

        assertToken(Token.OPEN_BRACKET, tokens);
        while (!isToken(Token.CLOSED_BRACKET, tokens)) {
            var next = tokens.next();
            switch (next.token()) {
                case MARCO -> defaults.put(MacroContext.of(tokens));
                case FUNCTION -> functions.put(FunctionContext.of(context, tokens));
                default -> throw new RuntimeException("Unknown token " + next.token());
            }
        }
        return new ClassContent(context, defaults, functions);
    }

    private static List<String> captureImports(PeekableIterator<TokenPair> tokens) {
        List<String> imports = new ArrayList<>();
        while (isToken(Token.IMPORT, tokens)) {
            imports.add(getFullyQualifiedIdenfier(tokens));
        }
        return imports;
    }
}
