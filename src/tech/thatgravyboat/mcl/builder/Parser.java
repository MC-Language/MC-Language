package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.context.FileContext;
import tech.thatgravyboat.mcl.context.MethodContext;

import java.util.List;
import java.util.regex.Matcher;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;

public class Parser {

    public static void parser(String file, List<TokenPair> tokens) {
        FileContext context = new FileContext(file);
        gatherVarContext(context, tokens);
        gatherMethodContext(context, tokens);
        System.out.println(context);
    }

    private static void gatherVarContext(FileContext context, List<TokenPair> tokens) {
        PeekableIterator<TokenPair> iterator = new PeekableIterator<>(tokens.iterator());
        while (iterator.hasNext()) {
            Matcher id = valueOf(Token.IDENTIFIER, iterator);
            if (id != null && iterator.hasNext() && iterator.next().token().equals(Token.DEFINE)) {
                assertToken(Token.INTEGER, iterator);
                context.addVariable(id.group());
            }
        }
    }

    private static void gatherMethodContext(FileContext context, List<TokenPair> tokens) {
        PeekableIterator<TokenPair> iterator = new PeekableIterator<>(tokens.iterator());
        while (iterator.hasNext()) {
            String service = parseService(iterator);
            Matcher id = valueOf(Token.IDENTIFIER, iterator);
            if (id != null && iterator.hasNext() && iterator.next().token().equals(Token.OPEN_PARENTHESIS)) {
                List<TokenPair> args = InstructionParser.parseCommaList(iterator, false);
                assertToken(Token.OPEN_BRACKET, iterator);

                MethodContext methodContext = new MethodContext(service, id.group(), context);
                args.forEach(arg -> methodContext.addVariable(arg.value().group()));
                InstructionParser.parseInstructions(methodContext, iterator);
                context.addMethod(methodContext);
            }
        }
    }

    private static String parseService(PeekableIterator<TokenPair> tokens)  {
        if (isToken(Token.SERVICE, tokens)) {
            assertToken(Token.OPEN_PARENTHESIS, tokens);
            Matcher service = assertToken(Token.STRING, tokens);
            assertToken(Token.CLOSED_PARENTHESIS, tokens);
            return service.group(1);
        } else {
            return null;
        }
    }

}
