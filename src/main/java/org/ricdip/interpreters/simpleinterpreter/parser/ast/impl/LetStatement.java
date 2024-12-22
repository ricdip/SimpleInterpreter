package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class LetStatement implements Statement {
    private final IdentifierExpression name;
    private final Expression value;

    @Override
    public String toString() {
        return String.format("(%s = %s)", name, value);
    }
}
