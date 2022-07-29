package tech.thatgravyboat.mcl.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {
    PACKAGE("package"),
    FUNC("func "),
    LOAD("@Load"),
    TICK("@Tick"),
    TYPE_TICK("@TypeTick *\\("),
    SELECTOR_TICK("@SelectTick *\\("),
    REPEAT("repeat *\\("),
    RUN("run *\\("),
    IF("if *\\("),
    NOTIF("notif *\\("),
    ELSE("else *\\{"),
    OPEN_PARENTHESIS("\\("),
    CLOSED_PARENTHESIS("\\)"),
    OPEN_BRACKET("\\{"),
    CLOSED_BRACKET("\\}"),
    COMMA(","),
    PERIOD("\\."),

    STRING("`[^`]+`"),
    INTEGER("\\d+"),
    IDENTIFIER("[a-z_]+");

    private final Pattern regex;


    Token(String regex) {
        this.regex = Pattern.compile("^" + regex);
    }

    int endOfMatch(String s) {
        Matcher m = regex.matcher(s);
        return m.find() ? m.end() : -1;
    }
}
