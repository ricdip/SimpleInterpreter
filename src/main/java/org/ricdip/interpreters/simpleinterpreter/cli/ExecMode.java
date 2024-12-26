package org.ricdip.interpreters.simpleinterpreter.cli;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import picocli.CommandLine;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecMode {
    @CommandLine.Option(names = {"-ml", "--mode-lexer"}, description = "Enable lexer mode.")
    private boolean rlpl;
    @CommandLine.Option(names = {"-mp", "--mode-parse"}, description = "Enable parse mode.")
    private boolean rppl;
    @CommandLine.Option(names = {"-me", "--mode-evaluator"}, description = "Enable evaluator mode [default].")
    private boolean repl;

    public ExecModeTypes getExecMode() {
        if (rlpl) {
            return ExecModeTypes.LEXER;
        } else if (rppl) {
            return ExecModeTypes.PARSER;
        } else if (repl) {
            return ExecModeTypes.EVALUATOR;
        } else {
            return ExecModeTypes.EVALUATOR;
        }
    }
}
