package org.ricdip.interpreters.simpleinterpreter.lexer;

import lombok.NonNull;
import org.ricdip.interpreters.simpleinterpreter.symbol.Keyword;
import org.ricdip.interpreters.simpleinterpreter.symbol.Symbol;
import org.ricdip.interpreters.simpleinterpreter.token.Token;
import org.ricdip.interpreters.simpleinterpreter.token.TokenType;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Lexer implements Iterator<Token> {
    private final String input;
    private char currentChar;
    private char peekChar;
    private int peekPosition;

    public Lexer(@NonNull String input) {
        // remove leading and trailing white space from input
        this.input = input.strip();
        this.peekPosition = -1;

        // prepare lexer before starting
        readCharSkipWhitespace();
    }

    private void readChar(boolean skipWhitespaces) {
        peekPosition += 1;

        if (peekChar == Symbol.EOF && peekPosition >= input.length()) {
            return;
        }

        if (peekPosition >= input.length()) {
            currentChar = peekChar;
            peekChar = Symbol.EOF;
        } else {
            currentChar = peekChar;
            peekChar = input.charAt(peekPosition);
        }

        if (Character.isWhitespace(currentChar) && skipWhitespaces) {
            readChar(skipWhitespaces);
        }
    }

    private void readCharSkipWhitespace() {
        readChar(true);
    }

    private void readCharNoSkipWhitespace() {
        readChar(false);
    }

    @Override
    public boolean hasNext() {
        return peekChar != Symbol.EOF;
    }

    @Override
    public Token next() {
        if (hasNext()) {
            readCharSkipWhitespace();
            Token token = null;

            switch (currentChar) {
                // operators
                case Symbol.PLUS:
                    token = new Token(TokenType.PLUS, String.valueOf(currentChar));
                    break;
                case Symbol.MINUS:
                    token = new Token(TokenType.MINUS, String.valueOf(currentChar));
                    break;
                case Symbol.ASTERISK:
                    token = new Token(TokenType.ASTERISK, String.valueOf(currentChar));
                    break;
                case Symbol.SLASH:
                    token = new Token(TokenType.SLASH, String.valueOf(currentChar));
                    break;
                case Symbol.EQUAL:
                    if (peekChar == Symbol.EQUAL) {
                        // ==
                        token = new Token(TokenType.EQ, String.valueOf(currentChar) + peekChar);
                        readCharSkipWhitespace();
                    } else {
                        // =
                        token = new Token(TokenType.ASSIGN, String.valueOf(currentChar));
                    }
                    break;
                case Symbol.NEG:
                    if (peekChar == Symbol.EQUAL) {
                        // !=
                        token = new Token(TokenType.NEQ, String.valueOf(currentChar) + peekChar);
                        readCharSkipWhitespace();
                    } else {
                        // !
                        token = new Token(TokenType.NEG, String.valueOf(currentChar));
                    }
                    break;
                case Symbol.LT:
                    if (peekChar == Symbol.EQUAL) {
                        // <=
                        token = new Token(TokenType.LTEQ, String.valueOf(currentChar) + peekChar);
                        readCharSkipWhitespace();
                    } else {
                        // <
                        token = new Token(TokenType.LT, String.valueOf(currentChar));
                    }
                    break;
                case Symbol.GT:
                    if (peekChar == Symbol.EQUAL) {
                        // >=
                        token = new Token(TokenType.GTEQ, String.valueOf(currentChar) + peekChar);
                        readCharSkipWhitespace();
                    } else {
                        // >
                        token = new Token(TokenType.GT, String.valueOf(currentChar));
                    }
                    break;
                // parenthesis
                case Symbol.LPAREN:
                    token = new Token(TokenType.LPAREN, String.valueOf(currentChar));
                    break;
                case Symbol.RPAREN:
                    token = new Token(TokenType.RPAREN, String.valueOf(currentChar));
                    break;
                case Symbol.LSQUARE:
                    token = new Token(TokenType.LSQUARE, String.valueOf(currentChar));
                    break;
                case Symbol.RSQUARE:
                    token = new Token(TokenType.RSQUARE, String.valueOf(currentChar));
                    break;
                case Symbol.LBRACE:
                    token = new Token(TokenType.LBRACE, String.valueOf(currentChar));
                    break;
                case Symbol.RBRACE:
                    token = new Token(TokenType.RBRACE, String.valueOf(currentChar));
                    break;
                // others
                case Symbol.COMMA:
                    token = new Token(TokenType.COMMA, String.valueOf(currentChar));
                    break;
                case Symbol.DQUOTE:
                    token = readString();
                    break;
                case Symbol.EOF:
                    token = new Token(TokenType.EOF, String.valueOf(currentChar));
                    break;
                default:
                    if (Character.isDigit(currentChar)) {
                        // integers
                        return readInteger();
                    } else if (isValidStartCharacter(currentChar)) {
                        // identifiers
                        return readIdentifier();
                    } else {
                        // not valid characters
                        return new Token(TokenType.ILLEGAL, String.valueOf(currentChar));
                    }
            }

            return token;
        } else {
            throw new NoSuchElementException("already reached EOF");
        }
    }

    private Token readInteger() {
        StringBuilder intLexeme = new StringBuilder();
        intLexeme.append(currentChar);

        while (Character.isDigit(peekChar)) {
            readCharSkipWhitespace();
            intLexeme.append(currentChar);
        }

        return new Token(TokenType.INT, intLexeme.toString());
    }

    private Token readIdentifier() {
        StringBuilder identifierLexeme = new StringBuilder();
        identifierLexeme.append(currentChar);

        while (isValidMiddleCharacter(peekChar)) {
            readCharSkipWhitespace();
            identifierLexeme.append(currentChar);
        }

        String identifier = identifierLexeme.toString();
        TokenType tokenType = Keyword.identify(identifier);

        return new Token(tokenType, identifier);
    }

    private boolean isValidStartCharacter(char character) {
        return Character.isLetter(character) || (character == Symbol.UNDERSCORE);
    }

    private boolean isValidMiddleCharacter(char character) {
        return isValidStartCharacter(character) || Character.isDigit(character);
    }

    private Token readString() {
        readCharNoSkipWhitespace(); // " -> string
        StringBuilder stringLexeme = new StringBuilder();

        while (currentChar != Symbol.DQUOTE && peekChar != Symbol.EOF) {
            stringLexeme.append(currentChar);
            readCharNoSkipWhitespace();
        }

        // string -> "

        if (currentChar == Symbol.DQUOTE) {
            return new Token(TokenType.STRING, stringLexeme.toString());
        } else {
            return new Token(TokenType.ILLEGAL, stringLexeme.toString());
        }
    }
}
