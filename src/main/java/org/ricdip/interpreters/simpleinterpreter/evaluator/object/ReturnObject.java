package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class ReturnObject implements EvaluatedObject {
    private final EvaluatedObject returnValue;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.RETURN;
    }

    @Override
    public String toString() {
        return String.format("return %s", returnValue);
    }
}
