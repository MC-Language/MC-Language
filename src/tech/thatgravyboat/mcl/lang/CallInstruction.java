package tech.thatgravyboat.mcl.lang;

import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.MethodContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public record CallInstruction(MethodContext context, String method, List<TokenPair> args) implements FuncInstruction {

    @Override
    public String replacement() {
        return null;
    }

    @Override
    public String toString() {
        return "CallInstruction{method='" + method + "'}";
    }

}
