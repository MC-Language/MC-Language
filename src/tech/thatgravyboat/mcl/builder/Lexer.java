package tech.thatgravyboat.mcl.builder;

import java.util.function.Predicate;
import java.util.regex.Matcher;

public class Lexer {
    private final StringBuilder data = new StringBuilder();
    private Matcher value;
    private Token token;
    private boolean isFinished;
    private String error;

    public Lexer(String data) {
        data.lines().map(text -> text.replaceAll("^\s*#.*$", "")).filter(Predicate.not(String::isBlank)).forEachOrdered(this.data::append);
        next();
    }

    public void next() {
        if ((data.length() == 0 && (isFinished = true)) || isFinished) return;

        deleteUselessCharacters();

        if (hasNewToken()) return;

        isFinished = true;

        if (data.length() > 0) {
            error = "Unexpected symbol: '" + data.charAt(0) + "'";
        }
    }

    private void deleteUselessCharacters() {
        int toRemove = 0;

        while (isWhitespaceOrSemicolon(data.charAt(toRemove))) {
            toRemove++;
        }

        if (toRemove > 0) {
            data.delete(0, toRemove);
        }
    }

    private boolean isWhitespaceOrSemicolon(char c) {
        return Character.isWhitespace(c) || c == ';';
    }

    private boolean hasNewToken() {
        for (Token t : Token.values()) {
            Matcher matcher = t.matcher(data.toString());

            if (matcher != null) {
                token = t;
                value = matcher;
                data.delete(0, matcher.end());
                return true;
            }
        }

        return false;
    }

    public Token currentToken() {
        return token;
    }

    public Matcher currentValue() {
        return value;
    }

    public void throwError() {
        if (error != null) throw new RuntimeException("\u001B[31m" + error + "\u001B[0m");
    }

    public boolean canContinue() {
        return !isFinished;
    }
}