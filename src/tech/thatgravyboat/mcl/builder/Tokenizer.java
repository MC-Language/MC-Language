package tech.thatgravyboat.mcl.builder;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<TokenPair> tokenize(String input) {
        Lexer lexer = new Lexer(input);
        List<TokenPair> tokens = new ArrayList<>();
        while (lexer.canContinue()) {
            tokens.add(new TokenPair(lexer.currentValue(), lexer.currentToken()));
            lexer.next();
        }
        lexer.throwError();
        return tokens;
    }

}
