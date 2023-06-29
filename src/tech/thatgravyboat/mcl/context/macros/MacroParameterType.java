package tech.thatgravyboat.mcl.context.macros;

import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;

import java.util.function.Predicate;

public enum MacroParameterType implements Predicate<TokenPair> {
    STRING("string",
            pair -> pair.token() == Token.STRING
    ),
    INTEGER("int",
            pair -> pair.token() == Token.INTEGER
    ),
    FLOAT("float",
            pair -> pair.token() == Token.FLOAT
    ),
    BOOLEAN("boolean",
            pair -> pair.token() == Token.IDENTIFIER && ("true".equalsIgnoreCase(pair.value()) || "false".equalsIgnoreCase(pair.value()))
    );

    private final String id;
    private final Predicate<TokenPair> tester;

    MacroParameterType(String id, Predicate<TokenPair> tester) {
        this.id = id;
        this.tester = tester;
    }

    @Override
    public boolean test(TokenPair pair) {
        return this.tester.test(pair);
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
