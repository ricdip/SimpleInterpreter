package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

@FunctionalInterface
public interface BuiltinFunctionalInterface {
    EvaluatedObject apply(EvaluatedObject... input);
}
