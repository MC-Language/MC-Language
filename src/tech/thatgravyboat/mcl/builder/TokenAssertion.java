package tech.thatgravyboat.mcl.builder;

import java.util.Iterator;
import java.util.regex.Matcher;

public class TokenAssertion {

    public static Matcher valueOf(Token token, Iterator<TokenPair> tokens) {
        try {
            return assertToken(token, tokens);
        } catch (Exception e) {
            return null;
        }
    }

    public static Matcher assertToken(Token token, Iterator<TokenPair> tokens) {
        if (tokens.hasNext()) {
            var next = tokens.next();
            if (next.token().equals(token)) {
                return next.value();
            }
            throw new RuntimeException("Expected " + token + " but got " + next.token());
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
}
