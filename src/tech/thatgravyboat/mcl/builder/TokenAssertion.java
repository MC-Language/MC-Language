package tech.thatgravyboat.mcl.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TokenAssertion {

    public static String assertToken(Token token, TokenPair pair) {
        if (pair.token().equals(token)) {
            return pair.value();
        }
        throw new RuntimeException("Expected " + token + " but got " + pair.token() + " with value " + pair.value());
    }

    public static String assertToken(Token token, Iterator<TokenPair> tokens) {
        if (tokens.hasNext()) {
            return assertToken(token, tokens.next());
        }
        throw new RuntimeException("Expected " + token.name() + " but got nothing");
    }

    public static boolean isToken(Token token, PeekableIterator<TokenPair> tokens) {
        if (tokens.hasNext()) {
            var next = tokens.peek();
            if (next.token().equals(token)) {
                tokens.next();
                return true;
            }
        }
        return false;
    }

    public static String assertIdentifier(TokenPair pair, boolean requireLower) {
        return assertIdentifier(pair, requireLower, null);
    }

    public static String assertIdentifier(TokenPair pair, boolean requireLower, String message) {
        if (pair.token().equals(Token.IDENTIFIER)) {
            String value = pair.value();
            if (requireLower && !value.toLowerCase().equals(value)) {
                if (message != null) {
                    throw new RuntimeException("Expected lower case identifier but got normal casing for " + message + " with value " + value);
                } else {
                    throw new RuntimeException("Expected lower case identifier but got normal casing with value " + value);
                }
            }
            return value;
        }
        throw new RuntimeException("Expected identifier but got " + pair.token());
    }

    public static String assertIdentifier(PeekableIterator<TokenPair> tokens, boolean requireLower) {
        return assertIdentifier(tokens, requireLower, null);
    }

    public static String assertIdentifier(PeekableIterator<TokenPair> tokens, boolean requireLower, String message) {
        return assertIdentifier(tokens.next(), requireLower, message);
    }

    public static String assertTypeReturn(PeekableIterator<TokenPair> tokens) {
        TokenAssertion.assertToken(Token.TYPE_RETURN, tokens);
        if (isToken(Token.OPEN_SQUARE_BRACKET, tokens)) {
            List<String> output = new ArrayList<>();
            boolean comma = true;
            while (!isToken(Token.CLOSED_SQUARE_BRACKET, tokens)) {
                if (!comma) throw new RuntimeException("Expected comma");
                output.add(assertToken(Token.STRING, tokens));
                comma = isToken(Token.COMMA, tokens);
            }
            if (comma) throw new RuntimeException("Trailing comma");
            TokenAssertion.assertToken(Token.SEMICOLON, tokens);
            return String.join("\n", output);
        }
        String returnType = TokenAssertion.assertToken(Token.STRING, tokens);
        TokenAssertion.assertToken(Token.SEMICOLON, tokens);
        return returnType;
    }

    public static String getFullyQualifiedIdenfier(PeekableIterator<TokenPair> tokens) {
        StringBuilder builder = new StringBuilder();
        while (tokens.hasNext() && !tokens.peek().token().equals(Token.SEMICOLON)) {
            Token token = tokens.peek().token();
            if (token.equals(Token.IDENTIFIER) || token.equals(Token.PERIOD)) {
                builder.append(tokens.next().value());
            } else {
                throw new RuntimeException("Expected identifier but got " + token);
            }
        }
        tokens.next();
        return builder.toString();
    }
}
