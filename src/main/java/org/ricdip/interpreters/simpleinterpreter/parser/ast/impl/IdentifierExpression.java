package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.CallableExpression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class IdentifierExpression implements CallableExpression, IndexableExpression {
    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
