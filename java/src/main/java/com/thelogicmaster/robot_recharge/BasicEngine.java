/*
 * Jasic uses the MIT License:
 *
 * Copyright (c) 2010 Robert Nystrom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.thelogicmaster.robot_recharge;

import com.badlogic.gdx.Gdx;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.ExecutionListener;

import java.util.*;

/**
 * This defines a single class that contains an entire interpreter for a
 * language very similar to the original BASIC. Everything is here (albeit in
 * very simplified form): tokenizing, parsing, and interpretation. The file is
 * organized in phases, with each appearing roughly in the order that they
 * occur when a program is run. You should be able to read this top-down to walk
 * through the entire process of loading and running a program.
 *
 * Jasic language syntax
 * ---------------------
 *
 * Comments start with ' and proceed to the end of the line:
 *
 *     print "hi there" ' this is a comment
 *
 * Numbers and strings are supported. Strings should be in "double quotes", and
 * only positive integers can be parsed (though numbers are double internally).
 *
 * Variables are identified by name which must start with a letter and can
 * contain letters or numbers. Case is significant for names and keywords.
 *
 * Each statement is on its own line. Optionally, a line may have a label before
 * the statement. A label is a name that ends with a colon:
 *
 *     foo:
 *
 *
 * The following statements are supported:
 *
 * <name> = <expression>
 *     Evaluates the expression and assigns the result to the given named
 *     variable. All variables are globally scoped.
 *
 *     pi = (314159 / 10000)
 *
 * speak <expression>
 *     Evaluates the expression and prints the result.
 *
 *     speak "hello, " + "world"
 *
 * goto <label>
 *     Jumps to the statement after the label with the given name.
 *
 *     goto loop
 *
 * if <expression> then <label>
 *     Evaluates the expression. If it evaluates to a non-zero number, then
 *     jumps to the statement after the given label.
 *
 *     if a < b then dosomething
 *
 *
 * The following expressions are supported:
 *
 * <expression> = <expression>
 *     Evaluates to 1 if the two expressions are equal, 0 otherwise.
 *
 * <expression> + <expression>
 *     If the left-hand expression is a number, then adds the two expressions,
 *     otherwise concatenates the two strings.
 *
 * <expression> - <expression>
 * <expression> * <expression>
 * <expression> / <expression>
 * <expression> < <expression>
 * <expression> > <expression>
 *     You can figure it out.
 *
 * <name>
 *     A name in an expression simply returns the value of the variable with
 *     that name. If the variable was never set, it defaults to 0.
 *
 * All binary operators have the same precedence. Sorry, I had to cut corners
 * somewhere.
 *
 * To keep things simple, I've omitted some stuff or hacked things a bit. When
 * possible, I'll leave a "HACK" note there explaining what and why. If you
 * make your own interpreter, you'll want to address those.
 *
 * @author Bob Nystrom
 */
public class BasicEngine implements CodeEngine {

	private Map<String, Value> variables;
	private Map<String, Integer> labels;
	private int currentStatement;
	private IRobot robot;

	@Override
	public void initialize () {
	}

	@Override
	public ExecutionInstance run (IRobot robot, String code, ExecutionListener listener) {
		this.robot = robot;

		Thread thread = new Thread(() -> {
			try {
				interpret(code + "\n");
				listener.onExecutionFinish();
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					listener.onExecutionInterrupted();
					return;
				}
				Gdx.app.error("BASIC", e.toString());
				listener.onExecutionError(e.getMessage());
			}
		});
		thread.start();
		return new ExecutionInstance(thread);
	}

	// Tokenizing (lexing) -----------------------------------------------------

	/**
	 * This function takes a script as a string of characters and chunks it into
	 * a sequence of tokens. Each token is a meaningful unit of program, like a
	 * variable name, a number, a string, or an operator.
	 */
	private static List<Token> tokenize(String source) {
		List<Token> tokens = new ArrayList<Token>();

		String token = "";
		TokenizeState state = TokenizeState.DEFAULT;

		// Many tokens are a single character, like operators and ().
		String charTokens = "\n=+-*/<>()";
		TokenType[] tokenTypes = { TokenType.LINE, TokenType.EQUALS,
			TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
			TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
			TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN
		};

		// Scan through the code one character at a time, building up the list
		// of tokens.
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (state) {
			case DEFAULT:
				if (charTokens.indexOf(c) != -1) {
					tokens.add(new Token(Character.toString(c),
						tokenTypes[charTokens.indexOf(c)]));
				} else if (Character.isLetter(c)) {
					token += c;
					state = TokenizeState.WORD;
				} else if (Character.isDigit(c)) {
					token += c;
					state = TokenizeState.NUMBER;
				} else if (c == '"') {
					state = TokenizeState.STRING;
				} else if (c == '\'') {
					state = TokenizeState.COMMENT;
				}
				break;

			case WORD:
				if (Character.isLetterOrDigit(c)) {
					token += c;
				} else if (c == ':') {
					tokens.add(new Token(token, TokenType.LABEL));
					token = "";
					state = TokenizeState.DEFAULT;
				} else {
					tokens.add(new Token(token, TokenType.WORD));
					token = "";
					state = TokenizeState.DEFAULT;
					i--; // Reprocess this character in the default state.
				}
				break;

			case NUMBER:
				// HACK: Negative numbers and floating points aren't supported.
				// To get a negative number, just do 0 - <your number>.
				// To get a floating point, divide.
				if (Character.isDigit(c)) {
					token += c;
				} else {
					tokens.add(new Token(token, TokenType.NUMBER));
					token = "";
					state = TokenizeState.DEFAULT;
					i--; // Reprocess this character in the default state.
				}
				break;

			case STRING:
				if (c == '"') {
					tokens.add(new Token(token, TokenType.STRING));
					token = "";
					state = TokenizeState.DEFAULT;
				} else {
					token += c;
				}
				break;

			case COMMENT:
				if (c == '\n') {
					state = TokenizeState.DEFAULT;
				}
				break;
			}
		}

		// HACK: Silently ignore any in-progress token when we run out of
		// characters. This means that, for example, if a script has a string
		// that's missing the closing ", it will just ditch it.
		return tokens;
	}

	// Token data --------------------------------------------------------------

	/**
	 * This defines the different kinds of tokens or meaningful chunks of code
	 * that the parser knows how to consume. These let us distinguish, for
	 * example, between a string "foo" and a variable named "foo".
	 *
	 * HACK: A typical tokenizer would actually have unique token types for
	 * each keyword (print, goto, etc.) so that the parser doesn't have to look
	 * at the names, but Jasic is a little more crude.
	 */
	private enum TokenType {
		WORD, NUMBER, STRING, LABEL, LINE,
		EQUALS, OPERATOR, LEFT_PAREN, RIGHT_PAREN, EOF
	}

	/**
	 * This is a single meaningful chunk of code. It is created by the tokenizer
	 * and consumed by the parser.
	 */
	private static class Token {
		public final String text;
		public final TokenType type;

		public Token(String text, TokenType type) {
			this.text = text;
			this.type = type;
		}
	}

	/**
	 * This defines the different states the tokenizer can be in while it's
	 * scanning through the source code. Tokenizers are state machines, which
	 * means the only data they need to store is where they are in the source
	 * code and this one "state" or mode value.
	 *
	 * One of the main differences between tokenizing and parsing is this
	 * regularity. Because the tokenizer stores only this one state value, it
	 * can't handle nesting (which would require also storing a number to
	 * identify how deeply nested you are). The parser is able to handle that.
	 */
	private enum TokenizeState {
		DEFAULT, WORD, NUMBER, STRING, COMMENT
	}

	// Parsing -----------------------------------------------------------------

	/**
	 * This defines the Jasic parser. The parser takes in a sequence of tokens
	 * and generates an abstract syntax tree. This is the nested data structure
	 * that represents the series of statements, and the expressions (which can
	 * nest arbitrarily deeply) that they evaluate. In technical terms, what we
	 * have is a recursive descent parser, the simplest kind to hand-write.
	 *
	 * As a side-effect, this phase also stores off the line numbers for each
	 * label in the program. It's a bit gross, but it works.
	 */
	private class Parser {
		private final List<Token> tokens;
		private int position;

		public Parser(List<Token> tokens) {
			this.tokens = tokens;
			position = 0;
		}

		/**
		 * The top-level function to start parsing. This will keep consuming
		 * tokens and routing to the other parse functions for the different
		 * grammar syntax until we run out of code to parse.
		 *
		 * @param  labels   A map of label names to statement indexes. The
		 *                  parser will fill this in as it scans the code.
		 * @return          The list of parsed statements.
		 */
		public List<Statement> parse(Map<String, Integer> labels) {
			List<Statement> statements = new ArrayList<Statement>();

			while (true) {
				// Ignore empty lines.
				while (match(TokenType.LINE));

				if (match(TokenType.LABEL)) {
					// Mark the index of the statement after the label.
					labels.put(last(1).text, statements.size());
				} else if (match(TokenType.WORD, TokenType.EQUALS)) {
					String name = last(2).text;
					Expression value = expression();
					statements.add(new AssignStatement(name, value));
				} else if (match("speak")) {
					statements.add(new SpeakStatement(expression()));
				} else if (match("move")) {
					statements.add(new MoveStatement(expression()));
				} else if (match("turn")) {
					statements.add(new TurnStatement(expression()));
				} else if (match("sleep")) {
					statements.add(new SleepStatement(expression()));
				} else if (match("goto")) {
					statements.add(new GotoStatement(consume(TokenType.WORD).text));
				} else if (match("if")) {
					Expression condition = expression();
					consume("then");
					String label = consume(TokenType.WORD).text;
					statements.add(new IfThenStatement(condition, label));
				} else break; // Unexpected token (likely EOF), so end.
			}

			return statements;
		}

		// The following functions each represent one grammatical part of the
		// language. If this parsed English, these functions would be named like
		// noun() and verb().

		/**
		 * Parses a single expression. Recursive descent parsers start with the
		 * lowest-precedent term and moves towards higher precedence. For Jasic,
		 * binary operators (+, -, etc.) are the lowest.
		 *
		 * @return The parsed expression.
		 */
		private Expression expression() {
			return operator();
		}

		/**
		 * Parses a series of binary operator expressions into a single
		 * expression. In Jasic, all operators have the same predecence and
		 * associate left-to-right. That means it will interpret:
		 *    1 + 2 * 3 - 4 / 5
		 * like:
		 *    ((((1 + 2) * 3) - 4) / 5)
		 *
		 * It works by building the expression tree one at a time. So, given
		 * this code: 1 + 2 * 3, this will:
		 *
		 * 1. Parse (1) as an atomic expression.
		 * 2. See the (+) and start a new operator expression.
		 * 3. Parse (2) as an atomic expression.
		 * 4. Build a (1 + 2) expression and replace (1) with it.
		 * 5. See the (*) and start a new operator expression.
		 * 6. Parse (3) as an atomic expression.
		 * 7. Build a ((1 + 2) * 3) expression and replace (1 + 2) with it.
		 * 8. Return the last expression built.
		 *
		 * @return The parsed expression.
		 */
		private Expression operator() {
			Expression expression = atomic();

			// Keep building operator expressions as long as we have operators.
			while (match(TokenType.OPERATOR) ||
				match(TokenType.EQUALS)) {
				char operator = last(1).text.charAt(0);
				Expression right = atomic();
				expression = new OperatorExpression(expression, operator, right);
			}

			return expression;
		}

		/**
		 * Parses an "atomic" expression. This is the highest level of
		 * precedence and contains single literal tokens like 123 and "foo", as
		 * well as parenthesized expressions.
		 *
		 * @return The parsed expression.
		 */
		private Expression atomic() {
			if (match(TokenType.WORD)) {
				// A word is a reference to a variable.
				return new VariableExpression(last(1).text);
			} else if (match(TokenType.NUMBER)) {
				return new NumberValue(Double.parseDouble(last(1).text));
			} else if (match(TokenType.STRING)) {
				return new StringValue(last(1).text);
			} else if (match(TokenType.LEFT_PAREN)) {
				// The contents of a parenthesized expression can be any
				// expression. This lets us "restart" the precedence cascade
				// so that you can have a lower precedence expression inside
				// the parentheses.
				Expression expression = expression();
				consume(TokenType.RIGHT_PAREN);
				return expression;
			}
			throw new Error("Couldn't parse :(");
		}

		// The following functions are the core low-level operations that the
		// grammar parser is built in terms of. They match and consume tokens in
		// the token stream.

		/**
		 * Consumes the next two tokens if they are the given type (in order).
		 * Consumes no tokens if either check fais.
		 *
		 * @param  type1 Expected type of the next token.
		 * @param  type2 Expected type of the subsequent token.
		 * @return       True if tokens were consumed.
		 */
		private boolean match(TokenType type1, TokenType type2) {
			if (get(0).type != type1) return false;
			if (get(1).type != type2) return false;
			position += 2;
			return true;
		}

		/**
		 * Consumes the next token if it's the given type.
		 *
		 * @param  type  Expected type of the next token.
		 * @return       True if the token was consumed.
		 */
		private boolean match(TokenType type) {
			if (get(0).type != type) return false;
			position++;
			return true;
		}

		/**
		 * Consumes the next token if it's a word token with the given name.
		 *
		 * @param  name  Expected name of the next word token.
		 * @return       True if the token was consumed.
		 */
		private boolean match(String name) {
			if (get(0).type != TokenType.WORD) return false;
			if (!get(0).text.equals(name)) return false;
			position++;
			return true;
		}

		/**
		 * Consumes the next token if it's the given type. If not, throws an
		 * exception. This is for cases where the parser demands a token of a
		 * certain type in a certain position, for example a matching ) after
		 * an opening (.
		 *
		 * @param  type  Expected type of the next token.
		 * @return       The consumed token.
		 */
		private Token consume(TokenType type) {
			if (get(0).type != type) throw new Error("Expected " + type + ".");
			return tokens.get(position++);
		}

		/**
		 * Consumes the next token if it's a word with the given name. If not,
		 * throws an exception.
		 *
		 * @param  name  Expected name of the next word token.
		 * @return       The consumed token.
		 */
		private Token consume(String name) {
			if (!match(name)) throw new Error("Expected " + name + ".");
			return last(1);
		}

		/**
		 * Gets a previously consumed token, indexing backwards. last(1) will
		 * be the token just consumed, last(2) the one before that, etc.
		 *
		 * @param  offset How far back in the token stream to look.
		 * @return        The consumed token.
		 */
		private Token last(int offset) {
			return tokens.get(position - offset);
		}

		/**
		 * Gets an unconsumed token, indexing forward. get(0) will be the next
		 * token to be consumed, get(1) the one after that, etc.
		 *
		 * @param  offset How far forward in the token stream to look.
		 * @return        The yet-to-be-consumed token.
		 */
		private Token get(int offset) {
			if (position + offset >= tokens.size()) {
				return new Token("", TokenType.EOF);
			}
			return tokens.get(position + offset);
		}
	}

	// Abstract syntax tree (AST) ----------------------------------------------

	// These classes define the syntax tree data structures. This is how code is
	// represented internally in a way that's easy for the interpreter to
	// understand.
	//
	// HACK: Unlike most real compilers or interpreters, the logic to execute
	// the code is baked directly into these classes. Typically, it would be
	// separated out so that the AST us just a static data structure.

	/**
	 * Base interface for a Jasic statement. The different supported statement
	 * types like "print" and "goto" implement this.
	 */
	public interface Statement {
		/**
		 * Statements implement this to actually perform whatever behavior the
		 * statement causes. "print" statements will display text here, "goto"
		 * statements will change the current statement, etc.
		 */
		void execute() throws InterruptedException;
	}

	/**
	 * Base interface for an expression. An expression is like a statement
	 * except that it also returns a value when executed. Expressions do not
	 * appear at the top level in Jasic programs, but are used in many
	 * statements. For example, the value printed by a "print" statement is an
	 * expression. Unlike statements, expressions can nest.
	 */
	public interface Expression {
		/**
		 * Expression classes implement this to evaluate the expression and
		 * return the value.
		 *
		 * @return The value of the calculated expression.
		 */
		Value evaluate();
	}

	/**
	 * A "speak" statement evaluates an expression, converts the result to a
	 * string, and displays it to the user.
	 */
	public class SpeakStatement implements Statement {
		private final Expression expression;

		public SpeakStatement (Expression expression) {
			this.expression = expression;
		}

		public void execute() {
			robot.speak(expression.evaluate().toString());
		}
	}

	/**
	 * A "sleep" statement sleeps for a specified number of seconds.
	 */
	public class SleepStatement implements Statement {
		private final Expression time;

		public SleepStatement(Expression time) {
			this.time = time;
		}

		public void execute() throws InterruptedException {
			robot.sleep((int)time.evaluate().toNumber());
		}
	}

	/**
	 * A "move" statement moves a specified number of times.
	 */
	public class MoveStatement implements Statement {
		private final Expression distance;

		public MoveStatement(Expression distance) {
			this.distance = distance;
		}

		public void execute() throws InterruptedException {
			robot.move((int)distance.evaluate().toNumber());
		}
	}

	/**
	 * A "turn" statement turns a specified number of times.
	 */
	public class TurnStatement implements Statement {
		private final Expression distance;

		public TurnStatement(Expression distance) {
			this.distance = distance;
		}

		public void execute() throws InterruptedException {
			robot.turn((int)distance.evaluate().toNumber());
		}
	}

	/**
	 * An assignment statement evaluates an expression and stores the result in
	 * a variable.
	 */
	public class AssignStatement implements Statement {
		private final String name;
		private final Expression value;

		public AssignStatement(String name, Expression value) {
			this.name = name;
			this.value = value;
		}

		public void execute() {
			variables.put(name, value.evaluate());
		}
	}

	/**
	 * A "goto" statement jumps execution to another place in the program.
	 */
	public class GotoStatement implements Statement {
		private final String label;

		public GotoStatement(String label) {
			this.label = label;
		}

		public void execute() {
			if (labels.containsKey(label))
				currentStatement = labels.get(label);
			else
				throw new Error("No such label: " + label);
		}
	}

	/**
	 * An if then statement jumps execution to another place in the program, but
	 * only if an expression evaluates to something other than 0.
	 */
	public class IfThenStatement implements Statement {
		private final Expression condition;
		private final String label;

		public IfThenStatement(Expression condition, String label) {
			this.condition = condition;
			this.label = label;
		}

		public void execute() {
			if (labels.containsKey(label)) {
				double value = condition.evaluate().toNumber();
				if (value != 0)
					currentStatement = labels.get(label);
			}
		}
	}

	/**
	 * A variable expression evaluates to the current value stored in that
	 * variable.
	 */
	public class VariableExpression implements Expression {
		private final String name;

		public VariableExpression(String name) {
			this.name = name;
		}

		public Value evaluate() {
			if (variables.containsKey(name))
				return variables.get(name);
			return new NumberValue(0);
		}
	}

	/**
	 * An operator expression evaluates two expressions and then performs some
	 * arithmetic operation on the results.
	 */
	public class OperatorExpression implements Expression {
		private final Expression left;
		private final char operator;
		private final Expression right;

		public OperatorExpression(Expression left, char operator,
			Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public Value evaluate() {
			Value leftVal = left.evaluate();
			Value rightVal = right.evaluate();

			switch (operator) {
			case '=':
				// Coerce to the left argument's type, then compare.
				if (leftVal instanceof NumberValue) {
					return new NumberValue((leftVal.toNumber() ==
						rightVal.toNumber()) ? 1 : 0);
				} else {
					return new NumberValue(leftVal.toString().equals(
						rightVal.toString()) ? 1 : 0);
				}
			case '+':
				// Addition if the left argument is a number, otherwise do
				// string concatenation.
				if (leftVal instanceof NumberValue) {
					return new NumberValue(leftVal.toNumber() +
						rightVal.toNumber());
				} else {
					return new StringValue(leftVal.toString() +
						rightVal.toString());
				}
			case '-':
				return new NumberValue(leftVal.toNumber() -
					rightVal.toNumber());
			case '*':
				return new NumberValue(leftVal.toNumber() *
					rightVal.toNumber());
			case '/':
				return new NumberValue(leftVal.toNumber() /
					rightVal.toNumber());
			case '<':
				// Coerce to the left argument's type, then compare.
				if (leftVal instanceof NumberValue) {
					return new NumberValue((leftVal.toNumber() <
						rightVal.toNumber()) ? 1 : 0);
				} else {
					return new NumberValue((leftVal.toString().compareTo(
						rightVal.toString()) < 0) ? 1 : 0);
				}
			case '>':
				// Coerce to the left argument's type, then compare.
				if (leftVal instanceof NumberValue) {
					return new NumberValue((leftVal.toNumber() >
						rightVal.toNumber()) ? 1 : 0);
				} else {
					return new NumberValue((leftVal.toString().compareTo(
						rightVal.toString()) > 0) ? 1 : 0);
				}
			}
			throw new Error("Unknown operator.");
		}
	}

	// Value types -------------------------------------------------------------

	/**
	 * This is the base interface for a value. Values are the data that the
	 * interpreter processes. They are what gets stored in variables, printed,
	 * and operated on.
	 *
	 * There is an implementation of this interface for each of the different
	 * primitive types (really just double and string) that Jasic supports.
	 * Wrapping them in a single Value interface lets Jasic be dynamically-typed
	 * and convert between different representations as needed.
	 *
	 * Note that Value extends Expression. This is a bit of a hack, but it lets
	 * us use values (which are typically only ever seen by the interpreter and
	 * not the parser) as both runtime values, and as object representing
	 * literals in code.
	 */
	public interface Value extends Expression {
		/**
		 * Value types override this to convert themselves to a string
		 * representation.
		 */
		String toString();

		/**
		 * Value types override this to convert themselves to a numeric
		 * representation.
		 */
		double toNumber();
	}

	/**
	 * A numeric value. Jasic uses doubles internally for all numbers.
	 */
	public class NumberValue implements Value {
		private final double value;

		public NumberValue(double value) {
			this.value = value;
		}

		@Override public String toString() { return Double.toString(value); }
		public double toNumber() { return value; }
		public Value evaluate() { return this; }
	}

	/**
	 * A string value.
	 */
	public class StringValue implements Value {
		private final String value;

		public StringValue(String value) {
			this.value = value;
		}

		@Override public String toString() { return value; }
		public double toNumber() { return Double.parseDouble(value); }
		public Value evaluate() { return this; }
	}

	// Interpreter -------------------------------------------------------------

	/**
	 * This is where the magic happens. This runs the code through the parsing
	 * pipeline to generate the AST. Then it executes each statement. It keeps
	 * track of the current line in a member variable that the statement objects
	 * have access to. This lets "goto" and "if then" do flow control by simply
	 * setting the index of the current statement.
	 *
	 * In an interpreter that didn't mix the interpretation logic in with the
	 * AST node classes, this would be doing a lot more work.
	 *
	 * @param source A string containing the source code of a .jas script to
	 *               interpret.
	 */
	public void interpret(String source) throws InterruptedException {
		variables = new HashMap<>();
		labels = new HashMap<>();

		// Tokenize.
		List<Token> tokens = tokenize(source);

		// Parse.
		Parser parser = new Parser(tokens);
		List<Statement> statements = parser.parse(labels);

		// Interpret until we're done.
		currentStatement = 0;
		while (currentStatement < statements.size()) {
			int thisStatement = currentStatement;
			currentStatement++;
			statements.get(thisStatement).execute();
			if (Thread.interrupted())
				throw new InterruptedException();
		}
	}
}