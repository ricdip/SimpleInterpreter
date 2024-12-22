package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class NullObject implements EvaluatedObject {
    @Override
    public ObjectTypes getType() {
        return ObjectTypes.NULL;
    }

    @Override
    public String toString() {
        return "null";
    }
}
