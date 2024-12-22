package org.ricdip.interpreters.simpleinterpreter.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Token {
    private final TokenType type;
    private final String lexeme;

    @Override
    public String toString() {
        return String.format("(%s, '%s')", type.name(), lexeme);
    }
}
