package org.ricdip.interpreters.simpleinterpreter.utils;

import org.apache.commons.io.FileUtils;
import org.ricdip.interpreters.simpleinterpreter.evaluator.object.ErrorObject;
import org.ricdip.interpreters.simpleinterpreter.evaluator.object.ObjectTypes;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public final class Utils {
    private Utils() {
    }

    public static void print(PrintWriter out, Object object) {
        out.print(object.toString());
        out.flush();
    }

    public static void println(PrintWriter out, Object object) {
        out.println(object.toString());
        out.flush();
    }

    public static void printList(PrintWriter out, List<String> list) {
        if (!list.isEmpty()) {
            for (String line : list) {
                out.println(line);
            }
            out.flush();
        }
    }

    public static boolean containsOpenParentheses(String line) {
        int nParen = 0;
        int nBrace = 0;
        int nSquare = 0;

        for (char character : line.toCharArray()) {
            switch (character) {
                case '(':
                    nParen++;
                    break;
                case ')':
                    nParen--;
                    break;
                case '[':
                    nSquare++;
                    break;
                case ']':
                    nSquare--;
                    break;
                case '{':
                    nBrace++;
                    break;
                case '}':
                    nBrace--;
                    break;

                default:
            }
        }

        return nParen != 0 || nBrace != 0 || nSquare != 0;
    }

    public static ErrorObject unexpectedObjectTypeError(ObjectTypes unexpectedType, ObjectTypes... expectedObjectTypes) {
        return new ErrorObject(
                "Unexpected type of argument: expected %s, got %s",
                String.join(" or ", Stream.of(expectedObjectTypes).map(Enum::name).toList()),
                unexpectedType
        );
    }

    public static ErrorObject unexpectedObjectTypeError(String message, ObjectTypes unexpectedType, ObjectTypes... expectedObjectTypes) {
        return new ErrorObject(
                message,
                String.join(" or ", Stream.of(expectedObjectTypes).map(Enum::name).toList()),
                unexpectedType
        );
    }

    public static String readProgramFromFile(String filePath) {
        File file = new File(filePath);

        try {
            // read from file and replace all line separators with one line separator
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8).replaceAll("\\R+", "\n");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred during file reading", e);
        }
    }
}
