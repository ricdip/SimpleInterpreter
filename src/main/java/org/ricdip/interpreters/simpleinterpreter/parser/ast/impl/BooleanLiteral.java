package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class BooleanLiteral implements Expression {
    private final Boolean value;

    @Override
    public String toString() {
        return value.toString();
    }
}
