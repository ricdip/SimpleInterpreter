package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StringObject implements EvaluatedObject {
    private String value;

    public StringObject(@NonNull String value) {
        this.value = value;
    }

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.STRING;
    }

    public StringObject concat(StringObject stringObject) {
        return new StringObject(this.value + stringObject.getValue());
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", value);
    }
}
