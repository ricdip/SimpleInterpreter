package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnStatement implements Statement {
    private final Expression returnValue;

    @Override
    public String toString() {
        // TODO: to string null pointer exception
        return String.format("(return %s)", returnValue.toString());
    }
}
