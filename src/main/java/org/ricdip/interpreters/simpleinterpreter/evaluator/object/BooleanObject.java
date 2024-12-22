package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class BooleanObject implements EvaluatedObject {
    private final Boolean value;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.BOOLEAN;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
