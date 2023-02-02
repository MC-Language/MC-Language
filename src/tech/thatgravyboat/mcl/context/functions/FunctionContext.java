package tech.thatgravyboat.mcl.context.functions;

import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;
import tech.thatgravyboat.mcl.context.functions.instructions.Instruction;
import tech.thatgravyboat.mcl.context.functions.instructions.InstructionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertIdentifier;
import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertToken;

public record FunctionContext(@Nullable String event, String id, ClassContext context, List<Instruction> instructions) {

    public static FunctionContext of(ClassContext context, PeekableIterator<TokenPair> tokens) {
        String id = assertIdentifier(tokens, true);
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        return new FunctionContext(null, id, context, InstructionHelper.getInstructions(context, id, tokens));
    }

    public static FunctionContext eventOf(ClassContext context, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        String eventId = assertToken(Token.STRING, tokens).group(1);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        assertToken(Token.FUNCTION, tokens);
        String id = assertIdentifier(tokens, true);
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        return new FunctionContext(eventId, id, context, InstructionHelper.getInstructions(context, id, tokens));
    }

    private String getOutput(Map<ClassContext, ClassContent> context) {
        return instructions.stream()
            .map(instruction -> instruction.getOutput(context))
            .collect(Collectors.joining("\n"));
    }

    public List<FileContent> getFileOutput(Map<ClassContext, ClassContent> context) {
        List<FileContent> output = new ArrayList<>();
        output.add(new FileContent(this.context, id, getOutput(context)));
        instructions.forEach(instruction -> output.addAll(instruction.getAdditionalFileOutput(context)));
        return output;
    }
}
