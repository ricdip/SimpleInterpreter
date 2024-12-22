package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class ArrayObject implements EvaluatedObject {
    private final List<EvaluatedObject> elements;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.ARRAY;
    }

    @Override
    public String toString() {
        return String.format("[%s]", String.join(",", elements.stream().map(Object::toString).toList()));
    }
}
