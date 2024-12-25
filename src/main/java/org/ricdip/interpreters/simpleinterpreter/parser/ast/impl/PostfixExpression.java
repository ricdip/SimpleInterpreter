package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.Operator;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class PostfixExpression implements Expression {
    private final Expression left;
    private final Operator operator;

    @Override
    public String toString() {
        return String.format("(%s%s)", left, operator.getSymbols());
    }
}
