package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.context.MethodContext;
import tech.thatgravyboat.mcl.lang.CallInstruction;
import tech.thatgravyboat.mcl.lang.EchoInstruction;
import tech.thatgravyboat.mcl.lang.condition.IfCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertToken;
import static tech.thatgravyboat.mcl.builder.TokenAssertion.isToken;

public class InstructionParser {


    public static void parseInstructions(MethodContext ctx, PeekableIterator<TokenPair> tokens) {
        while (tokens.hasNext()) {
            if (isToken(Token.CLOSED_BRACKET, tokens)) {
                return;
            }
            TokenPair next = tokens.next();
            switch (next.token()) {
                case IF -> parseIf(false, ctx, tokens);
                case NOT_IF -> parseIf(true, ctx, tokens);
                case IDENTIFIER -> parseCall(next.value(), ctx, tokens);
                case ECHO -> parseEcho(ctx, tokens);
                default -> throw new IllegalStateException("Unexpected value: " + next.token());
            }
        }
    }

    private static void parseCall(Matcher id, MethodContext ctx, PeekableIterator<TokenPair> tokens) {
        if (isToken(Token.PERIOD, tokens)) {
            Matcher method = assertToken(Token.IDENTIFIER, tokens);
            assertToken(Token.OPEN_PARENTHESIS, tokens);
            List<TokenPair> args = parseCommaList(tokens, true);
            ctx.data().add(new CallInstruction(ctx, id.group() + "." + method.group(), args));
        } else {
            assertToken(Token.OPEN_PARENTHESIS, tokens);
            List<TokenPair> args = parseCommaList(tokens, true);
            ctx.data().add(new CallInstruction(ctx, id.group(), args));
        }
    }

    private static void parseIf(boolean flip, MethodContext ctx, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        List<IfCondition> conditions = new ArrayList<>();
        while (true) {
            TokenPair pair = tokens.next();
            switch (pair.token()) {
                case IDENTIFIER -> {
                    var matcher = assertToken(Token.COMPARISON_OPERATOR, tokens);
                    TokenPair value = tokens.next();
                    switch (value.token()) {
                        case INTEGER, IDENTIFIER -> conditions.add(new IfCondition(pair.value().group(), matcher.group(), value.value().group()));
                        default -> throw new IllegalStateException("Unexpected value: " + value.token());
                    }
                }
                case STATEMENT -> conditions.add(new IfCondition(pair.value().group(1)));
                default -> throw new IllegalStateException("Unexpected value: " + pair.token());
            }
            if (isToken(Token.AND, tokens)) {
                continue;
            }
            break;
        }
        System.out.println(conditions);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        assertToken(Token.OPEN_BRACKET, tokens);
        parseInstructions(ctx, tokens);
        assertToken(Token.CLOSED_BRACKET, tokens);
        if (isToken(Token.ELSE, tokens)) {
            assertToken(Token.OPEN_BRACKET, tokens);
            parseInstructions(ctx, tokens);
            assertToken(Token.CLOSED_BRACKET, tokens);
        }
    }

    private static void parseEcho(MethodContext ctx, PeekableIterator<TokenPair> tokens) {
        var value = assertToken(Token.STRING, tokens);
        ctx.data().add(new EchoInstruction(ctx, value.group(1)));
    }

    public static List<TokenPair> parseCommaList(PeekableIterator<TokenPair> tokens, boolean allowInts) {
        List<TokenPair> list = new ArrayList<>();
        TokenPair next = tokens.peek();
        if (next.token() == Token.CLOSED_PARENTHESIS) {
            tokens.next();
            return list;
        }
        while (tokens.hasNext()) {
            next = tokens.peek();
            if (next.token() == Token.IDENTIFIER || (next.token() == Token.INTEGER && allowInts)) {
                list.add(tokens.next());
                if (!isToken(Token.COMMA, tokens)) {
                    assertToken(Token.CLOSED_PARENTHESIS, tokens);
                    return list;
                }
            } else {
                throw new IllegalStateException("Unexpected value: " + next.token());
            }
        }
        return list;
    }
}
