package tech.thatgravyboat.mcl.context.macros;

import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;

import java.util.function.Function;
import java.util.function.Predicate;

public enum MacroParameterType implements Predicate<TokenPair> {
    STRING("string",
            Token.STRING,
            pair -> pair.token() == Token.STRING,
            pair -> pair.value().group(1)
    ),
    INTEGER("int",
            Token.INTEGER,
            pair -> pair.token() == Token.INTEGER,
            pair -> pair.value().group()
    ),
    FLOAT("float",
            Token.FLOAT,
            pair -> pair.token() == Token.FLOAT,
            pair -> pair.value().group()
    ),
    BOOLEAN("boolean",
            Token.IDENTIFIER,
            pair -> pair.token() == Token.IDENTIFIER && ("true".equalsIgnoreCase(pair.value().group()) || "false".equalsIgnoreCase(pair.value().group())),
            pair -> pair.value().group()
    );

    private final String id;
    private final Token token;
    private final Predicate<TokenPair> tester;
    private final Function<TokenPair, String> getter;

    MacroParameterType(String id, Token token, Predicate<TokenPair> tester, Function<TokenPair, String> getter) {
        this.id = id;
        this.token = token;
        this.tester = tester;
        this.getter = getter;
    }

    @Override
    public boolean test(TokenPair pair) {
        return this.tester.test(pair);
    }

    public String get(TokenPair pair) {
        return this.getter.apply(pair);
    }

    public static MacroParameterType fromId(String id) {
        for (var type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown parameter type " + id);
    }
}
