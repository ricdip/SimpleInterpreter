package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.CallableExpression;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class FunctionExpression implements CallableExpression {
    private final List<IdentifierExpression> formalParameters;
    private final BlockStatement functionBody;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
                String.format("fn (%s) ", String.join(",", formalParameters.stream().map(Object::toString).toList()))
        );

        stringBuilder.append(functionBody);

        return stringBuilder.toString();
    }
}
