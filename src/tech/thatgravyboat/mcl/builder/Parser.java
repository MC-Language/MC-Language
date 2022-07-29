package tech.thatgravyboat.mcl.builder;

import tech.thatgravyboat.mcl.lang.Package;
import tech.thatgravyboat.mcl.lang.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Parser {

    public static Package parse(String project, List<TokenPair> tokens) {
        if (tokens.isEmpty()) throw new RuntimeException("Package was empty.");
        PeekableIterator<TokenPair> iterator = new PeekableIterator<>(tokens.iterator());
        expectOrThrow(iterator, Token.PACKAGE);
        return parseClass(project, iterator);
    }

    private static McFunctionData parseToken(String project, String packageId, String path, PeekableIterator<TokenPair> iterator) {
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Empty tokens list");
        }

        var token = iterator.next();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Empty tokens list");
        }
        return switch (token.token()) {
            case REPEAT -> parseRepeat(project, packageId, path, iterator);
            case RUN -> parseRun(iterator);
            case NOTIF, IF -> parseIf(token.token() == Token.NOTIF, project, packageId, path, iterator);
            case IDENTIFIER -> parseInternalFunction(token, project, packageId, iterator);
            default -> throw new IllegalArgumentException("Unknown token");
        };
    }

    private static Package parseClass(String project, PeekableIterator<TokenPair> iterator) {
        TokenPair id = iterator.next();
        expectOrThrow(id, Token.IDENTIFIER);
        expectOrThrow(iterator, Token.OPEN_BRACKET);

        Map<String, McFunction> functions = new LinkedHashMap<>();
        while (iterator.hasNext() && iterator.peek().token() != Token.CLOSED_BRACKET) {
            String tick = getTick(iterator);
            boolean load = getLoad(iterator);
            expectOrThrow(iterator, Token.FUNC);
            McFunction function = parseFunc(project, load, tick, id.value(), iterator);
            if (functions.put(function.name(), function) != null) throw new IllegalArgumentException("Duplicate function " + function.name());
        }
        expectOrThrow(iterator, Token.CLOSED_BRACKET);
        return new Package(id.value(), functions.values());
    }

    private static McFunction parseFunc(String project, boolean load, String tickId, String packageId, PeekableIterator<TokenPair> iterator) {
        TokenPair id = iterator.next();
        expectOrThrow(id, Token.IDENTIFIER);
        expectOrThrow(iterator, Token.OPEN_PARENTHESIS);
        expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
        expectOrThrow(iterator, Token.OPEN_BRACKET);

        List<McFunctionData> funcs = new ArrayList<>();
        while (iterator.hasNext() && iterator.peek().token() != Token.CLOSED_BRACKET) {
            funcs.add(parseToken(project, packageId, packageId + "/" + id.value() + "/" + funcs.size(), iterator));
        }
        expectOrThrow(iterator, Token.CLOSED_BRACKET);
        return new McFunction(load, tickId, packageId, id.value(), funcs);
    }

    private static McRepeat parseRepeat(String project, String packageid, String path, PeekableIterator<TokenPair> iterator) {
        String amount = parenthesis(iterator, Token.INTEGER);
        expectOrThrow(iterator, Token.OPEN_BRACKET);
        List<McFunctionData> funcs = new ArrayList<>();
        while (iterator.hasNext() && iterator.peek().token() != Token.CLOSED_BRACKET) {
            funcs.add(parseToken(project, packageid, path + "/repeat/" + funcs.size(), iterator));
        }
        expectOrThrow(iterator, Token.CLOSED_BRACKET);
        return new McRepeat(project, path, Integer.parseInt(amount), funcs);
    }

    private static McFunctionData parseIf(boolean not, String project, String packageId, String path, PeekableIterator<TokenPair> iterator) {
        String statement = parenthesis(iterator, Token.STRING);
        expectOrThrow(iterator, Token.OPEN_BRACKET);
        List<McFunctionData> funcs = new ArrayList<>();
        while (iterator.hasNext() && iterator.peek().token() != Token.CLOSED_BRACKET) {
            funcs.add(parseToken(project, packageId, path + "/if/" + funcs.size(), iterator));
        }
        expectOrThrow(iterator, Token.CLOSED_BRACKET);
        if (nextMatch(iterator, Token.ELSE::equals)) {
            List<McFunctionData> elseFuncs = new ArrayList<>();
            while (iterator.hasNext() && iterator.peek().token() != Token.CLOSED_BRACKET) {
                elseFuncs.add(parseToken(project, packageId, path + "/else/" + elseFuncs.size(), iterator));
            }
            expectOrThrow(iterator, Token.CLOSED_BRACKET);
            return new McIfElse(not, project, path, statement.substring(1, statement.length() - 1), funcs, elseFuncs);
        }
        return new McIf(not, project, path, statement.substring(1, statement.length() - 1), funcs);
    }

    private static McInternalFunction parseInternalFunction(TokenPair token, String project, String packageId, PeekableIterator<TokenPair> iterator) {
        switch (iterator.next().token()) {
            case PERIOD -> {
                var next = iterator.next();
                expectOrThrow(next, Token.IDENTIFIER);
                expectOrThrow(iterator, Token.OPEN_PARENTHESIS);
                expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
                return new McInternalFunction(project, token.value(), next.value());
            }
            case OPEN_PARENTHESIS -> {
                expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
                return new McInternalFunction(project, packageId, token.value());
            }
            default -> throw new ExpectTokenException(Token.OPEN_PARENTHESIS);
        }
    }

    private static McRun parseRun(PeekableIterator<TokenPair> iterator) {
        String data = parenthesis(iterator, Token.STRING);
        return new McRun(data.substring(1, data.length() - 1));
    }

    private static boolean getLoad(PeekableIterator<TokenPair> iterator) {
        if (nextMatch(iterator, Token.LOAD::equals)) {
            if (iterator.peek().token() == Token.OPEN_PARENTHESIS) {
                expectOrThrow(iterator, Token.OPEN_PARENTHESIS);
                expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
            }
            return true;
        }
        return false;
    }

    private static String getTick(PeekableIterator<TokenPair> iterator) {
        if (nextMatch(iterator, Token.TICK::equals)) {
            if (iterator.peek().token() == Token.OPEN_PARENTHESIS) {
                expectOrThrow(iterator, Token.OPEN_PARENTHESIS);
                expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
            }
            return "";
        }
        Token token = iterator.peek().token();
        if (token == Token.TYPE_TICK || token == Token.SELECTOR_TICK) {
            iterator.next();
            String data = parenthesis(iterator, Token.STRING);
            data = data.substring(1, data.length() - 1);
            return token == Token.TYPE_TICK ? "type=" + data : data;
        }
        return null;
    }

    private static String parenthesis(PeekableIterator<TokenPair> iterator, Token token) {
        TokenPair data = iterator.next();
        expectOrThrow(data, token);
        expectOrThrow(iterator, Token.CLOSED_PARENTHESIS);
        return data.value();
    }

    private static void expectOrThrow(PeekableIterator<TokenPair> iterator, Token token) {
        expectOrThrow(iterator.next(), token);
    }

    private static void expectOrThrow(TokenPair pair, Token token) {
        if (pair.token() != token) throw new ExpectTokenException(token);
    }

    private static boolean nextMatch(PeekableIterator<TokenPair> iterator, Predicate<Token> token) {
        if (token.test(iterator.peek().token())) {
            iterator.next();
            return true;
        }
        return false;
    }

}
