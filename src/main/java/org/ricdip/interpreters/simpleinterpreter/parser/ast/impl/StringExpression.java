package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class StringExpression implements IndexableExpression {
    private final String value;

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}
