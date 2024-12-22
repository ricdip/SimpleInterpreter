package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class IntegerLiteral implements Expression {
    private final Integer value;

    @Override
    public String toString() {
        return value.toString();
    }
}
