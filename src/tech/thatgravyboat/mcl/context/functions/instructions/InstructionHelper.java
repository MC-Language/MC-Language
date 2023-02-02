package tech.thatgravyboat.mcl.context.functions.instructions;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.ClassContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;

public class InstructionHelper {

    private static final Map<String, Creator> reserved = new HashMap<>();

    static {
        reserved.put("if", IfInstruction::of);
        reserved.put("for", ForInstruction::of);
    }

    public static List<Instruction> getInstructions(ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        List<Instruction> instructions = new ArrayList<>();
        assertToken(Token.OPEN_BRACKET, tokens);
        while (!isToken(Token.CLOSED_BRACKET, tokens)) {
            String id = assertIdentifier(tokens, false);
            instructions.add(InstructionHelper.get(instructions.size(), id, context, functionId, tokens));
        }
        return instructions;
    }

    public static Instruction get(int index, String id, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        Creator creator = reserved.getOrDefault(id, BasicFunctionInstruction::of);
        if (creator != null) {
            return creator.create(index, id, context, functionId, tokens);
        }
        throw new IllegalStateException("Reserved keyword " + id + " is not implemented! This should never happen");
    }

    @FunctionalInterface
    public interface Creator {

        Instruction create(int index, String id, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens);
    }
}
