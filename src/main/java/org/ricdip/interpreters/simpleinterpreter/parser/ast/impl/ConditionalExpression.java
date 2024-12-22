package org.ricdip.interpreters.simpleinterpreter.parser.ast.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;

import java.util.Optional;

@Getter
@EqualsAndHashCode
public class ConditionalExpression implements Expression {
    private final Expression condition;
    private final BlockStatement ifBranch;
    private final Optional<BlockStatement> elseBranch;

    public ConditionalExpression(Expression condition, BlockStatement ifBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
        this.elseBranch = Optional.empty();
    }

    public ConditionalExpression(Expression condition, BlockStatement ifBranch, BlockStatement elseBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
        this.elseBranch = Optional.of(elseBranch);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("if (%s) ", condition));

        stringBuilder.append(ifBranch.toString());

        if (elseBranch.isPresent()) {
            stringBuilder.append(" else ");
            stringBuilder.append(elseBranch.get());
        }

        return stringBuilder.toString();
    }
}
