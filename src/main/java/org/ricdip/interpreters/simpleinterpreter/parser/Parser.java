package org.ricdip.interpreters.simpleinterpreter.parser;

import lombok.Getter;
import lombok.NonNull;
import org.ricdip.interpreters.simpleinterpreter.exception.ParserException;
import org.ricdip.interpreters.simpleinterpreter.lexer.Lexer;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.CallableExpression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Expression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.IndexableExpression;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.Statement;
import org.ricdip.interpreters.simpleinterpreter.parser.ast.impl.*;
import org.ricdip.interpreters.simpleinterpreter.symbol.Symbol;
import org.ricdip.interpreters.simpleinterpreter.token.Token;
import org.ricdip.interpreters.simpleinterpreter.token.TokenType;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    private static final Token EOF_TOKEN = new Token(TokenType.EOF, String.valueOf(Symbol.EOF));

    @Getter
    private final List<String> errors = new ArrayList<>();
    private final Lexer lexer;
    private final Map<TokenType, Supplier<Expression>> prefixParseFunctionMap = new HashMap<>();
    private final Map<TokenType, Function<Expression, Expression>> infixParseFunctionMap = new HashMap<>();
    private final Map<TokenType, Precedence> infixOperatorPrecedenceMap = new HashMap<>();
    private Token currentToken;
    private Token peekToken;

    public Parser(@NonNull Lexer lexer) {
        this.lexer = lexer;

        // prefix parse function table
        prefixParseFunctionMap.put(TokenType.INT, this::parseExpressionInteger);
        prefixParseFunctionMap.put(TokenType.MINUS, this::parseExpressionPrefix);
        prefixParseFunctionMap.put(TokenType.TRUE, this::parseExpressionBoolean);
        prefixParseFunctionMap.put(TokenType.FALSE, this::parseExpressionBoolean);
        prefixParseFunctionMap.put(TokenType.NEG, this::parseExpressionPrefix);
        prefixParseFunctionMap.put(TokenType.LPAREN, this::parseGroupedExpression);
        prefixParseFunctionMap.put(TokenType.IDENTIFIER, this::parseIdentifierExpression);
        prefixParseFunctionMap.put(TokenType.IF, this::parseConditionalExpression);
        prefixParseFunctionMap.put(TokenType.FUNCTION, this::parseFunctionExpression);
        prefixParseFunctionMap.put(TokenType.LSQUARE, this::parseArrayExpression);
        prefixParseFunctionMap.put(TokenType.STRING, this::parseStringExpression);

        // infix parse function table
        infixParseFunctionMap.put(TokenType.PLUS, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.MINUS, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.ASTERISK, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.SLASH, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.LT, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.GT, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.EQ, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.NEQ, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.LTEQ, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.GTEQ, this::parseExpressionInfix);
        infixParseFunctionMap.put(TokenType.LPAREN, this::parseCallExpression);
        infixParseFunctionMap.put(TokenType.LSQUARE, this::parseIndexExpression);

        // operator precedence map for infix expressions
        infixOperatorPrecedenceMap.put(Operator.LT.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.GT.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.EQ.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.NEQ.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.LTEQ.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.GTEQ.getTokenType(), Precedence.COMPARISON);
        infixOperatorPrecedenceMap.put(Operator.PLUS.getTokenType(), Precedence.SUMMATION);
        infixOperatorPrecedenceMap.put(Operator.MINUS.getTokenType(), Precedence.SUMMATION);
        infixOperatorPrecedenceMap.put(Operator.ASTERISK.getTokenType(), Precedence.MULTIPLICATION);
        infixOperatorPrecedenceMap.put(Operator.SLASH.getTokenType(), Precedence.MULTIPLICATION);
        infixOperatorPrecedenceMap.put(Operator.CALL.getTokenType(), Precedence.CALL);
        infixOperatorPrecedenceMap.put(Operator.INDEX.getTokenType(), Precedence.INDEX);

        // prepare lexer before starting
        nextToken();
    }

    private void nextToken() {
        if (!EOF_TOKEN.equals(currentToken)) {
            currentToken = peekToken;
            peekToken = lexer.hasNext() ? lexer.next() : EOF_TOKEN;
        } else {
            throw new ParserException("No tokens left");
        }
    }

    public Optional<Program> parse() {
        Program program = new Program();

        while (!EOF_TOKEN.equals(peekToken)) {
            nextToken();

            Statement statement = parseStatement();

            if (statement != null) {
                program.getStatements().add(statement);
            } else {
                break;
            }
        }

        if (!EOF_TOKEN.equals(peekToken)) {
            addError(
                    "An error occurred while parsing program: current token is %s, peek token is %s",
                    currentToken.toString(),
                    peekToken.toString()
            );
            return Optional.empty();
        }

        return Optional.of(program);
    }

    /**
     * Parses a statement
     *
     * @return the created {@link Statement}
     */
    private Statement parseStatement() {
        switch (currentToken.getType()) {
            case LET:
                return parseLetStatement();
            case RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    /**
     * Parses a let statement: {@code let <identifier> = <expression>}
     *
     * @return the created {@link LetStatement}
     */
    private LetStatement parseLetStatement() {
        if (!expectToken(currentToken, TokenType.LET)) {
            addUnexpectedTokenError(TokenType.LET);
            return null;
        }

        nextToken(); // let -> identifier

        IdentifierExpression identifier = parseIdentifierExpression();

        nextToken(); // identifier -> =

        if (!expectToken(currentToken, TokenType.ASSIGN)) {
            addUnexpectedTokenError(TokenType.ASSIGN);
            return null;
        }

        nextToken(); // = -> expression

        Expression expression = parseExpression(Precedence.LOWEST);

        return new LetStatement(identifier, expression);
    }

    /**
     * Parses an expression statement
     *
     * @return the created {@link ExpressionStatement}
     */
    private ExpressionStatement parseExpressionStatement() {
        Expression expression = parseExpression(Precedence.LOWEST);

        if (expression == null) {
            return null;
        }

        return new ExpressionStatement(expression);
    }

    /**
     * Parses an expression
     *
     * @param precedence a {@link Precedence} value for the current expression
     * @return the created {@link Expression}
     */
    private Expression parseExpression(Precedence precedence) {
        Supplier<Expression> prefixFn = prefixParseFunctionMap.get(currentToken.getType());

        if (prefixFn == null) {
            addError("Unknown prefix function for expression: %s", currentToken);
            return null;
        }

        Expression left = prefixFn.get();

        while (precedence.ordinal() < infixOperatorPrecedenceMap.getOrDefault(peekToken.getType(), Precedence.LOWEST).ordinal()) {
            nextToken(); // left -> operator

            Function<Expression, Expression> infixFn = infixParseFunctionMap.get(currentToken.getType());

            if (infixFn == null) {
                addError("Unknown infix function for expression: %s", currentToken);
                return null;
            }

            left = infixFn.apply(left);
        }

        return left;
    }

    /**
     * Parses an integer: {@code 1}
     *
     * @return the created {@link IntegerLiteral}
     */
    private IntegerLiteral parseExpressionInteger() {
        if (!expectToken(currentToken, TokenType.INT)) {
            addUnexpectedTokenError(TokenType.INT);
            return null;
        }

        int value = 0;

        try {
            value = Integer.parseInt(currentToken.getLexeme());
        } catch (NumberFormatException e) {
            addError("not valid integer in current token: %s", currentToken);
            return null;
        }

        return new IntegerLiteral(value);
    }

    /**
     * Parses a boolean: {@code true}
     *
     * @return the created {@link IntegerLiteral}
     */
    private BooleanLiteral parseExpressionBoolean() {
        if (!expectToken(currentToken, TokenType.TRUE) && !expectToken(currentToken, TokenType.FALSE)) {
            addUnexpectedTokenError(TokenType.TRUE, TokenType.FALSE);
            return null;
        }

        boolean value = "true".equals(currentToken.getLexeme());

        return new BooleanLiteral(value);
    }

    /**
     * Parses an infix expression: {@code 1+2}
     *
     * @param left the left-hand side of the infix expression
     * @return the created {@link InfixExpression}
     */
    private InfixExpression parseExpressionInfix(Expression left) {
        Optional<Operator> optionalOperator = Operator.fromToken(currentToken.getType());

        if (optionalOperator.isEmpty()) {
            addError("current token %s is not a valid operator", currentToken);
            return null;
        }

        Operator operator = optionalOperator.get();

        nextToken(); // operator -> right

        Expression right = parseExpression(infixOperatorPrecedenceMap.getOrDefault(operator.getTokenType(), Precedence.LOWEST));

        return new InfixExpression(left, operator, right);
    }

    /**
     * Parses a prefix expression: {@code -1}
     *
     * @return the created {@link PrefixExpression}
     */
    private PrefixExpression parseExpressionPrefix() {
        Optional<Operator> optionalOperator = Operator.fromToken(currentToken.getType());

        if (optionalOperator.isEmpty()) {
            addError("current token %s is not a valid operator", currentToken);
            return null;
        }

        Operator operator = optionalOperator.get();

        nextToken(); // operator -> right

        Expression right = parseExpression(Precedence.PREFIX);

        return new PrefixExpression(operator, right);
    }

    /**
     * Parses a grouped expression: {@code 2*(3+4)}
     *
     * @return the created {@link Expression}
     */
    private Expression parseGroupedExpression() {
        if (!expectToken(currentToken, TokenType.LPAREN)) {
            addUnexpectedTokenError(TokenType.LPAREN);
            return null;
        }

        nextToken(); // ( -> expression

        Expression expression = parseExpression(Precedence.LOWEST);

        nextToken(); // expression -> )

        if (!expectToken(currentToken, TokenType.RPAREN)) {
            addUnexpectedTokenError(TokenType.RPAREN);
            return null;
        }

        return expression;
    }

    /**
     * Parses an identifier: {@code ident}
     *
     * @return the created {@link IdentifierExpression}
     */
    private IdentifierExpression parseIdentifierExpression() {
        if (!expectToken(currentToken, TokenType.IDENTIFIER)) {
            addUnexpectedTokenError(TokenType.IDENTIFIER);
            return null;
        }

        return new IdentifierExpression(currentToken.getLexeme());
    }

    /**
     * Parses a conditional expression: {@code if (<expression>) <block-statement> [else <block-statement>] }
     *
     * @return the created {@link ConditionalExpression}
     */
    private ConditionalExpression parseConditionalExpression() {
        if (!expectToken(currentToken, TokenType.IF)) {
            addUnexpectedTokenError(TokenType.IF);
            return null;
        }

        nextToken(); // if -> (

        if (!expectToken(currentToken, TokenType.LPAREN)) {
            addUnexpectedTokenError(TokenType.LPAREN);
            return null;
        }

        nextToken(); // ( -> condition expression

        Expression condition = parseExpression(Precedence.LOWEST);

        nextToken(); // expression -> )

        if (!expectToken(currentToken, TokenType.RPAREN)) {
            addUnexpectedTokenError(TokenType.RPAREN);
            return null;
        }

        nextToken(); // ) -> {

        BlockStatement ifBranch = parseBlockStatement();

        if (expectToken(peekToken, TokenType.ELSE)) {
            nextToken(); // } -> else
            nextToken(); // else -> {

            BlockStatement elseBranch = parseBlockStatement();

            return new ConditionalExpression(condition, ifBranch, elseBranch);
        } else {
            return new ConditionalExpression(condition, ifBranch);
        }
    }

    /**
     * Parses a function expression: {@code fn ([<expression>, <expression>, ...]) <block-statement> }
     *
     * @return the created {@link FunctionExpression}
     */
    private FunctionExpression parseFunctionExpression() {
        if (!expectToken(currentToken, TokenType.FUNCTION)) {
            addUnexpectedTokenError(TokenType.FUNCTION);
            return null;
        }

        nextToken(); // fn -> (

        List<IdentifierExpression> parameters = parseFunctionParameters();

        nextToken(); // args -> )

        if (!TokenType.RPAREN.equals(currentToken.getType())) {
            addUnexpectedTokenError(TokenType.RPAREN);
            return null;
        }

        nextToken(); // ) -> {

        BlockStatement functionBody = parseBlockStatement();

        return new FunctionExpression(parameters, functionBody);
    }

    /**
     * Parses a call expression: {@code <callable>([<expression>, <expression>, ...]) }
     *
     * @param left the left-hand side of the call expression
     * @return the created {@link CallExpression}
     */
    private CallExpression parseCallExpression(Expression left) {
        if (!(left instanceof CallableExpression)) {
            addError("Expected callable expression in left-hand side of call expression, got '%s'", left);
            return null;
        }

        List<Expression> arguments = parseCallArguments();

        nextToken(); // args -> )

        if (!TokenType.RPAREN.equals(currentToken.getType())) {
            addUnexpectedTokenError(TokenType.RPAREN);
            return null;
        }

        return new CallExpression(left, arguments);
    }

    /**
     * Parses a block statement. Used by ConditionalExpression and FunctionExpression
     *
     * @return the created {@link BlockStatement}
     */
    private BlockStatement parseBlockStatement() {
        if (!expectToken(currentToken, TokenType.LBRACE)) {
            addUnexpectedTokenError(TokenType.LBRACE);
            return null;
        }

        nextToken(); // { -> block statement

        BlockStatement blockStatement = new BlockStatement();

        while (!TokenType.RBRACE.equals(currentToken.getType()) && !TokenType.EOF.equals(currentToken.getType())) {
            Statement statement = parseStatement();

            blockStatement.getStatements().add(statement);

            nextToken();
        }

        if (!TokenType.RBRACE.equals(currentToken.getType())) {
            addUnexpectedTokenError(TokenType.RBRACE);
            return null;
        }

        return blockStatement;
    }

    /**
     * Parses function parameters. Used by FunctionExpression.
     *
     * @return the parsed function parameters {@link List<Expression>}
     */
    private List<IdentifierExpression> parseFunctionParameters() {
        List<IdentifierExpression> parameters = new ArrayList<>();

        if (!TokenType.RPAREN.equals(peekToken.getType())) {
            // parse parameters
            nextToken(); // ( -> identifier
            parameters.add(parseIdentifierExpression());

            while (TokenType.COMMA.equals(peekToken.getType())) {
                nextToken(); // identifier -> ,
                nextToken(); // , -> identifier
                parameters.add(parseIdentifierExpression());
            }
        }

        return parameters;
    }

    /**
     * Parses call arguments. Used by CallExpression.
     *
     * @return the parsed call arguments {@link List<Expression>}
     */
    private List<Expression> parseCallArguments() {
        List<Expression> arguments = new ArrayList<>();

        if (!TokenType.RPAREN.equals(peekToken.getType())) {
            // parse parameters
            nextToken(); // ( -> expression
            arguments.add(parseExpression(Precedence.LOWEST));

            while (TokenType.COMMA.equals(peekToken.getType())) {
                nextToken(); // expression -> ,
                nextToken(); // , -> expression
                arguments.add(parseExpression(Precedence.LOWEST));
            }
        }

        return arguments;
    }

    /**
     * Parses return statement: {@code return <expression>}
     *
     * @return the parsed {@link ReturnStatement}
     */
    private ReturnStatement parseReturnStatement() {
        if (!expectToken(currentToken, TokenType.RETURN)) {
            addUnexpectedTokenError(TokenType.RETURN);
            return null;
        }

        nextToken(); // return -> expression

        Expression expression = parseExpression(Precedence.LOWEST);

        return new ReturnStatement(expression);
    }

    /**
     * Parses an array expression: {@code [<expression>, ...]}
     *
     * @return the parsed {@link ArrayExpression}
     */
    private ArrayExpression parseArrayExpression() {
        if (!expectToken(currentToken, TokenType.LSQUARE)) {
            addUnexpectedTokenError(TokenType.LSQUARE);
            return null;
        }

        List<Expression> elements = new ArrayList<>();

        // parse array elements
        if (!TokenType.RSQUARE.equals(peekToken.getType())) {
            nextToken(); // [ -> expression
            elements.add(parseExpression(Precedence.LOWEST));

            while (TokenType.COMMA.equals(peekToken.getType())) {
                nextToken(); // expression -> ,
                nextToken(); // , -> expression
                elements.add(parseExpression(Precedence.LOWEST));
            }
        }

        nextToken(); // elements -> ]

        if (!TokenType.RSQUARE.equals(currentToken.getType())) {
            addUnexpectedTokenError(TokenType.RSQUARE);
            return null;
        }

        return new ArrayExpression(elements);
    }

    /**
     * Parses an index expression: {@code <indexable>[<expression>]}
     *
     * @param left the left-hand side of the index expression
     * @return the parsed {@link ArrayExpression}
     */
    private IndexExpression parseIndexExpression(Expression left) {
        if (!(left instanceof IndexableExpression)) {
            addError("Expected indexable expression in left-hand side of index expression, got '%s'", left);
            return null;
        }

        nextToken(); // [ -> expression

        Expression index = parseExpression(Precedence.LOWEST);

        nextToken(); // expression -> ]

        if (!TokenType.RSQUARE.equals(currentToken.getType())) {
            addUnexpectedTokenError(TokenType.RSQUARE);
            return null;
        }

        return new IndexExpression(left, index);
    }

    /**
     * Parses a string expression: {@code "<string>"]}
     *
     * @return the parsed {@link StringExpression}
     */
    private StringExpression parseStringExpression() {
        if (!expectToken(currentToken, TokenType.STRING)) {
            addUnexpectedTokenError(TokenType.STRING);
            return null;
        }

        return new StringExpression(currentToken.getLexeme());
    }

    private boolean expectToken(Token token, TokenType tokenType) {
        return tokenType.equals(token.getType());
    }

    private void addUnexpectedTokenError(TokenType... expected) {
        addError("Unexpected token %s, expected %s", currentToken, String.join(" or ", Arrays.stream(expected).map(Enum::name).toList()));
    }

    private void addError(String message, Object... args) {
        errors.add(String.format("\t" + message, args));
    }
}
