package org.ricdip.interpreters.simpleinterpreter.parser.ast;

/**
 * Interface that represent an expression that is also callable: a callable expression can be used as left-hand side
 * in a call expression. Example: the identifier {@code add} in the call expression {@code add(1,2)}.
 */
public interface CallableExpression extends Expression {
}
