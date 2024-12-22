package org.ricdip.interpreters.simpleinterpreter.parser.ast;

/**
 * Interface that represent an expression that is also indexable: an indexable expression can be used as left-hand side
 * in an index expression. Example: the identifier {@code array} in the index expression {@code array[0]}.
 */
public interface IndexableExpression extends Expression {
}
