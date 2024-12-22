package org.ricdip.interpreters.simpleinterpreter.parser;

import org.apache.commons.lang3.StringUtils;
import org.ricdip.interpreters.simpleinterpreter.Constants;
import org.ricdip.interpreters.simpleinterpreter.lexer.Lexer;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.Program;
import org.ricdip.interpreters.simpleinterpreter.utils.Utils;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.Scanner;

public final class RPPL {
    private RPPL() {
    }

    public static void start(InputStream in, PrintWriter out) {
        Scanner scanner = new Scanner(in);
        String line = "";

        while (true) {
            if (StringUtils.isBlank(line)) {
                Utils.print(out, Constants.PROMPT);
            } else {
                Utils.print(out, Constants.PROMPT_CONTINUE);
            }

            line = line + " " + scanner.nextLine();

            if (StringUtils.isBlank(line)) {
                break;
            } else if (Utils.containsOpenParentheses(line)) {
                continue;
            }

            Lexer lexer = new Lexer(line);

            Parser parser = new Parser(lexer);

            Optional<Program> program = parser.parse();

            if (!parser.getErrors().isEmpty()) {
                Utils.printList(out, parser.getErrors());
            }

            if (program.isPresent()) {
                Utils.println(out, program.get());
            }

            line = "";
        }
    }
}
