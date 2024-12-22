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
                    return !stringValue.isEmpty() ? new StringObject(String.valueOf(stringValue.charAt(0))) : new StringObject("");
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

                    return !arrayElements.isEmpty() ? new ArrayObject(arrayElements.subList(1, arrayElements.size())) : new ArrayObject(new ArrayList<>());
                } else if (arg instanceof StringObject stringObject) {
                    String stringValue = stringObject.getValue();

                    return !stringValue.isEmpty() ? new StringObject(stringValue.substring(1)) : new StringObject("");
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
                } else if (container instanceof StringObject stringObject) {
                    StringBuilder stringBuilder = new StringBuilder(stringObject.getValue());

                    stringBuilder.insert(0, element.toString());

                    return new StringObject(stringBuilder.toString());
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
                } else if (container instanceof StringObject stringObject) {
                    StringBuilder stringBuilder = new StringBuilder(stringObject.getValue());

                    stringBuilder.append(element.toString());

                    return new StringObject(stringBuilder.toString());
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

    private Objects() {
    }
}
