package org.ricdip.interpreters.simpleinterpreter.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.ricdip.interpreters.simpleinterpreter.evaluator.object.EvaluatedObject;
import org.ricdip.interpreters.simpleinterpreter.lexer.Lexer;
import org.ricdip.interpreters.simpleinterpreter.parser.Parser;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.Program;

import java.util.Optional;
import java.util.stream.Stream;

class EvaluatorTest {
    @ParameterizedTest
    @MethodSource("provideInput")
    void validInput(String inputString, String expectedOutput) {
        Lexer lexer = new Lexer(inputString);

        Parser parser = new Parser(lexer);

        Optional<Program> program = parser.parse();

        Assertions.assertTrue(program.isPresent());

        Evaluator evaluator = new Evaluator();
        Environment environment = new Environment();

        EvaluatedObject result = evaluator.eval(program.get(), environment);

        Assertions.assertEquals(
                expectedOutput.replaceAll("\\s+", ""),
                result.toString().replaceAll("\\s+", "")
        );
    }

    private static Stream<Arguments> provideInput() {
        return Stream.of(
                // let statement
                Arguments.of("let a = 2", """
                        null
                        """),
                // integer literal
                Arguments.of("1", """
                        1
                        """),
                Arguments.of("1250", """
                        1250
                        """),
                // boolean literal
                Arguments.of("true", """
                        true
                        """),
                Arguments.of("false", """
                        false
                        """),
                // infix expression
                Arguments.of("1+2", """
                        3
                        """),
                Arguments.of("1-2", """
                        -1
                        """),
                Arguments.of("2*3", """
                        6
                        """),
                Arguments.of("4/2", """
                        2
                        """),
                Arguments.of("1<2", """
                        true
                        """),
                Arguments.of("1>2", """
                        false
                        """),
                Arguments.of("1==2", """
                        false
                        """),
                Arguments.of("2==2", """
                        true
                        """),
                Arguments.of("1!=2", """
                        true
                        """),
                Arguments.of("1<=2", """
                        true
                        """),
                Arguments.of("2<=2", """
                        true
                        """),
                Arguments.of("1>=2", """
                        false
                        """),
                Arguments.of("2>=2", """
                        true
                        """),
                // prefix expression
                Arguments.of("-1", """
                        -1
                        """),
                Arguments.of("!true", """
                        false
                        """),
                Arguments.of("!false", """
                        true
                        """),
                // precedences
                Arguments.of("-1+2", """
                        1
                        """),
                Arguments.of("1+2+3", """
                        6
                        """),
                Arguments.of("1+2*3", """
                        7
                        """),
                Arguments.of("1+9/3", """
                        4
                        """),
                Arguments.of("1+-9/3", """
                        -2
                        """),
                Arguments.of("false != !true", """
                        false
                        """),
                Arguments.of("1 + 2 == 3", """
                        true
                        """),
                Arguments.of("1 + 2>= 3 + 5", """
                        false
                        """),
                Arguments.of("let test = 1+5-2", """
                        null
                        """),
                // grouped expression
                Arguments.of("(1+2)+3", """
                        6
                        """),
                Arguments.of("1+(2+3)", """
                        6
                        """),
                Arguments.of("2*(2+3)", """
                        10
                        """),
                Arguments.of("2*(-2+3)", """
                        2
                        """),
                Arguments.of("let a = 1*(-2+3)", """
                        null
                        """),
                // identifier
                Arguments.of("let a = 2*(3+1) a", """
                        8
                        """),
                Arguments.of("let test = 5 let a = test a", """
                        5
                        """),
                // conditional expression
                Arguments.of("if(true) { 2 } else {3 }  ", """
                        2
                        """),
                Arguments.of("if(false) { 2 } else {3 }  ", """
                        3
                        """),
                Arguments.of("if(true) { 2 }  ", """
                        2
                        """),
                Arguments.of("if(false) { 2 }  ", """
                        null
                        """),
                Arguments.of("if(2+3 < 4) {1+2} else {4+4}  ", """
                        8
                        """),
                Arguments.of("if(-2+3 < 4) {1+2} else {4+4}  ", """
                        3
                        """),
                Arguments.of("if(!true) {-1+2} else {4+4*2}  ", """
                        12
                        """),
                // function expression
                Arguments.of("fn(){1+2}", """
                        fn () { ((1 + 2)) }
                        """),
                Arguments.of("fn(x){x+2}", """
                        fn (x) { ((x + 2)) }
                        """),
                Arguments.of("fn(x, y){x+y-1}", """
                        fn (x, y) { (((x + y) - 1)) }
                        """),
                Arguments.of("fn(x, y, z){x+y-z}", """
                        fn (x, y, z) { (((x + y) - z)) }
                        """),
                // call expression
                Arguments.of("let add = fn(){1} add()", """
                        1
                        """),
                Arguments.of("""
                        let add = fn(a){a+2}
                        let x = 3
                        
                        add(x)
                        """, """
                        5
                        """),
                Arguments.of("""
                        let add = fn(a, b){a+b}
                        let x = 1
                        let y = 2
                        
                        add(x, y)
                        """, """
                        3
                        """),
                Arguments.of("""
                        let add = fn(a, b, c){ a + b + c }
                        let x = 1
                        let y = 2
                        let z = 3
                        
                        add(x, y, z)
                        """, """
                        6
                        """),
                Arguments.of("""
                        let add = fn(a, b, c){
                            if(c) {
                                a
                            } else {
                                b
                            }
                        }
                  
                        let test = false
                        
                        add(1+2+3, -1, !test)
                        """, """
                        6
                        """),
                Arguments.of("""
                        let add = fn(a, b, c){
                            if(c) {
                                a
                            } else {
                                b
                            }
                        }
                  
                        let test = true
                        
                        add(1+2+3, -1-2, !test)
                        """, """
                        -3
                        """),
                Arguments.of("""
                        fn(x){x+1}(15)
                        """, """
                        16
                        """),
                Arguments.of("""
                        let adder = fn(x){
                            fn(n){
                                n + x
                            }
                        }
                        
                        adder(1)(15)
                        """, """
                        16
                        """),
                // return statement
                Arguments.of("""
                        1+2+3
                        return 10
                        15
                        """, """
                        10
                        """),
                Arguments.of("""
                        let test = fn(a, b){
                            if(a < b) {
                                1+2
                                return a+b
                                1000 + 100
                            } else {
                                1-2
                                return a-b
                                1000 - 100
                            }
                        }
                        
                        test(5, 10)
                        """, """
                        15
                        """),
                Arguments.of("""
                        let test = fn(a, b){
                            if(a < b) {
                                1+2
                                return a+b
                                1000 + 100
                            } else {
                                1-2
                                return a-b
                                1000 - 100
                            }
                        }
                        
                        test(10, 5)
                        """, """
                        5
                        """),
                Arguments.of("""
                        let test = fn(){
                            if(true) {
                                10+5
                                if(true) {
                                    return 1
                                }
                                return 2
                            }
                            return 3
                        }
                        
                        test()
                        """, """
                        1
                        """),
                Arguments.of("""
                        let test = fn(){
                            if(true) {
                                10+5
                                if(true) {
                                    return 1
                                }
                                return 2
                            }
                            return 3
                        }
                        
                        test()
                        5+5
                        """, """
                        10
                        """),
                Arguments.of("""
                        let test = fn(){
                            if(true) {
                                10+5
                                if(true) {
                                    return 1
                                }
                                return 2
                            }
                            return 3
                        }
                        
                        test() + 5
                        """, """
                        6
                        """),
                // closures
                Arguments.of("""
                        let adder = fn(x){
                            fn(n) {
                                n + x
                            }
                        }
                        
                        let adder2 = adder(2)
                        
                        adder2(6)
                        """, """
                        8
                        """),
                Arguments.of("""
                        let adder = fn(x){
                            return fn(n) {
                                return n + x
                            }
                        }
                        
                        let adder2 = adder(2)
                        
                        adder2(6)
                        """, """
                        8
                        """),
                // arrays
                Arguments.of("""
                        let a = [1,2,3]
                        
                        a
                        """, """
                        [1,2,3]
                        """),
                Arguments.of("""
                        let a = [1,2,3]
                        
                        a[0]
                        """, """
                        1
                        """),
                Arguments.of("""
                        let a = [1,2,3]
                        
                        a[-1]
                        """, """
                        3
                        """),
                Arguments.of("""
                        let a = [1+2*4,fn(x){x+1},!true]
                        
                        a[0]
                        """, """
                        9
                        """),
                Arguments.of("""
                        let a = [1+2*4,fn(x){x+1},!true]
                        
                        a[1](2)
                        """, """
                        3
                        """),
                Arguments.of("""
                        let a = [1+2*4,fn(x){x+1},!true]
                        
                        a[2]
                        """, """
                        false
                        """),
                Arguments.of("""
                        let a = [1+2*4,fn(x){x+1},!true, "test"]
                        
                        a[3]
                        """, """
                        "test"
                        """),
                Arguments.of("""
                        let a = fn(x){
                            return [x, x+1]
                        }
                        
                        a(2)[1]
                        """, """
                        3
                        """),
                Arguments.of("""
                        [1,2,3][0]
                        """, """
                        1
                        """),
                Arguments.of("""
                        let a = [[1,2,3],
                                [4,5,6]]
                        
                        a[0][2]
                        """, """
                        3
                        """),
                // strings and index strings
                Arguments.of("\"test\"", """
                        "test"
                        """),
                Arguments.of("\"hello world\"", """
                        "hello world"
                        """),
                Arguments.of("\"  hello  world   \"", """
                        "  hello  world   "
                        """),
                Arguments.of("\" hello world  \"", """
                        " hello world  "
                        """),
                Arguments.of("let a = \"test\" a", """
                        "test"
                        """),
                Arguments.of("\"test\"[1]", """
                        "e"
                        """),
                Arguments.of("\"test\"[-2]", """
                        "s"
                        """),
                // builtin functions
                Arguments.of("""
                        let array = [1,2,3]
                        
                        len(array)
                        """, """
                        3
                        """),
                Arguments.of("""
                        len([1,2,3])
                        """, """
                        3
                        """),
                Arguments.of("""
                        let string = "test"
                        
                        len(string)
                        """, """
                        4
                        """),
                Arguments.of("""
                        len("test")
                        """, """
                        4
                        """),
                Arguments.of("""
                        let array = [1,2,3]
                        
                        first(array)
                        """, """
                        1
                        """),
                Arguments.of("""
                        first([1,2,3])
                        """, """
                        1
                        """),
                Arguments.of("""
                        let array = [1]
                        
                        first(array)
                        """, """
                        1
                        """),
                Arguments.of("""
                        let array = []
                        
                        first(array)
                        """, """
                        null
                        """),
                Arguments.of("""
                        let str = "test"
                        
                        first(str)
                        """, """
                        "t"
                        """),
                Arguments.of("""
                        first("test")
                        """, """
                        "t"
                        """),
                Arguments.of("""
                        let str = "t"
                        
                        first(str)
                        """, """
                        "t"
                        """),
                Arguments.of("""
                        let str = ""
                        
                        first(str)
                        """, """
                        null
                        """),
                Arguments.of("""
                        let array = [1,2,3]
                        
                        rest(array)
                        """, """
                        [2,3]
                        """),
                Arguments.of("""
                        rest([1,2,3])
                        """, """
                        [2,3]
                        """),
                Arguments.of("""
                        let array = [1]
                        
                        rest(array)
                        """, """
                        []
                        """),
                Arguments.of("""
                        let array = []
                        
                        rest(array)
                        """, """
                        null
                        """),
                Arguments.of("""
                        let str = "test"
                        
                        rest(str)
                        """, """
                        "est"
                        """),
                Arguments.of("""
                        rest("test")
                        """, """
                        "est"
                        """),
                Arguments.of("""
                        let str = "t"
                        
                        rest(str)
                        """, """
                        ""
                        """),
                Arguments.of("""
                        let str = ""
                        
                        rest(str)
                        """, """
                        null
                        """),
                Arguments.of("""
                        let array = [2,3]
                        
                        push(array, 1)
                        """, """
                        [1,2,3]
                        """),
                Arguments.of("""
                        push([2,3], 1)
                        """, """
                        [1,2,3]
                        """),
                Arguments.of("""
                        let str = "bc"
                        
                        push("bc", "a")
                        """, """
                        "abc"
                        """),
                Arguments.of("""
                        push("bc", "a")
                        """, """
                        "abc"
                        """),
                Arguments.of("""
                        let array = [1,2]
                        
                        append(array, 3)
                        """, """
                        [1,2,3]
                        """),
                Arguments.of("""
                        append([1,2], 3)
                        """, """
                        [1,2,3]
                        """),
                Arguments.of("""
                        let str = "ab"
                        
                        append(str, "c")
                        """, """
                        "abc"
                        """),
                Arguments.of("""
                        append("ab", "c")
                        """, """
                        "abc"
                        """),
                Arguments.of("""
                        let array = [1,2]
                        
                        pop(array)
                        """, """
                        1
                        """),
                Arguments.of("""
                        let array = [1,2,3]
                        
                        pop(array)
                        array
                        """, """
                        [2,3]
                        """),
                Arguments.of("""
                        pop([])
                        """, """
                        null
                        """),
                Arguments.of("""
                        let str = "abc"
                        
                        pop(str)
                        """, """
                        "a"
                        """),
                Arguments.of("""
                        let str = "abc"
                        
                        pop(str)
                        str
                        """, """
                        "bc"
                        """),
                Arguments.of("""
                        let array = [1,2]
                        
                        removeLast(array)
                        """, """
                        2
                        """),
                Arguments.of("""
                        let array = [1,2,3]
                        
                        removeLast(array)
                        array
                        """, """
                        [1,2]
                        """),
                Arguments.of("""
                        removeLast([])
                        """, """
                        null
                        """),
                Arguments.of("""
                        let str = "abc"
                        
                        removeLast(str)
                        """, """
                        "c"
                        """),
                Arguments.of("""
                        let str = "abc"
                        
                        removeLast(str)
                        str
                        """, """
                        "ab"
                        """),
                // postfix operators
                Arguments.of("""
                        let a = 1
                        
                        a++
                        a
                        """, """
                        2
                        """),
                Arguments.of("""
                        let a = 2
                        
                        a--
                        a
                        """, """
                        1
                        """),
                // more complex code
                Arguments.of("""
                        let map = fn(array, func){
                            let acc = fn(curr, res) {
                                if(len(curr) == 0) {
                                    res
                                } else {
                                    acc(rest(curr), append(res, func(first(curr))))
                                }
                            }
                            acc(array, [])
                        }
                        
                        let array = [1,2,3,4,5]
                        let double = fn(x){x*x}
                        
                        map(array, double)
                        """, """
                        [1,4,9,16,25]
                        """),
                Arguments.of("""
                        let fib = fn(n) {
                            if(n == 0) {
                                return 0
                            }
                        
                            if(n == 1) {
                                return 1
                            }
                        
                            return fib(n - 1) + fib(n - 2)
                        }
                        
                        fib(12)
                        """, """
                        144
                        """),
                Arguments.of("""
                        let fact = fn(n) {
                            if(n == 0) {
                                return 1
                            } else {
                                return n * fact(n - 1)
                            }
                        }
                        
                        fact(5)
                        """, """
                        120
                        """)
        );
    }
}