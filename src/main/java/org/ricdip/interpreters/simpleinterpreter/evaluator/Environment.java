package org.ricdip.interpreters.simpleinterpreter.evaluator;

import org.ricdip.interpreters.simpleinterpreter.evaluator.object.EvaluatedObject;
import org.ricdip.interpreters.simpleinterpreter.evaluator.object.Objects;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.IdentifierExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Environment {
    private final Map<String, EvaluatedObject> environmentMap = new HashMap<>();
    private final Optional<Environment> outerEnvironment;

    public Environment() {
        this.outerEnvironment = Optional.empty();
    }

    public Environment(Environment outerEnvironment) {
        this.outerEnvironment = Optional.of(outerEnvironment);
    }

    public void put(IdentifierExpression identifier, EvaluatedObject value) {
        environmentMap.put(identifier.getValue(), value);
    }

    public EvaluatedObject get(IdentifierExpression identifier) {
        String identifierValue = identifier.getValue();

        if (environmentMap.containsKey(identifierValue)) {
            return environmentMap.get(identifierValue);
        } else if (outerEnvironment.isPresent()) {
            return outerEnvironment.get().get(identifier);
        } else {
            return Objects.NULL;
        }
    }
}
