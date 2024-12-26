package org.ricdip.interpreters.simpleinterpreter.cli;


import org.apache.commons.lang3.StringUtils;
import org.ricdip.interpreters.simpleinterpreter.evaluator.REPL;
import org.ricdip.interpreters.simpleinterpreter.lexer.RLPL;
import org.ricdip.interpreters.simpleinterpreter.parser.RPPL;
import org.ricdip.interpreters.simpleinterpreter.utils.Utils;
import picocli.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@CommandLine.Command(
        name = "<jar file name>",
        versionProvider = BuildInfoVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "A simple language interpreter written in Java"
)
public class CLIApplication implements Runnable {

    @CommandLine.ArgGroup
    private final ExecMode execMode = new ExecMode();

    @CommandLine.Option(names = {"-f", "--file"}, description = "Read program from file path")
    private String filePath = "";

    @Override
    public void run() {
        InputStream inputStream = System.in;

        if (StringUtils.isNotBlank(filePath)) {
            String fileContent = Utils.readProgramFromFile(filePath);
            inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        }

        switch (execMode.getExecMode()) {
            case LEXER: {
                RLPL.start(inputStream, new PrintWriter(System.out));
                break;
            }
            case PARSER: {
                RPPL.start(inputStream, new PrintWriter(System.out));
                break;
            }
            case EVALUATOR: {
                REPL.start(inputStream, new PrintWriter(System.out));
                break;
            }
        }
    }
}
