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

public record ForInstruction(ClassContext context, String id, int start, int end, List<Instruction> instructions) implements Instruction {

    public static ForInstruction of(int index, String ignoredId, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_PARENTHESIS, tokens);
        int start = Integer.parseInt(assertToken(Token.INTEGER, tokens));
        assertToken(Token.PERIOD, tokens);
        assertToken(Token.PERIOD, tokens);
        int end = Integer.parseInt(assertToken(Token.INTEGER, tokens));
        assertToken(Token.CLOSED_PARENTHESIS, tokens);
        List<Instruction> instructions = InstructionHelper.getInstructions(context, functionId + "_for_" + index, tokens);
        return new ForInstruction(context, functionId + "_for_" + index, start, end, instructions);
    }

    @Override
    public String getOutput(Map<ClassContext, ClassContent> context) {
        String output = "scoreboard objectives add mclang.int dummy";
        output += "\nscoreboard players set $for mclang.int " + start;
        output += "\nscoreboard players set $for_end mclang.int " + end;
        output += "\nfunction " + this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id + "_loop";
        return output;
    }

    @Override
    public List<FileContent> getAdditionalFileOutput(Map<ClassContext, ClassContent> context) {
        List<FileContent> files = new ArrayList<>();
        String instructionOutput = instructions.stream()
                .map(instruction -> instruction.getOutput(context))
                .collect(Collectors.joining("\n"));
        if (start > end) {
            instructionOutput += "\nscoreboard players remove $for mclang.int 1";
        } else {
            instructionOutput += "\nscoreboard players add $for mclang.int 1";
        }
        instructionOutput += "\nfunction " + this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id + "_loop";
        FileContent instructionFile = new FileContent(this.context, this.id, instructionOutput);
        files.add(instructionFile);
        instructions.forEach(instruction -> files.addAll(instruction.getAdditionalFileOutput(context)));
        String output = "";
        if (start > end) {
            output += "execute if score $for mclang.int > $for_end mclang.int run function " + this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id;
        } else {
            output += "execute if score $for mclang.int < $for_end mclang.int run function " + this.context.pPackage() + ":" + this.context.pClass() + "/" + this.id;
        }
        files.add(new FileContent(this.context, this.id + "_loop", output));
        return files;
    }
}
