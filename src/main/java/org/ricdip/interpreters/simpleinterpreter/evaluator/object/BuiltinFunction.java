package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BuiltinFunction implements EvaluatedObject {
    private final String functionName;
    private final BuiltinFunctionalInterface functionImplementation;
    private final String functionUsageMessage;

    @Override
    public ObjectTypes getType() {
        return ObjectTypes.BUILTIN;
    }

    @Override
    public String toString() {
        return String.format("\t%s", functionUsageMessage);
    }
}
