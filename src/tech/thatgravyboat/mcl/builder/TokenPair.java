package tech.thatgravyboat.mcl.builder;

public record TokenPair(String value, Token token) {


    @Override
    public String toString() {
        return "TokenPair{" +
                "value=" + value +
                ", token=" + token +
                '}';
    }
}
