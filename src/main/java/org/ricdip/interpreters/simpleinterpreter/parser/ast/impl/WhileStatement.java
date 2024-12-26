package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class WhileStatement implements Statement {
    private final Expression condition;
    private final BlockStatement whileBlock;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("while (%s) ", condition));

        stringBuilder.append(whileBlock.toString());

        return stringBuilder.toString();
    }
}
