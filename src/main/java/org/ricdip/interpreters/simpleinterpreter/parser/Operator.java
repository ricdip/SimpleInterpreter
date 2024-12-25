package org.ricdip.interpreters.simpleinterpreter.parser;

import lombok.Getter;
import org.ricdip.interpreters.simpleinterpreter.symbol.Symbol;
import org.ricdip.interpreters.simpleinterpreter.token.TokenType;

import java.util.Optional;

@Getter
public enum Operator {
    PLUS(TokenType.PLUS, Symbol.PLUS),
    MINUS(TokenType.MINUS, Symbol.MINUS),
    ASTERISK(TokenType.ASTERISK, Symbol.ASTERISK),
    SLASH(TokenType.SLASH, Symbol.SLASH),
    NEG(TokenType.NEG, Symbol.NEG),
    LT(TokenType.LT, Symbol.LT),
    GT(TokenType.GT, Symbol.GT),
    EQ(TokenType.EQ, Symbol.EQUAL + String.valueOf(Symbol.EQUAL)),
    NEQ(TokenType.NEQ, Symbol.NEG + String.valueOf(Symbol.EQUAL)),
    LTEQ(TokenType.LTEQ, Symbol.LT + String.valueOf(Symbol.EQUAL)),
    GTEQ(TokenType.GTEQ, Symbol.GT + String.valueOf(Symbol.EQUAL)),
    CALL(TokenType.LPAREN, Symbol.LPAREN),
    INDEX(TokenType.LSQUARE, Symbol.LSQUARE),
    INCREMENT(TokenType.INCREMENT, Symbol.PLUS + String.valueOf(Symbol.PLUS)),
    DECREMENT(TokenType.DECREMENT, Symbol.MINUS + String.valueOf(Symbol.MINUS));

    private final TokenType tokenType;
    private final String symbols;

    Operator(TokenType tokenType, char symbol) {
        this.tokenType = tokenType;
        this.symbols = String.valueOf(symbol);
    }

    Operator(TokenType tokenType, String symbols) {
        this.tokenType = tokenType;
        this.symbols = String.valueOf(symbols);
    }

    public static Optional<Operator> fromToken(TokenType tokenType) {
        return switch (tokenType) {
            case PLUS -> Optional.of(Operator.PLUS);
            case MINUS -> Optional.of(Operator.MINUS);
            case ASTERISK -> Optional.of(Operator.ASTERISK);
            case SLASH -> Optional.of(Operator.SLASH);
            case NEG -> Optional.of(Operator.NEG);
            case LT -> Optional.of(Operator.LT);
            case GT -> Optional.of(Operator.GT);
            case EQ -> Optional.of(Operator.EQ);
            case NEQ -> Optional.of(Operator.NEQ);
            case LTEQ -> Optional.of(Operator.LTEQ);
            case GTEQ -> Optional.of(Operator.GTEQ);
            case INCREMENT -> Optional.of(Operator.INCREMENT);
            case DECREMENT -> Optional.of(Operator.DECREMENT);
            default -> Optional.empty();
        };
    }
}
