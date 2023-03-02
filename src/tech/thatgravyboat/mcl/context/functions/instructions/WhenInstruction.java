package tech.thatgravyboat.mcl.context.functions.instructions;

import tech.thatgravyboat.mcl.builder.PeekableIterator;
import tech.thatgravyboat.mcl.builder.Token;
import tech.thatgravyboat.mcl.builder.TokenPair;
import tech.thatgravyboat.mcl.context.ClassContent;
import tech.thatgravyboat.mcl.context.ClassContext;
import tech.thatgravyboat.mcl.context.FileContent;
import tech.thatgravyboat.mcl.utils.Pair;
import tech.thatgravyboat.mcl.utils.PairList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertIdentifier;
import static tech.thatgravyboat.mcl.builder.TokenAssertion.assertToken;

public record WhenInstruction(ClassContext context, String id, PairList<String, Pair<String, List<Instruction>>> statements) implements Instruction {

    public static WhenInstruction of(int index, String ignoredId, ClassContext context, String functionId, PeekableIterator<TokenPair> tokens) {
        assertToken(Token.OPEN_BRACKET, tokens);
        String id = functionId + "_when_" + index;
        PairList<String, Pair<String, List<Instruction>>> statements = new PairList<>();
        while (tokens.peek().token() != Token.CLOSED_BRACKET) {
            int whenIndex = statements.size();
            var cased = parseCase(whenIndex, id, context, tokens);
            statements.add(id + "_" + whenIndex, cased);
        }
        assertToken(Token.CLOSED_BRACKET, tokens);
        return new WhenInstruction(context, id, statements);
    }

    private static Pair<String, List<Instruction>> parseCase(int whenIndex, String id, ClassContext context, PeekableIterator<TokenPair> tokens) {
        String s = assertIdentifier(tokens, true, "Expected case keyword.");
        if (!s.equals("case")) throw new RuntimeException("Expected case keyword but got: " + s);
        String statement = assertToken(Token.STRING, tokens).group(1);
        assertToken(Token.COLON, tokens);
        List<Instruction> instructions = InstructionHelper.getInstructions(context, id + "_" + whenIndex, tokens);
        return new Pair<>(statement, instructions);
    }

    @Override
    public String getOutput(Map<ClassContext, ClassContent> context) {
        String output = "scoreboard objectives add mclang.int dummy";
        output += "\nscoreboard players set $" + id + " mclang.int 0";
        output += "\nscoreboard players set $false mclang.int 0";
        for (var statement : statements) {
            var value = statement.b();
            var state = value.a();
            output += "\nexecute if score %s mclang.int = $false mclang.int if %s run function %s".formatted(
                    id, state, this.context.pPackage() + ":" + this.context.pClass() + "/" + statement.a());
        }
        return output;
    }

    @Override
    public List<FileContent> getAdditionalFileOutput(Map<ClassContext, ClassContent> context) {
        List<FileContent> files = new ArrayList<>();
        for (var value : statements) {
            String content = value.b().b().stream()
                    .map(instruction -> instruction.getOutput(context))
                    .collect(Collectors.joining("\n"));
            String finalContent = "scoreboard players set $" + id + " mclang.int 1\n" + content;
            files.add(new FileContent(this.context, value.a(), finalContent));
        }
        return files;
    }
}
