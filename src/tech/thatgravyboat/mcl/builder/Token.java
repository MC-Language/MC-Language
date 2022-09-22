package tech.thatgravyboat.mcl.builder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Token {
    SERVICE("@Service"), // @Service("name")
    IF("if"), // if (condition) { }
    NOT_IF("notif"), // notif (condition) { }
    ELSE("else"), // else { }
    ECHO("echo"), // echo "message"
    OPEN_PARENTHESIS("\\("),
    CLOSED_PARENTHESIS("\\)"),
    OPEN_BRACKET("\\{"),
    CLOSED_BRACKET("\\}"),
    COMMA(","),
    PERIOD("\\."),

    COMPARISON_OPERATOR("==|!=|<=|>=|<|>"),
    AND("&&"),

    DEFINE(":="),
    ASSIGNMENT("="),
    PLUS_ASSIGNMENT("\\+="),
    MINUS_ASSIGNMENT("-="),
    MULTIPLY_ASSIGNMENT("\\*="),
    DIVIDE_ASSIGNMENT("/="),
    MODULO_ASSIGNMENT("%="),

    STRING("\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\""),
    STATEMENT("`([^`]*)`"),
    INTEGER("\\d+"),
    IDENTIFIER("[a-z_]+");

    private final Pattern regex;


    Token(String regex) {
        this.regex = Pattern.compile("^" + regex);
    }

    Matcher matcher(String input) {
        var matcher = regex.matcher(input);
        return matcher.find() ? matcher : null;
    }

    int endOfMatch(String s) {
        Matcher m = regex.matcher(s);
        return m.find() ? m.end() : -1;
    }
}
