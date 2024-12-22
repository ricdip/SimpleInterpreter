package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class BlockStatement implements Statement {
    private final List<Statement> statements = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");
        stringBuilder.append(String.join(" ; ", statements.stream().map(Object::toString).toList()));
        stringBuilder.append(" }");

        return stringBuilder.toString();
    }
}
