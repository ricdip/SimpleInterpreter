package org.ricdip.interpreters.simpleinterpreter.cli;


import org.ricdip.interpreters.simpleinterpreter.evaluator.REPL;
import org.ricdip.interpreters.simpleinterpreter.lexer.RLPL;
import org.ricdip.interpreters.simpleinterpreter.parser.RPPL;
import picocli.CommandLine;

import java.io.PrintWriter;

@CommandLine.Command(
        name = "<jar file name>",
        versionProvider = BuildInfoVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "A simple language interpreter written in Java"
)
public class CLIApplication implements Runnable {

    @CommandLine.ArgGroup
    private final ExecMode execMode = new ExecMode();

    @Override
    public void run() {
        switch (execMode.getExecMode()) {
            case LEXER: {
                RLPL.start(System.in, new PrintWriter(System.out));
                break;
            }
            case PARSER: {
                RPPL.start(System.in, new PrintWriter(System.out));
                break;
            }
            case EVALUATOR: {
                REPL.start(System.in, new PrintWriter(System.out));
                break;
            }
        }
    }
}
