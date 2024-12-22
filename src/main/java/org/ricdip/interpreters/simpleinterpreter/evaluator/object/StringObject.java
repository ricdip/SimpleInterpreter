package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class StringObject implements EvaluatedObject {
    private final String value;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.STRING;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}
