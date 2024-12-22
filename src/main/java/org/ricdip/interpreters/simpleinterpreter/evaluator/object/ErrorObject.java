package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ErrorObject implements EvaluatedObject {
    private final String error;

    public ErrorObject(String error) {
        this.error = error;
    }

    public ErrorObject(String error, Object... args) {
        this.error = String.format(error, args);
    }

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.ERROR;
    }

    @Override
    public String toString() {
        return String.format("\t%s", error);
    }
}
