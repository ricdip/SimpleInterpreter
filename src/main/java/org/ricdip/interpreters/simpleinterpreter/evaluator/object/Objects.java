package org.ricdip.interpreters.simpleinterpreter.evaluator.object;

import org.ricdip.interpreters.simpleinterpreter.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class Objects {
    public static final NullObject NULL = new NullObject();
    public static final BuiltinFunction PRINT = new BuiltinFunction(
            "print",
            args -> {
                if (args.length < 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be > 1", args.length);
                }

                for (EvaluatedObject arg : args) {
                    // TODO: remove println
                    System.out.println(arg);
                }

                return NULL;
            },
            """
                    print(x -> any, ...) -> null: prints all parameters
                    """
    );
    public static final BuiltinFunction LEN = new BuiltinFunction(
            "len",
            args -> {
                if (args.length != 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 1", args.length);
                }

                EvaluatedObject arg = args[0];

                if (arg instanceof ArrayObject arrayObject) {
                    return new IntegerObject(arrayObject.getElements().size());
                } else if (arg instanceof StringObject stringObject) {
                    return new IntegerObject(stringObject.getValue().length());
                } else {
                    return Utils.unexpectedObjectTypeError(arg.getType(), ObjectTypes.ARRAY, ObjectTypes.STRING);
                }
            },
            """
                    len(x -> array|string) -> integer: returns the number of elements in 'x'
                    """
    );
    public static final BuiltinFunction FIRST = new BuiltinFunction(
            "first",
            args -> {
                if (args.length != 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 1", args.length);
                }

                EvaluatedObject arg = args[0];

                if (arg instanceof ArrayObject arrayObject) {
                    return arrayObject.getElements().stream().findFirst().orElse(NULL);
                } else if (arg instanceof StringObject stringObject) {
                    String stringValue = stringObject.getValue();
                    return !stringValue.isEmpty() ? new StringObject(String.valueOf(stringValue.charAt(0))) : NULL;
                } else {
                    return Utils.unexpectedObjectTypeError(arg.getType(), ObjectTypes.ARRAY, ObjectTypes.STRING);
                }
            },
            """
                    first(x -> array|string) -> any|string: returns the first element in 'x'
                    """
    );
    public static final BuiltinFunction REST = new BuiltinFunction(
            "rest",
            args -> {
                if (args.length != 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 1", args.length);
                }

                EvaluatedObject arg = args[0];

                if (arg instanceof ArrayObject arrayObject) {
                    List<EvaluatedObject> arrayElements = arrayObject.getElements();

                    return !arrayElements.isEmpty() ? new ArrayObject(arrayElements.subList(1, arrayElements.size())) : NULL;
                } else if (arg instanceof StringObject stringObject) {
                    String stringValue = stringObject.getValue();

                    return !stringValue.isEmpty() ? new StringObject(stringValue.substring(1)) : NULL;
                } else {
                    return Utils.unexpectedObjectTypeError(arg.getType(), ObjectTypes.ARRAY, ObjectTypes.STRING);
                }
            },
            """
                    rest(x -> array|string) -> array|string: returns all the elements in 'x' excluded the first element
                    """
    );
    public static final BuiltinFunction PUSH = new BuiltinFunction(
            "push",
            args -> {
                if (args.length != 2) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 2", args.length);
                }

                EvaluatedObject container = args[0];
                EvaluatedObject element = args[1];

                if (container instanceof ArrayObject arrayObject) {
                    List<EvaluatedObject> arrayElements = new ArrayList<>(arrayObject.getElements());
                    arrayElements.addFirst(element);
                    return new ArrayObject(arrayElements);
                } else if (container instanceof StringObject containerStringObject) {
                    if (element instanceof StringObject elementStringObject) {
                        return elementStringObject.concat(containerStringObject);
                    } else {
                        return new StringObject(element.toString() + containerStringObject.getValue());
                    }
                } else {
                    return Utils.unexpectedObjectTypeError(
                            "Unexpected type of first argument: expected %s, got %s",
                            container.getType(),
                            ObjectTypes.ARRAY,
                            ObjectTypes.STRING
                    );
                }
            },
            """
                    push(x -> array|string, y: any) -> array|string: returns a new object with the new element 'y' added as first element of 'x'
                    """
    );
    public static final BuiltinFunction APPEND = new BuiltinFunction(
            "append",
            args -> {
                if (args.length != 2) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 2", args.length);
                }

                EvaluatedObject container = args[0];
                EvaluatedObject element = args[1];

                if (container instanceof ArrayObject arrayObject) {
                    List<EvaluatedObject> arrayElements = new ArrayList<>(arrayObject.getElements());
                    arrayElements.addLast(element);
                    return new ArrayObject(arrayElements);
                } else if (container instanceof StringObject containerStringObject) {
                    if (element instanceof StringObject elementStringObject) {
                        return containerStringObject.concat(elementStringObject);
                    } else {
                        return new StringObject(containerStringObject.getValue() + element.toString());
                    }
                } else {
                    return Utils.unexpectedObjectTypeError(
                            "Unexpected type of first argument: expected %s, got %s",
                            container.getType(),
                            ObjectTypes.ARRAY,
                            ObjectTypes.STRING
                    );
                }
            },
            """
                    append(x -> array|string, y: any) -> array|string: returns a new object with the new element 'y' added as last element of 'x'
                    """
    );
    public static final BuiltinFunction POP = new BuiltinFunction(
            "pop",
            args -> {
                if (args.length != 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 1", args.length);
                }

                EvaluatedObject container = args[0];

                if (container instanceof ArrayObject arrayObject) {
                    List<EvaluatedObject> arrayElements = arrayObject.getElements();

                    return !arrayElements.isEmpty() ? arrayElements.removeFirst() : NULL;
                } else if (container instanceof StringObject stringObject) {
                    String stringValue = stringObject.getValue();

                    if (!stringValue.isEmpty()) {
                        char firstChar = stringValue.charAt(0);
                        stringObject.setValue(stringValue.substring(1));
                        return new StringObject(String.valueOf(firstChar));
                    } else {
                        return NULL;
                    }
                } else {
                    return Utils.unexpectedObjectTypeError(
                            "Unexpected type of first argument: expected %s, got %s",
                            container.getType(),
                            ObjectTypes.ARRAY,
                            ObjectTypes.STRING
                    );
                }
            },
            """
                    pop(x -> array|string) -> any|string: removes the first element from 'x' and returns it
                    """
    );
    public static final BuiltinFunction REMOVE_LAST = new BuiltinFunction(
            "removeLast",
            args -> {
                if (args.length != 1) {
                    return new ErrorObject("Unexpected number of arguments: got %d, must be 1", args.length);
                }

                EvaluatedObject container = args[0];

                if (container instanceof ArrayObject arrayObject) {
                    List<EvaluatedObject> arrayElements = arrayObject.getElements();

                    return !arrayElements.isEmpty() ? arrayElements.removeLast() : NULL;
                } else if (container instanceof StringObject stringObject) {
                    String stringValue = stringObject.getValue();

                    if (!stringValue.isEmpty()) {
                        char lastChar = stringValue.charAt(stringValue.length() - 1);
                        stringObject.setValue(stringValue.substring(0, stringValue.length() - 1));
                        return new StringObject(String.valueOf(lastChar));
                    } else {
                        return NULL;
                    }
                } else {
                    return Utils.unexpectedObjectTypeError(
                            "Unexpected type of first argument: expected %s, got %s",
                            container.getType(),
                            ObjectTypes.ARRAY,
                            ObjectTypes.STRING
                    );
                }
            },
            """
                    removeLast(x -> array|string) -> any|string: removes the last element from 'x' and returns it
                    """
    );

    private Objects() {
    }
}
