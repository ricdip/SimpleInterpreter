package org.ricdip.interpreters.simpleinterpreter.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.ricdip.interpreters.simpleinterpreter.lexer.Lexer;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.Program;

import java.util.Optional;
import java.util.stream.Stream;

class ParserTest {
    @ParameterizedTest
    @MethodSource("provideInput")
    void validInput(String inputString, String expectedASTString) {
        Lexer lexer = new Lexer(inputString);

        Parser parser = new Parser(lexer);

        Optional<Program> result = parser.parse();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(
                expectedASTString.replaceAll("\\s+", ""),
                result.get().toString().replaceAll("\\s+", "")
        );
    }

    private static Stream<Arguments> provideInput() {
        return Stream.of(
                // let statement
                Arguments.of("let a = 2", """
                        {
                            (a = 2)
                        }
                        """),
                // integer literal
                Arguments.of("1", """
                        {
                            (1)
                        }
                        """),
                Arguments.of("1250", """
                        {
                            (1250)
                        }
                        """),
                // boolean literal
                Arguments.of("true", """
                        {
                            (true)
                        }
                        """),
                Arguments.of("false", """
                        {
                            (false)
                        }
                        """),
                // infix expression
                Arguments.of("1+2", """
                        {
                            ((1 + 2))
                        }
                        """),
                Arguments.of("1-2", """
                        {
                            ((1 - 2))
                        }
                        """),
                Arguments.of("1*2", """
                        {
                            ((1 * 2))
                        }
                        """),
                Arguments.of("1/2", """
                        {
                            ((1 / 2))
                        }
                        """),
                Arguments.of("1<2", """
                        {
                            ((1 < 2))
                        }
                        """),
                Arguments.of("1>2", """
                        {
                            ((1 > 2))
                        }
                        """),
                Arguments.of("1==2", """
                        {
                            ((1 == 2))
                        }
                        """),
                Arguments.of("1!=2", """
                        {
                            ((1 != 2))
                        }
                        """),
                Arguments.of("1<=2", """
                        {
                            ((1 <= 2))
                        }
                        """),
                Arguments.of("1>=2", """
                        {
                            ((1 >= 2))
                        }
                        """),
                // prefix expression
                Arguments.of("-1", """
                        {
                            ((-1))
                        }
                        """),
                Arguments.of("!true", """
                        {
                            ((!true))
                        }
                        """),
                // precedences
                Arguments.of("-1+2", """
                        {
                            (((-1) + 2))
                        }
                        """),
                Arguments.of("-true+2", """
                        {
                            (((-true) + 2))
                        }
                        """),
                Arguments.of("1+2+3", """
                        {
                            (((1 + 2) + 3))
                        }
                        """),
                Arguments.of("1+2*3", """
                        {
                            ((1 + (2 * 3)))
                        }
                        """),
                Arguments.of("1+2/3", """
                        {
                            ((1 + (2 / 3)))
                        }
                        """),
                Arguments.of("1+-2/3", """
                        {
                            ((1 + ((-2) / 3)))
                        }
                        """),
                Arguments.of("false != !true", """
                        {
                            ((false != (!true)))
                        }
                        """),
                Arguments.of("1 <= 3 < 5", """
                        {
                            (((1 <= 3) < 5))
                        }
                        """),
                Arguments.of("1 >= 3 > 5", """
                        {
                            (((1 >= 3) > 5))
                        }
                        """),
                Arguments.of("let test = 1+5-2", """
                        {
                            (test = ((1 + 5) - 2))
                        }
                        """),
                // grouped expression
                Arguments.of("(1+2)+3", """
                        {
                            (((1 + 2) + 3))
                        }
                        """),
                Arguments.of("1+(2+3)", """
                        {
                            ((1 + (2 + 3)))
                        }
                        """),
                Arguments.of("1*(2+3)", """
                        {
                            ((1 * (2 + 3)))
                        }
                        """),
                Arguments.of("1*(-2+3)", """
                        {
                            ((1 * ((-2) + 3)))
                        }
                        """),
                Arguments.of("let a = 1*(-2+3)", """
                        {
                            (a = (1 * ((-2) + 3)))
                        }
                        """),
                // identifier
                Arguments.of("a", """
                        {
                            (a)
                        }
                        """),
                Arguments.of("let a = test", """
                        {
                            (a = test)
                        }
                        """),
                // conditional expression
                Arguments.of("if(true) { 2 } else {3 }  ", """
                        {
                            (if (true) { (2) } else { (3) })
                        }
                        """),
                Arguments.of("if(2+3 < 4) {1+2} else {3+4}  ", """
                        {
                            (if (((2 + 3) < 4)) { ((1 + 2)) } else { ((3 + 4)) })
                        }
                        """),
                Arguments.of("if(-2+3 < 4) {1+2} else {3+4}  ", """
                        {
                            (if ((((-2) + 3) < 4)) { ((1 + 2)) } else { ((3 + 4)) })
                        }
                        """),
                Arguments.of("if(!true) {-1+2} else {3+4*2}  ", """
                        {
                            (if ((!true)) { (((-1) + 2)) } else { ((3 + (4 * 2))) })
                        }
                        """),
                // function expression
                Arguments.of("fn(){1+2}", """
                        {
                            (fn() { ((1 + 2)) })
                        }
                        """),
                Arguments.of("fn(x){x+2}", """
                        {
                            (fn(x) { ((x + 2)) })
                        }
                        """),
                Arguments.of("fn(x, y){x+y-1}", """
                        {
                            (fn(x, y) { (((x + y) - 1)) })
                        }
                        """),
                Arguments.of("fn(x, y, z){x+y-z}", """
                        {
                            (fn(x, y, z) { (((x + y) - z)) })
                        }
                        """),
                // call expression
                Arguments.of("add()", """
                        {
                            (add())
                        }
                        """),
                Arguments.of("add(x)", """
                        {
                            (add(x))
                        }
                        """),
                Arguments.of("add(x, y)", """
                        {
                            (add(x, y))
                        }
                        """),
                Arguments.of("add(x, y, z)", """
                        {
                            (add(x, y, z))
                        }
                        """),
                Arguments.of("add(1+2+3, -1, !test)", """
                        {
                            (add(((1 + 2) + 3), (-1), (!test)))
                        }
                        """),
                Arguments.of("fn(x){x+2}(1)", """
                        {
                            (fn(x) { ((x + 2)) }(1))
                        }
                        """),
                Arguments.of("adder(1)(2)", """
                        {
                            (adder(1)(2))
                        }
                        """),
                // return expression
                Arguments.of("return 2", """
                        {
                            (return 2)
                        }
                        """),
                Arguments.of("return 1+2", """
                        {
                            (return (1+2))
                        }
                        """),
                Arguments.of("1 2 return 3+4+5 6 7 8", """
                        {
                            (1)
                            (2)
                            (return ((3 + 4) + 5))
                            (6)
                            (7)
                            (8)
                        }
                        """),
                // arrays and index arrays
                Arguments.of("let a = [1,2]", """
                        {
                            (a = [1,2])
                        }
                        """),
                Arguments.of("a[0]", """
                        {
                            (a[0])
                        }
                        """),
                Arguments.of("add(1)[2]", """
                        {
                            (add(1)[2])
                        }
                        """),
                Arguments.of("[1,2,3][0]", """
                        {
                            ([1,2,3][0])
                        }
                        """),
                Arguments.of("a[0][1]", """
                        {
                            (a[0][1])
                        }
                        """),
                Arguments.of("add[0](1)", """
                        {
                            (add[0](1))
                        }
                        """),
                // strings and index strings
                Arguments.of("\"test\"", """
                        {
                            ("test")
                        }
                        """),
                Arguments.of("\"hello world\"", """
                        {
                            ("hello world")
                        }
                        """),
                Arguments.of("\"  hello  world   \"", """
                        {
                            ("  hello  world   ")
                        }
                        """),
                Arguments.of("1 \" hello world  \" 2 3", """
                        {
                            (1)
                            (" hello world  ")
                            (2)
                            (3)
                        }
                        """),
                Arguments.of("let a = \"test\"", """
                        {
                            (a = "test")
                        }
                        """),
                Arguments.of("\"test\"[0]", """
                        {
                            ("test"[0])
                        }
                        """),
                // postfix expression
                Arguments.of("a++", """
                        {
                            ((a++))
                        }
                        """),
                Arguments.of("-a++", """
                        {
                            ((-(a++)))
                        }
                        """),
                Arguments.of("a++ + 5", """
                        {
                            (((a++) + 5))
                        }
                        """),
                Arguments.of("a++ * 5", """
                        {
                            (((a++) * 5))
                        }
                        """),
                Arguments.of("a++ + 5 * 2", """
                        {
                            (((a++) + (5 * 2)))
                        }
                        """),
                Arguments.of("a--", """
                        {
                            ((a--))
                        }
                        """),
                Arguments.of("-a--", """
                        {
                            ((-(a--)))
                        }
                        """),
                Arguments.of("a-- + 5", """
                        {
                            (((a--) + 5))
                        }
                        """),
                Arguments.of("a-- * 5", """
                        {
                            (((a--) * 5))
                        }
                        """),
                Arguments.of("a-- + 5 * 2", """
                        {
                            (((a--) + (5 * 2)))
                        }
                        """)
        );
    }
}