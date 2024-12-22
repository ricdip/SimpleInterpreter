package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Node;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
public class Program implements Node {
    private final List<Statement> statements = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{\n");

        for (Statement statement : statements) {
            stringBuilder.append(String.format("\t%s\n", statement));
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
