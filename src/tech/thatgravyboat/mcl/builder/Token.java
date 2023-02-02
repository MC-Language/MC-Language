package tech.thatgravyboat.mcl.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {
    CLASS("class"),
    PACKAGE("package"),
    IMPORT("import"),
    FUNCTION("func"),
    MARCO("macro"),
    EVENT("@Event"),

    OPEN_PARENTHESIS("\\("),
    CLOSED_PARENTHESIS("\\)"),
    OPEN_BRACKET("\\{"),
    CLOSED_BRACKET("\\}"),
    OPEN_SQUARE_BRACKET("\\["),
    CLOSED_SQUARE_BRACKET("\\]"),

    COMMA(","),
    PERIOD("\\."),
    COLON(":"),
    EQUALS("="),
    SEMICOLON(";"),

    TYPE_RETURN("->"),

    STRING("\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\""),
    FLOAT("-?[0-9]*[.][0-9]+"),
    INTEGER("-?\\d+"),
    IDENTIFIER("[a-zA-Z_]+");

    private final Pattern regex;


    Token(String regex) {
        this.regex = Pattern.compile("^" + regex);
    }

    public Matcher matcher(String input) {
        var matcher = regex.matcher(input);
        return matcher.find() ? matcher : null;
    }

    public int endOfMatch(String s) {
        Matcher m = regex.matcher(s);
        return m.find() ? m.end() : -1;
    }

    public String getContent(Matcher matcher) {
        return switch (this) {
            case STRING -> matcher.group(1);
            default -> matcher.group();
        };
    }
}
