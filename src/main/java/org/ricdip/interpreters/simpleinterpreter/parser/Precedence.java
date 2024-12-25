package org.ricdip.interpreters.simpleinterpreter.parser;

public enum Precedence {
    LOWEST,
    COMPARISON,
    SUMMATION,
    MULTIPLICATION,
    PREFIX,
    POSTFIX,
    CALL,
    INDEX
}
