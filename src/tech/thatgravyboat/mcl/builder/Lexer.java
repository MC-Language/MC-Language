package tech.thatgravyboat.mcl.builder;

public class Lexer {
    private final StringBuilder data = new StringBuilder();
    private String value;
    private Token token;
    private boolean isFinished;
    private String error;

    public Lexer(String data) {
        data.lines().forEachOrdered(this.data::append);
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
            int end = t.endOfMatch(data.toString());

            if (end != -1) {
                token = t;
                value = data.substring(0, end);
                data.delete(0, end);
                return true;
            }
        }

        return false;
    }

    public Token currentToken() {
        return token;
    }

    public String currentValue() {
        return value;
    }

    public void throwError() {
        if (error != null) throw new RuntimeException("\u001B[31m" + error + "\u001B[0m");
    }

    public boolean canContinue() {
        return !isFinished;
    }
}