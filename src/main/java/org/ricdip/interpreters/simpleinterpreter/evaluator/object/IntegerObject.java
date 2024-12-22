package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class IntegerObject implements EvaluatedObject {
    private final Integer value;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.INTEGER;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
