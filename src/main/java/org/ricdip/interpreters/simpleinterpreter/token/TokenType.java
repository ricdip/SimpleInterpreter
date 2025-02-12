package org.ricdip.interpreters.simpleinterpreter.token;

public enum TokenType {
    INT, // 1
    STRING, // "test"
    // operators
    PLUS, // +
    MINUS, // -
    ASTERISK, // *
    SLASH, // /
    ASSIGN, // =
    NEG, // !
    LT, // <
    GT, // >
    EQ, // ==
    NEQ, // !=
    LTEQ, // <=
    GTEQ, // >=
    INCREMENT, // ++
    DECREMENT, // --
    // parenthesis
    LPAREN, // (
    RPAREN, // )
    LSQUARE, // [
    RSQUARE, // ]
    LBRACE, // {
    RBRACE, // }
    // identifiers
    IDENTIFIER, // test
    LET, // let
    FUNCTION, // fn
    IF, // if
    ELSE, // else
    TRUE, // true
    FALSE, // false
    RETURN, // return
    WHILE, // while
    // others
    COMMA, // ,
    EOF,
    // not valid
    ILLEGAL
}
