package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.Operator;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class PrefixExpression implements Expression {
    private final Operator operator;
    private final Expression right;

    @Override
    public String toString() {
        return String.format("(%s%s)", operator.getSymbols(), right);
    }
}
