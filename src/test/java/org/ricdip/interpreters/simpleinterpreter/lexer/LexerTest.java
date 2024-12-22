package org.ricdip.interpreters.simpleinterpreter.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.ricdip.interpreters.simpleinterpreter.token.Token;
import org.ricdip.interpreters.simpleinterpreter.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

class LexerTest {

    @Test
    void exceptionNextWithEmptyInput() {
        Lexer lexer = new Lexer("");

        Assertions.assertFalse(lexer.hasNext());
        Exception e = Assertions.assertThrows(NoSuchElementException.class, lexer::next);
        Assertions.assertEquals("already reached EOF", e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n ", "\n", "   ", ""})
    void whiteSpacesInput(String whiteSpaceString) {
        Lexer lexer = new Lexer(whiteSpaceString);

        Assertions.assertFalse(lexer.hasNext());
    }

    @ParameterizedTest
    @MethodSource("provideInput")
    void validInput(String inputString, List<Token> expectedTokens) {
        Lexer lexer = new Lexer(inputString);

        List<Token> result = new ArrayList<>();
        while (lexer.hasNext()) {
            result.add(lexer.next());
        }

        Assertions.assertEquals(expectedTokens, result);
    }

    private static Stream<Arguments> provideInput() {
        return Stream.of(
                Arguments.of("1+2*4/6", List.of(
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.ASTERISK, "*"),
                        new Token(TokenType.INT, "4"),
                        new Token(TokenType.SLASH, "/"),
                        new Token(TokenType.INT, "6")
                )),
                Arguments.of("   1  +  2  *  4  ", List.of(
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.ASTERISK, "*"),
                        new Token(TokenType.INT, "4")
                )),
                Arguments.of("1&2\n+3", List.of(
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.ILLEGAL, "&"),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INT, "3")
                )),
                Arguments.of("10+250 *  2505 ", List.of(
                        new Token(TokenType.INT, "10"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INT, "250"),
                        new Token(TokenType.ASTERISK, "*"),
                        new Token(TokenType.INT, "2505")
                )),
                Arguments.of("let test = 10;", List.of(
                        new Token(TokenType.LET, "let"),
                        new Token(TokenType.IDENTIFIER, "test"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.INT, "10"),
                        new Token(TokenType.ILLEGAL, ";")
                )),
                Arguments.of("let _abc = fn(x){x+1};", List.of(
                        new Token(TokenType.LET, "let"),
                        new Token(TokenType.IDENTIFIER, "_abc"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.FUNCTION, "fn"),
                        new Token(TokenType.LPAREN, "("),
                        new Token(TokenType.IDENTIFIER, "x"),
                        new Token(TokenType.RPAREN, ")"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.IDENTIFIER, "x"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.RBRACE, "}"),
                        new Token(TokenType.ILLEGAL, ";")
                )),
                Arguments.of("let _abc = fn(x, y){x+y}", List.of(
                        new Token(TokenType.LET, "let"),
                        new Token(TokenType.IDENTIFIER, "_abc"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.FUNCTION, "fn"),
                        new Token(TokenType.LPAREN, "("),
                        new Token(TokenType.IDENTIFIER, "x"),
                        new Token(TokenType.COMMA, ","),
                        new Token(TokenType.IDENTIFIER, "y"),
                        new Token(TokenType.RPAREN, ")"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.IDENTIFIER, "x"),
                        new Token(TokenType.PLUS, "+"),
                        new Token(TokenType.IDENTIFIER, "y"),
                        new Token(TokenType.RBRACE, "}")
                )),
                Arguments.of("< > = ! ; ", List.of(
                        new Token(TokenType.LT, "<"),
                        new Token(TokenType.GT, ">"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.NEG, "!"),
                        new Token(TokenType.ILLEGAL, ";")
                )),
                Arguments.of("<= >= == != < > = !", List.of(
                        new Token(TokenType.LTEQ, "<="),
                        new Token(TokenType.GTEQ, ">="),
                        new Token(TokenType.EQ, "=="),
                        new Token(TokenType.NEQ, "!="),
                        new Token(TokenType.LT, "<"),
                        new Token(TokenType.GT, ">"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.NEG, "!")
                )),
                Arguments.of("if(true) { 1 } else { 2 }", List.of(
                        new Token(TokenType.IF, "if"),
                        new Token(TokenType.LPAREN, "("),
                        new Token(TokenType.TRUE, "true"),
                        new Token(TokenType.RPAREN, ")"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.RBRACE, "}"),
                        new Token(TokenType.ELSE, "else"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.RBRACE, "}")
                )),
                Arguments.of("return 0", List.of(
                        new Token(TokenType.RETURN, "return"),
                        new Token(TokenType.INT, "0")
                )),
                Arguments.of("if(true) { return 0 } else { return 1 }", List.of(
                        new Token(TokenType.IF, "if"),
                        new Token(TokenType.LPAREN, "("),
                        new Token(TokenType.TRUE, "true"),
                        new Token(TokenType.RPAREN, ")"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.RETURN, "return"),
                        new Token(TokenType.INT, "0"),
                        new Token(TokenType.RBRACE, "}"),
                        new Token(TokenType.ELSE, "else"),
                        new Token(TokenType.LBRACE, "{"),
                        new Token(TokenType.RETURN, "return"),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.RBRACE, "}")
                )),
                Arguments.of("add(x, y)", List.of(
                        new Token(TokenType.IDENTIFIER, "add"),
                        new Token(TokenType.LPAREN, "("),
                        new Token(TokenType.IDENTIFIER, "x"),
                        new Token(TokenType.COMMA, ","),
                        new Token(TokenType.IDENTIFIER, "y"),
                        new Token(TokenType.RPAREN, ")")
                )),
                Arguments.of("[1,2]", List.of(
                        new Token(TokenType.LSQUARE, "["),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.COMMA, ","),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.RSQUARE, "]")
                )),
                Arguments.of("let a = [1,2]", List.of(
                        new Token(TokenType.LET, "let"),
                        new Token(TokenType.IDENTIFIER, "a"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.LSQUARE, "["),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.COMMA, ","),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.RSQUARE, "]")
                )),
                Arguments.of("a[1]", List.of(
                        new Token(TokenType.IDENTIFIER, "a"),
                        new Token(TokenType.LSQUARE, "["),
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.RSQUARE, "]")
                )),
                Arguments.of("\"test\"", List.of(
                        new Token(TokenType.STRING, "test")
                )),
                Arguments.of("\"hello world\"", List.of(
                        new Token(TokenType.STRING, "hello world")
                )),
                Arguments.of("\"  hello  world   \"", List.of(
                        new Token(TokenType.STRING, "  hello  world   ")
                )),
                Arguments.of("1 \" hello world  \" 2 3", List.of(
                        new Token(TokenType.INT, "1"),
                        new Token(TokenType.STRING, " hello world  "),
                        new Token(TokenType.INT, "2"),
                        new Token(TokenType.INT, "3")
                )),
                Arguments.of("let a = \"test\"", List.of(
                        new Token(TokenType.LET, "let"),
                        new Token(TokenType.IDENTIFIER, "a"),
                        new Token(TokenType.ASSIGN, "="),
                        new Token(TokenType.STRING, "test")
                ))
        );
    }
}