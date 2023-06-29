package tech.thatgravyboat.mcl.context.macros;

import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.mcl.builder.TokenPair;

public class MacroParameter {

    private final MacroParameterType type;
    @Nullable
    private final String defaultValue;

    public MacroParameter(MacroParameterType type, @Nullable String defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return defaultValue == null;
    }

    public String get(@Nullable TokenPair pair) {
        if (pair == null && defaultValue == null) {
            throw new RuntimeException("Expected token " + type + " but got nothing");
        }
        if (pair == null) {
            return defaultValue;
        }
        if (!type.test(pair)) {
            throw new RuntimeException("Expected token " + type + " but got " + pair.token());
        }
        return pair.value();
    }

}
