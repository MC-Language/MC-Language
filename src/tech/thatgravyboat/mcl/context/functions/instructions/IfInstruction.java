package tech.thatgravyboat.mcl.context.functions.instructions;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.*;

public record IfInstruction(ClassContext context, String id, String statement, List<Instruction> instructions, List<Instruction> elseInstructions) implements Instruction {

    public static IfInstruction of(int index, String ignoredId, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        String statement = assertToken(Token.STRING, tokens).group(1);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        List<Instruction> instructions = InstructionHelper.getInstructions(context, functionId + "_if_" + index, tokens);
        TokenPair pair = tokens.peek();
        if (pair.token() == Token.IDENTIFIER && pair.value().group().equals("else")) {
            assertToken(Token.IDENTIFIER, tokens);
            List<Instruction> elseInstructions = InstructionHelper.getInstructions(context, functionId + "_if_" + index + "_else", tokens);
            return new IfInstruction(context, functionId + "_if_" + index, statement, instructions, elseInstructions);
        }
        return new IfInstruction(context, functionId + "_if_" + index, statement, instructions, List.of());
    }

    @Override
    public String getOutput(Map<ClassContext, ClassContent> context) {
        String output = "execute if %s run function %s".formatted(statement, this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id);
        if (!elseInstructions.isEmpty()) {
            output += "\nexecute unless %s run function %s".formatted(statement, this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id + "_else");
        }
        return output;
    }

    @Override
    public List<FileContent> getAdditionalFileOutput(Map<ClassContext, ClassContent> context) {
        List<FileContent> files = new ArrayList<>();
        String ifOutput = instructions.stream()
            .map(instruction -> instruction.getOutput(context))
            .collect(Collectors.joining("\n"));
        files.add(new FileContent(this.context, this.id, ifOutput));
        if (!elseInstructions.isEmpty()) {
            String elseOutput = elseInstructions.stream()
                .map(instruction -> instruction.getOutput(context))
                .collect(Collectors.joining("\n"));
            files.add(new FileContent(this.context, this.id + "_else", elseOutput));
        }
        instructions.forEach(instruction -> files.addAll(instruction.getAdditionalFileOutput(context)));
        elseInstructions.forEach(instruction -> files.addAll(instruction.getAdditionalFileOutput(context)));
        return files;
    }
}
