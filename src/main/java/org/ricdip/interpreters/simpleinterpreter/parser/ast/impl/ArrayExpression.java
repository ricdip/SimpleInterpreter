package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class ArrayExpression implements IndexableExpression {
    private final List<Expression> elements;

    @Override
    public String toString() {
        return String.format("[%s]", String.join(",", elements.stream().map(Object::toString).toList()));
    }
}
