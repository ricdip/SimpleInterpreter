package org.ricdip.interpreters.simpleinterpreter.lexer;

import org.apache.commons.lang3.StringUtils;
import org.ricdip.interpreters.simpleinterpreter.Constants;
import org.ricdip.interpreters.simpleinterpreter.utils.Utils;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public final class RLPL {
    private RLPL() {
    }

    public static void start(InputStream in, PrintWriter out) {
        Scanner scanner = new Scanner(in);
        while (true) {
            Utils.print(out, Constants.PROMPT);
            String line = scanner.nextLine();

            if (StringUtils.isBlank(line)) {
                break;
            }

            Lexer lexer = new Lexer(line);

            while (lexer.hasNext()) {
                Utils.println(out, lexer.next());
            }

            Utils.println(out, "");
        }
    }
}
