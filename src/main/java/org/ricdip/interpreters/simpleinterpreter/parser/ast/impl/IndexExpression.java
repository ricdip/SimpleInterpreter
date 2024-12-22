package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.CallableExpression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class IndexExpression implements CallableExpression, IndexableExpression {
    private final Expression indexableExpression;
    private final Expression index;

    @Override
    public String toString() {
        return String.format("%s[%s]", indexableExpression, index);
    }
}
