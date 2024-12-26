package org.ricdip.interpreters.simpleinterpreter.symbol;

import org.ricdip.interpreters.simpleinterpreter.token.TokenType;

import java.util.HashMap;
import java.util.Map;

public final class Keyword {
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("let", TokenType.LET);
        keywords.put("fn", TokenType.FUNCTION);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("while", TokenType.WHILE);
    }

    public static TokenType identify(String identifier) {
        return keywords.getOrDefault(identifier, TokenType.IDENTIFIER);
    }
}
