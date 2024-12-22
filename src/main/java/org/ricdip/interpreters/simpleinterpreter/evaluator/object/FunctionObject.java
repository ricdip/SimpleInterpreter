package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ricdip.interpreters.simpleinterpreter.evaluator.Environment;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.BlockStatement;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.IdentifierExpression;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class FunctionObject implements EvaluatedObject {
    private final List<IdentifierExpression> formalParameters;
    private final BlockStatement functionBody;
    private final Environment functionEnvironment;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.FUNCTION;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
                String.format("fn (%s) ", String.join(",", formalParameters.stream().map(Object::toString).toList()))
        );

        stringBuilder.append(functionBody);

        return stringBuilder.toString();
    }
}
