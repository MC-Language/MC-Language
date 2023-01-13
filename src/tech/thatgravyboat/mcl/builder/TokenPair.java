package tech.thatgravyboat.mcl.builder;

import java.util.regex.Matcher;

public record TokenPair(Matcher value, Token token) {


    @Override
    public String toString() {
        return "TokenPair{" +
                "value=" + value.group() +
                ", token=" + token +
                '}';
    }
}
