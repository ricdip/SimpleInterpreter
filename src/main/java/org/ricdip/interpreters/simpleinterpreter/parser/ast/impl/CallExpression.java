package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.CallableExpression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class CallExpression implements CallableExpression, IndexableExpression {
    private final Expression callableExpression;
    private final List<Expression> actualParameters;

    @Override
    public String toString() {
        return String.format(
                "%s(%s)", callableExpression, String.join(",", actualParameters.stream().map(Object::toString).toList())
        );
    }
}
