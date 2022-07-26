package tech.thatgravyboat.mcl.builder;

public class ExpectTokenException extends RuntimeException {
    public ExpectTokenException(Token message) {
        super("Expected token " + message.name());
    }
}
