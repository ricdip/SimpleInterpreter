package org.ricdip.interpreters.simpleinterpreter.evaluator;

import org.ricdip.interpreters.simpleinterpreter.evaluator.object.Objects;
import org.ricdip.interpreters.simpleinterpreter.evaluator.object.*;
import org.ricdip.interpreters.simpleinterpreter.parser.Operator;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Node;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.*;

import java.util.*;

public class Evaluator {
    private final Map<String, BuiltinFunction> builtinFunctionMap = new HashMap<>();

    public Evaluator() {
        builtinFunctionMap.put(Objects.PRINT.getFunctionName(), Objects.PRINT);
        builtinFunctionMap.put(Objects.LEN.getFunctionName(), Objects.LEN);
        builtinFunctionMap.put(Objects.FIRST.getFunctionName(), Objects.FIRST);
        builtinFunctionMap.put(Objects.REST.getFunctionName(), Objects.REST);
        builtinFunctionMap.put(Objects.PUSH.getFunctionName(), Objects.PUSH);
        builtinFunctionMap.put(Objects.APPEND.getFunctionName(), Objects.APPEND);
        builtinFunctionMap.put(Objects.POP.getFunctionName(), Objects.POP);
        builtinFunctionMap.put(Objects.REMOVE_LAST.getFunctionName(), Objects.REMOVE_LAST);
    }

    public EvaluatedObject eval(Node node, Environment environment) {
        if (node instanceof Program program) {
            return evalProgram(program, environment);
        } else if (node instanceof ExpressionStatement expressionStatement) {
            return eval(expressionStatement.getExpression(), environment);
        } else if (node instanceof LetStatement letStatement) {
            return evalLetStatement(letStatement.getName(), letStatement.getValue(), environment);
        } else if (node instanceof IdentifierExpression identifierExpression) {
            return evalIdentifierExpression(identifierExpression, environment);
        } else if (node instanceof IntegerLiteral integerLiteral) {
            return new IntegerObject(integerLiteral.getValue());
        } else if (node instanceof BooleanLiteral booleanLiteral) {
            return new BooleanObject(booleanLiteral.getValue());
        } else if (node instanceof PrefixExpression prefixExpression) {
            return evalPrefixExpression(prefixExpression.getOperator(), prefixExpression.getRight(), environment);
        } else if (node instanceof InfixExpression infixExpression) {
            return evalInfixExpression(
                    infixExpression.getOperator(),
                    infixExpression.getLeft(),
                    infixExpression.getRight(),
                    environment
            );
        } else if (node instanceof BlockStatement blockStatement) {
            return evalBlockStatement(blockStatement, environment);
        } else if (node instanceof ConditionalExpression conditionalExpression) {
            return evalConditionalExpression(
                    conditionalExpression.getCondition(),
                    conditionalExpression.getIfBranch(),
                    conditionalExpression.getElseBranch(),
                    environment
            );
        } else if (node instanceof FunctionExpression functionExpression) {
            return new FunctionObject(
                    functionExpression.getFormalParameters(),
                    functionExpression.getFunctionBody(),
                    environment
            );
        } else if (node instanceof CallExpression callExpression) {
            return evalCallExpression(
                    callExpression.getCallableExpression(),
                    callExpression.getActualParameters(),
                    environment
            );
        } else if (node instanceof ReturnStatement returnStatement) {
            return evalReturnStatement(returnStatement, environment);
        } else if (node instanceof ArrayExpression arrayExpression) {
            return evalArrayExpression(arrayExpression, environment);
        } else if (node instanceof IndexExpression indexExpression) {
            return evalIndexExpression(indexExpression, environment);
        } else if (node instanceof StringExpression stringExpression) {
            return new StringObject(stringExpression.getValue());
        } else if (node instanceof PostfixExpression postfixExpression) {
            return evalPostfixExpression(postfixExpression.getOperator(), postfixExpression.getLeft(), environment);
        } else if (node instanceof WhileStatement whileStatement) {
            return evalWhileStatement(whileStatement.getCondition(), whileStatement.getWhileBlock(), environment);
        } else {
            return new ErrorObject("Unknown AST node: %s", node);
        }
    }

    /**
     * Evaluates a top-level AST node (Program node) and returns the result of the last evaluated statement.
     * If a {@link ReturnObject} object is found, the evaluation will stop, the object will be unwrapped and the
     * contained value returned.
     *
     * @param program     the {@link Program object that represent the top-level AST node}
     * @param environment the {@link Environment} object that contains the bindings
     * @return the result of the last evaluated statement
     */
    private EvaluatedObject evalProgram(Program program, Environment environment) {
        EvaluatedObject lastEvaluatedStatementResult = null;

        for (Statement statement : program.getStatements()) {
            lastEvaluatedStatementResult = eval(statement, environment);

            if (lastEvaluatedStatementResult instanceof ErrorObject errorObject) {
                return errorObject;
            } else if (lastEvaluatedStatementResult instanceof ReturnObject returnObject) {
                // the return object is unwrapped
                return returnObject.getReturnValue();
            }
        }

        return lastEvaluatedStatementResult;
    }

    /**
     * Evaluates a block statement and returns the result of the last evaluated statement.
     * If a {@link ReturnObject} object is found, the evaluation will stop and the object will be returned.
     * The {@link ReturnObject} object will not be unwrapped, so it will bubble-up until it is unwrapped. It can be
     * unwrapped if it reaches the evaluation of the top-level AST node (Program node) or if it is unwrapped by the
     * evaluation of the call expression.
     *
     * @param blockStatement the {@link BlockStatement object that represent the block statement}
     * @param environment    the {@link Environment} object that contains the bindings
     * @return the result of the last evaluated statement
     */
    private EvaluatedObject evalBlockStatement(BlockStatement blockStatement, Environment environment) {
        EvaluatedObject lastEvaluatedStatementResult = null;

        for (Statement statement : blockStatement.getStatements()) {
            lastEvaluatedStatementResult = eval(statement, environment);

            if (lastEvaluatedStatementResult instanceof ErrorObject errorObject) {
                return errorObject;
            } else if (lastEvaluatedStatementResult instanceof ReturnObject returnObject) {
                // the return object is not unwrapped: it will bubble-up in every nested block statement until it reaches
                // the top-level AST node or until it is unwrapped by the evaluation of the call expression
                return returnObject;
            }
        }

        return lastEvaluatedStatementResult;
    }

    /**
     * Evaluates a let statement and returns null object (a let statement does not produce a value).
     *
     * @param identifier  the {@link IdentifierExpression} used to bind the evaluated {@link Expression} in the {@link Environment}
     * @param expression  the {@link Expression} object to evaluate and bind in the {@link Environment}
     * @param environment the {@link Environment} object that contains the bindings
     * @return null object
     */
    private EvaluatedObject evalLetStatement(IdentifierExpression identifier, Expression expression, Environment environment) {
        EvaluatedObject evaluatedExpression = eval(expression, environment);

        if (evaluatedExpression instanceof ErrorObject errorObject) {
            return errorObject;
        }

        if (builtinFunctionMap.containsKey(identifier.getValue())) {
            return new ErrorObject("Identifier '%s' already used as a builtin function", identifier.getValue());
        }

        environment.put(identifier, evaluatedExpression);

        return Objects.NULL;
    }

    /**
     * Evaluates an {@link IdentifierExpression} and returns the {@link EvaluatedObject} bound to the identifier in the
     * {@link Environment}.
     *
     * @param identifier  the {@link IdentifierExpression} used to bind the {@link EvaluatedObject} in the {@link Environment}
     * @param environment the {@link Environment} object that contains the bindings
     * @return the {@link EvaluatedObject} bound to the identifier
     */
    private EvaluatedObject evalIdentifierExpression(IdentifierExpression identifier, Environment environment) {
        if (builtinFunctionMap.containsKey(identifier.getValue())) {
            return builtinFunctionMap.get(identifier.getValue());
        } else {
            return environment.get(identifier);
        }
    }

    /**
     * Evaluates a prefix expression and returns its result
     *
     * @param operator    the prefix {@link Operator}
     * @param right       the {@link Expression} to which the operator is applied
     * @param environment the {@link Environment} object that contains the bindings
     * @return the result of the expression after the application of the operator
     */
    private EvaluatedObject evalPrefixExpression(Operator operator, Expression right, Environment environment) {
        EvaluatedObject evaluatedRight = eval(right, environment);

        if (evaluatedRight instanceof ErrorObject errorObject) {
            return errorObject;
        }

        return switch (operator) {
            case MINUS -> evalPrefixExpressionOperatorMinus(evaluatedRight);
            case NEG -> evalPrefixExpressionOperatorNeg(evaluatedRight);
            default ->
                    new ErrorObject("Unknown prefix operator %s%s", operator.getSymbols(), evaluatedRight.getType().name());
        };
    }

    /**
     * Evaluates a prefix expression case in which the {@link Operator} applied is '-'.
     * Used by the evaluation of a PrefixExpression.
     *
     * @param right the {@link EvaluatedObject} that represents the evaluated expression
     * @return the result of the expression after the application of the operator
     */
    private EvaluatedObject evalPrefixExpressionOperatorMinus(EvaluatedObject right) {
        if (right instanceof IntegerObject integerObject) {
            return new IntegerObject(-integerObject.getValue());
        } else {
            return new ErrorObject("Cannot apply prefix operator '-' to %s", right.getType().name());
        }
    }

    /**
     * Evaluates a prefix expression case in which the {@link Operator} applied is '!'.
     * Used by the evaluation of a PrefixExpression.
     *
     * @param right the {@link EvaluatedObject} that represents the evaluated expression
     * @return the result of the expression after the application of the operator
     */
    private EvaluatedObject evalPrefixExpressionOperatorNeg(EvaluatedObject right) {
        if (right instanceof BooleanObject booleanObject) {
            return new BooleanObject(!booleanObject.getValue());
        } else {
            return new ErrorObject("Cannot apply prefix operator '!' to %s", right.getType().name());
        }
    }

    /**
     * Evaluates an infix expression and returns its result
     *
     * @param operator    the infix {@link Operator}
     * @param left        the left-hand side {@link Expression} to which the operator is applied
     * @param right       the right-hand side {@link Expression} to which the operator is applied
     * @param environment the {@link Environment} object that contains the bindings
     * @return the result of the expression after the application of the operator
     */
    private EvaluatedObject evalInfixExpression(Operator operator, Expression left, Expression right, Environment environment) {
        EvaluatedObject evaluatedLeft = eval(left, environment);
        EvaluatedObject evaluatedRight = eval(right, environment);

        if ((evaluatedLeft instanceof IntegerObject leftIntegerObject) &&
            (evaluatedRight instanceof IntegerObject rightIntegerObject)) {
            return switch (operator) {
                case LT -> new BooleanObject(leftIntegerObject.getValue() < rightIntegerObject.getValue());
                case GT -> new BooleanObject(leftIntegerObject.getValue() > rightIntegerObject.getValue());
                case EQ -> new BooleanObject(
                        leftIntegerObject.getValue().intValue() == rightIntegerObject.getValue().intValue()
                );
                case NEQ -> new BooleanObject(
                        leftIntegerObject.getValue().intValue() != rightIntegerObject.getValue().intValue()
                );
                case LTEQ -> new BooleanObject(leftIntegerObject.getValue() <= rightIntegerObject.getValue());
                case GTEQ -> new BooleanObject(leftIntegerObject.getValue() >= rightIntegerObject.getValue());
                case PLUS -> new IntegerObject(leftIntegerObject.getValue() + rightIntegerObject.getValue());
                case MINUS -> new IntegerObject(leftIntegerObject.getValue() - rightIntegerObject.getValue());
                case ASTERISK -> new IntegerObject(leftIntegerObject.getValue() * rightIntegerObject.getValue());
                case SLASH -> new IntegerObject(leftIntegerObject.getValue() / rightIntegerObject.getValue());
                default -> new ErrorObject(
                        "Unknown infix operator %s %s %s",
                        leftIntegerObject.getType().name(),
                        operator.getSymbols(),
                        rightIntegerObject.getType().name()
                );
            };
        } else if ((evaluatedLeft instanceof BooleanObject leftIntegerObject) &&
                   (evaluatedRight instanceof BooleanObject rightIntegerObject)) {
            return switch (operator) {

                case EQ -> new BooleanObject(
                        leftIntegerObject.getValue().booleanValue() == rightIntegerObject.getValue().booleanValue()
                );
                case NEQ -> new BooleanObject(
                        leftIntegerObject.getValue().booleanValue() != rightIntegerObject.getValue().booleanValue()
                );
                default -> new ErrorObject(
                        "Unknown infix operator %s %s %s",
                        leftIntegerObject.getType().name(),
                        operator.getSymbols(),
                        rightIntegerObject.getType().name()
                );
            };
        } else if (evaluatedLeft instanceof ErrorObject errorObjectLeft) {
            return errorObjectLeft;
        } else if (evaluatedRight instanceof ErrorObject errorObjectRight) {
            return errorObjectRight;
        } else {
            return new ErrorObject("Cannot solve infix expression %s %s %s", evaluatedLeft.getType().name(), operator.getSymbols(), evaluatedRight.getType().name());
        }
    }

    /**
     * Evaluates a conditional expression and executes the {@code ifBranch} {@link BlockStatement} if condition is {@code true}. Executes
     * the {@code elseBranch} {@link BlockStatement} is condition is {@code false} and the {@code elseBranch} is contained in {@link Optional} wrapper.
     *
     * @param condition   the conditional expression boolean condition
     * @param ifBranch    the {@link BlockStatement} if branch
     * @param elseBranch  the {@link BlockStatement} else branch wrapped in an {@link Optional} object
     * @param environment the {@link Environment} object that contains the bindings
     * @return the result of the last statement executed or null object if condition is {@code false} and {@code elseBranch} is not present.
     */
    private EvaluatedObject evalConditionalExpression(Expression condition, BlockStatement ifBranch, Optional<BlockStatement> elseBranch, Environment environment) {
        EvaluatedObject isTrue = eval(condition, environment);

        if (isTrue instanceof ErrorObject) {
            return isTrue;
        } else if (!(isTrue instanceof BooleanObject)) {
            return new ErrorObject("Conditional expression condition must be a %s expression, got %s", ObjectTypes.BOOLEAN.name(), isTrue.getType().name());
        }

        boolean ifCondition = ((BooleanObject) isTrue).getValue();

        if (ifCondition) {
            return eval(ifBranch, environment);
        } else if (elseBranch.isPresent()) {
            return eval(elseBranch.get(), environment);
        } else {
            return Objects.NULL;
        }
    }

    /**
     * Evaluates a call expression: if the function is not a builtin function, it binds the called function
     * formal parameters with the actual call parameters and executes the function body. If the function is a
     * builtin function, it evaluates the actual call parameters and executes the builtin function with the evaluated
     * parameters.
     *
     * @param callableExpression the callable expression (can be the name of the called function or an anonymous function)
     * @param actualParameters   the {@link List<Expression> object that contains the actual call parameters}
     * @param environment        the {@link Environment} object that contains the bindings
     * @return the result of the last evaluated statement of the called function
     */
    private EvaluatedObject evalCallExpression(Expression callableExpression, List<Expression> actualParameters, Environment environment) {
        EvaluatedObject evaluatedObject = eval(callableExpression, environment);

        if (evaluatedObject instanceof ErrorObject errorObject) {
            return errorObject;
        } else if (evaluatedObject instanceof BuiltinFunction builtinFunction) {
            return callBuiltinFunction(builtinFunction, actualParameters, environment);
        } else if (evaluatedObject instanceof FunctionObject functionObject) {
            return callFunctionObject(functionObject, actualParameters, environment);
        } else {
            return new ErrorObject("Cannot invoke %s: not a %s", evaluatedObject.getType().name(), ObjectTypes.FUNCTION);
        }
    }

    /**
     * Calls the {@link BuiltinFunction} after evaluating its {@link List<Expression>} of actual call
     * parameters.
     *
     * @param builtinFunction  the {@link BuiltinFunction} to call
     * @param actualParameters the {@link List<Expression>} to bind to the function formal parameters
     * @param environment      the {@link Environment} object that contains the bindings
     * @return the result of the called builtin function
     */
    private EvaluatedObject callBuiltinFunction(BuiltinFunction builtinFunction, List<Expression> actualParameters, Environment environment) {
        List<EvaluatedObject> evaluatedParameters = new ArrayList<>();
        for (Expression parameter : actualParameters) {
            EvaluatedObject evaluatedParameter = eval(parameter, environment);
            if (evaluatedParameter instanceof ErrorObject errorObject) {
                return errorObject;
            }
            evaluatedParameters.add(evaluatedParameter);
        }

        return builtinFunction.getFunctionImplementation().apply(evaluatedParameters.toArray(new EvaluatedObject[0]));
    }

    /**
     * Calls the {@link FunctionObject} after binding its formal parameters with the {@link List<Expression>} of actual
     * parameters.
     *
     * @param functionObject   the {@link FunctionObject} to call
     * @param actualParameters the {@link List<Expression>} to bind to the function formal parameters
     * @param environment      the {@link Environment} object that contains the bindings
     * @return the result of the last evaluated statement of the called function
     */
    private EvaluatedObject callFunctionObject(FunctionObject functionObject, List<Expression> actualParameters, Environment environment) {
        // check if parameters binding is possible
        if (functionObject.getFormalParameters().size() != actualParameters.size()) {
            return new ErrorObject(
                    "Formal parameters and actual parameters differ in length (formal %d != actual %d)",
                    functionObject.getFormalParameters().size(),
                    actualParameters.size()
            );
        }

        // evaluate actual parameters using current environment
        List<EvaluatedObject> evaluatedActualParameters = evalActualParameters(actualParameters, environment);

        // check actual parameter evaluation error
        if (evaluatedActualParameters.size() == 1 && evaluatedActualParameters.getFirst() instanceof ErrorObject errorObject) {
            return errorObject;
        }

        // create function call environment (inner scope) extending the environment previously stored in the function as
        // outer environment (outer scope): if an identifier is not found in the inner scope, it will be searched in
        // the outer scope.
        // All the identifiers declared before function creation are now accessible and so,
        // CLOSURES are now possible:
        // > let newAdder = fn(x) { fn(y) { x + y } };
        // > let addTwo = newAdder(2);
        // > x
        //      identifier not found: x
        // > addTwo(3)
        // > 5
        //
        // (x is not bound to a value in the inner scope, but addTwo still has access to it (outer scope))
        // (newAdder is a higher-order function: a function that either return other functions or receive functions as arguments)
        // (in this language functions are first-class citizens, we can pass functions like any other value)
        Environment innerEnvironment = new Environment(functionObject.getFunctionEnvironment());
        for (int i = 0; i < functionObject.getFormalParameters().size(); i++) {
            // bind current actual parameter to current formal parameter
            innerEnvironment.put(
                    functionObject.getFormalParameters().get(i),
                    evaluatedActualParameters.get(i)
            );
        }

        // evaluate function body (block statement) with created function call environment. If the evaluation
        // returns a ReturnObject, it is unwrapped and its value returned
        EvaluatedObject functionCallReturnValue = eval(functionObject.getFunctionBody(), innerEnvironment);

        return unwrapReturnValue(functionCallReturnValue);
    }

    /**
     * Evaluates the {@link List<Expression>} that represent the actual parameters.
     *
     * @param actualParameters the {@link List<Expression>} of actual parameters
     * @param environment      the {@link Environment} object that contains the bindings
     * @return the {@link List<EvaluatedObject>} of evaluated actual parameters
     */
    private List<EvaluatedObject> evalActualParameters(List<Expression> actualParameters, Environment environment) {
        List<EvaluatedObject> evaluatedParameters = new ArrayList<>();

        for (Expression actualParameter : actualParameters) {
            EvaluatedObject evaluatedParameter = eval(actualParameter, environment);

            if (evaluatedParameter instanceof ErrorObject errorObject) {
                return List.of(errorObject);
            }

            evaluatedParameters.add(evaluatedParameter);
        }

        return evaluatedParameters;
    }

    /**
     * If the {@code evaluatedObject} is a {@link ReturnObject}, it is unwrapped and its value is returned. Otherwise,
     * the {@code evaluatedObject} is returned.
     *
     * @param evaluatedObject the {@link EvaluatedObject} to unwrap
     * @return the value contained in the {@code evaluatedObject} if it is an object of type a {@link ReturnObject}.
     * Otherwise, the {@code evaluatedObject} is returned
     */
    private EvaluatedObject unwrapReturnValue(EvaluatedObject evaluatedObject) {
        if (evaluatedObject instanceof ReturnObject returnObject) {
            return returnObject.getReturnValue();
        } else {
            return evaluatedObject;
        }
    }

    /**
     * Evaluates a return statement and returns the created {@link ReturnObject}.
     *
     * @param returnStatement the {@link ReturnStatement} statement
     * @param environment     the {@link Environment} object that contains the bindings
     * @return the created {@link ReturnObject}
     */
    private EvaluatedObject evalReturnStatement(ReturnStatement returnStatement, Environment environment) {
        EvaluatedObject evaluatedReturnValue = eval(returnStatement.getReturnValue(), environment);

        if (evaluatedReturnValue instanceof ErrorObject errorObject) {
            return errorObject;
        }

        return new ReturnObject(evaluatedReturnValue);
    }

    /**
     * Evaluates an array expression and returns the created {@link ArrayObject}.
     *
     * @param arrayExpression the {@link ArrayExpression} expression
     * @param environment     the {@link Environment} object that contains the bindings
     * @return the created {@link ArrayObject}
     */
    private EvaluatedObject evalArrayExpression(ArrayExpression arrayExpression, Environment environment) {
        List<EvaluatedObject> evaluatedElements = new ArrayList<>();

        for (Expression element : arrayExpression.getElements()) {
            EvaluatedObject evaluatedElement = eval(element, environment);

            if (evaluatedElement instanceof ErrorObject errorObject) {
                return errorObject;
            }

            evaluatedElements.add(evaluatedElement);
        }

        return new ArrayObject(evaluatedElements);
    }

    /**
     * Evaluates an index expression and returns the element at the specified position in the array.
     *
     * @param indexExpression the {@link IndexExpression} expression
     * @param environment     the {@link Environment} object that contains the bindings
     * @return the element at the specified position in the array
     */
    private EvaluatedObject evalIndexExpression(IndexExpression indexExpression, Environment environment) {
        EvaluatedObject evaluatedIndexableObject = eval(indexExpression.getIndexableExpression(), environment);

        if (evaluatedIndexableObject instanceof ErrorObject errorObject) {
            return errorObject;
        } else if (evaluatedIndexableObject instanceof ArrayObject arrayObject) {
            return indexArrayObject(arrayObject, indexExpression.getIndex(), environment);
        } else if (evaluatedIndexableObject instanceof StringObject stringObject) {
            return indexStringObject(stringObject, indexExpression.getIndex(), environment);
        } else {
            return new ErrorObject("Cannot index non-indexable object: %s", evaluatedIndexableObject.getType().name());
        }
    }

    /**
     * Evaluates the index used on the {@code arrayObject} and retrieves the element at the specified index position
     * in the array.
     *
     * @param arrayObject the {@link ArrayObject} object that contains the array elements
     * @param index       the {@link Expression} object that represent the index
     * @param environment the {@link Environment} object that contains the bindings
     * @return the element at the specified position in the array
     */
    private EvaluatedObject indexArrayObject(ArrayObject arrayObject, Expression index, Environment environment) {
        EvaluatedObject evaluatedIndex = eval(index, environment);

        if (evaluatedIndex instanceof ErrorObject errorObject) {
            return errorObject;
        } else if (evaluatedIndex instanceof IntegerObject integerObject) {
            int indexValue = integerObject.getValue();
            List<EvaluatedObject> evaluatedElements = arrayObject.getElements();

            if (indexValue >= evaluatedElements.size()) {
                // index out of bounds
                return new ErrorObject(
                        "Array index out of bounds: max index %d, got %d",
                        evaluatedElements.size() - 1,
                        indexValue
                );
            } else if (indexValue < 0) {
                // reverse indexing
                indexValue = Math.abs(indexValue);

                if (indexValue > evaluatedElements.size()) {
                    // reverse index out of bounds
                    return new ErrorObject(
                            "Array reverse index out of bounds: max reverse index -%d, got -%d",
                            evaluatedElements.size(),
                            indexValue
                    );
                }

                List<EvaluatedObject> reversedElements = new ArrayList<>(evaluatedElements);
                Collections.reverse(reversedElements);
                return reversedElements.get(indexValue - 1);

            } else {
                // indexing
                return evaluatedElements.get(indexValue);
            }

        } else {
            return new ErrorObject("Cannot use %s as index", evaluatedIndex.getType().name());
        }
    }

    /**
     * Evaluates the index used on the {@code stringObject} and retrieves the element at the specified index position
     * in the string.
     *
     * @param stringObject the {@link StringObject} object that contains the string elements
     * @param index        the {@link Expression} object that represent the index
     * @param environment  the {@link Environment} object that contains the bindings
     * @return the element at the specified position in the string
     */
    private EvaluatedObject indexStringObject(StringObject stringObject, Expression index, Environment environment) {
        EvaluatedObject evaluatedIndex = eval(index, environment);

        if (evaluatedIndex instanceof ErrorObject errorObject) {
            return errorObject;
        } else if (evaluatedIndex instanceof IntegerObject integerObject) {
            int indexValue = integerObject.getValue();
            String stringValue = stringObject.getValue();

            if (indexValue >= stringValue.length()) {
                // index out of bounds
                return new ErrorObject(
                        "String index out of bounds: max index %d, got %d",
                        stringValue.length() - 1,
                        indexValue
                );
            } else if (indexValue < 0) {
                // reverse indexing
                indexValue = Math.abs(indexValue);

                if (indexValue > stringValue.length()) {
                    // reverse index out of bounds
                    return new ErrorObject(
                            "String reverse index out of bounds: max reverse index -%d, got -%d",
                            stringValue.length(),
                            indexValue
                    );
                }

                StringBuilder reversedString = new StringBuilder();
                reversedString.append(stringValue);

                return new StringObject(String.valueOf(reversedString.reverse().toString().charAt(indexValue - 1)));

            } else {
                // indexing
                return new StringObject(String.valueOf(stringObject.getValue().charAt(indexValue)));
            }

        } else {
            return new ErrorObject("Cannot use %s as index", evaluatedIndex.getType().name());
        }
    }

    /**
     * Evaluates a postfix expression and returns its result
     *
     * @param operator    the postfix {@link Operator}
     * @param left        the {@link Expression} to which the operator is applied
     * @param environment the {@link Environment} object that contains the bindings
     * @return the result of the expression after the application of the operator
     */
    private EvaluatedObject evalPostfixExpression(Operator operator, Expression left, Environment environment) {
        return switch (operator) {
            case INCREMENT -> evalPostfixExpressionOperatorIncrement(left, environment);
            case DECREMENT -> evalPostfixExpressionOperatorDecrement(left, environment);
            default -> new ErrorObject("Unknown postfix operator %s%s", left, operator.getSymbols());
        };
    }

    /**
     * Evaluates a postfix expression case in which the {@link Operator} applied is '++'.
     * Used by the evaluation of a PostfixExpression.
     *
     * @param left the {@link Expression} that represents the identifier whose pointed value will be incremented
     * @return null object
     */
    private EvaluatedObject evalPostfixExpressionOperatorIncrement(Expression left, Environment environment) {
        if (left instanceof IdentifierExpression identifier) {
            EvaluatedObject evaluatedObject = environment.get(identifier);
            if (evaluatedObject instanceof IntegerObject integerObject) {
                environment.put(identifier, new IntegerObject(integerObject.getValue() + 1));
                return Objects.NULL;
            } else if (Objects.NULL.equals(evaluatedObject)) {
                return new ErrorObject("Cannot apply postfix operator '++' to %s: not declared", left);
            } else {
                return new ErrorObject("Cannot apply postfix operator '++' to %s: not an integer", left);
            }
        } else {
            return new ErrorObject("Cannot apply postfix operator '++' to %s", left);
        }
    }

    /**
     * Evaluates a postfix expression case in which the {@link Operator} applied is '--'.
     * Used by the evaluation of a PostfixExpression.
     *
     * @param left the {@link Expression} that represents the identifier whose pointed value will be decremented
     * @return null object
     */
    private EvaluatedObject evalPostfixExpressionOperatorDecrement(Expression left, Environment environment) {
        if (left instanceof IdentifierExpression identifier) {
            EvaluatedObject evaluatedObject = environment.get(identifier);
            if (evaluatedObject instanceof IntegerObject integerObject) {
                environment.put(identifier, new IntegerObject(integerObject.getValue() - 1));
                return Objects.NULL;
            } else if (Objects.NULL.equals(evaluatedObject)) {
                return new ErrorObject("Cannot apply postfix operator '--' to %s: not declared", left);
            } else {
                return new ErrorObject("Cannot apply postfix operator '--' to %s: not an integer", left);
            }
        } else {
            return new ErrorObject("Cannot apply postfix operator '--' to %s", left);
        }
    }

    /**
     * Evaluates a while statement
     *
     * @param condition   the while expression boolean condition
     * @param whileBlock  the {@link BlockStatement} while block evaluated if {@code condition} expression evaluates to {@code true}
     * @param environment the {@link Environment} object that contains the bindings
     * @return null object
     */
    private EvaluatedObject evalWhileStatement(Expression condition, BlockStatement whileBlock, Environment environment) {
        EvaluatedObject isTrue = eval(condition, environment);

        if (isTrue instanceof ErrorObject) {
            return isTrue;
        } else if (!(isTrue instanceof BooleanObject)) {
            return new ErrorObject("While statement condition must be a %s expression, got %s", ObjectTypes.BOOLEAN.name(), isTrue.getType().name());
        }

        boolean whileCondition = ((BooleanObject) isTrue).getValue();

        // if while condition is true
        if (whileCondition) {
            // evaluates the while block: the evaluation changes the environment
            eval(whileBlock, environment);
            // recursively call this function: the environment keeps the changes done by the previous eval call.
            // Example:
            // > let a = 0
            // .. while(a < 2) {
            // .. print(a)
            // .. a++
            // .. }
            //
            // the code above will print 0 and 1 and after that will exit the while block
            evalWhileStatement(condition, whileBlock, environment);
        }

        return Objects.NULL;
    }
}
