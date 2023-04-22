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

import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertToken;

public record WhileInstruction(ClassContext context, String id, String statement, List<Instruction> instructions) implements Instruction {

    public static WhileInstruction of(int index, String ignoredId, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        String statement = assertToken(Token.STRING, tokens).group(1);
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        List<Instruction> instructions = InstructionHelper.getInstructions(context, functionId + "_while_" + index, tokens);
        return new WhileInstruction(context, functionId + "_while_" + index, statement, instructions);
    }

    @Override
    public String getOutput(Map<ClassContext, ClassContent> context) {
        return getRunStatement();
    }

    @Override
    public List<FileContent> getAdditionalFileOutput(Map<ClassContext, ClassContent> context) {
        List<FileContent> files = new ArrayList<>();
        String instructionOutput = instructions.stream()
                .map(instruction -> instruction.getOutput(context))
                .collect(Collectors.joining("\n"));
        instructionOutput += "\n" + getRunStatement();
        FileContent instructionFile = new FileContent(this.context, this.id, instructionOutput);
        files.add(instructionFile);
        instructions.forEach(instruction -> files.addAll(instruction.getAdditionalFileOutput(context)));
        return files;
    }

    private String getRunStatement() {
        return "execute if " + statement + " run function " + this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id;
    }
}
