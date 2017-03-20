package parse;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private static final HashSet<Character> operators = new HashSet<Character>(Arrays.asList('&', '+', '>', '~', '-'));

	/**
	 * Parse the given input formula into a parse tree
	 * 
	 * @param input
	 *            The input formula
	 * @return The parse tree of the input
	 */
	private static Tree parse(String input) {
		return parse(input, false, System.out);
	}

	/**
	 * Parse the given input formula into a parse tree with an optional output flagged by verbose
	 * 
	 * @param input
	 *            The input formula for the parser
	 * @param verbose
	 *            A boolean indicating whether there should be a lot of output during the parse
	 * @return The parse tree for the given input
	 */
	private static Tree parse(String input, boolean verbose) {
		return parse(input, verbose, System.out);
	}

	/**
	 * Parse the input formula into a parse tree with an optional output indicated by the verbose flag going to the given PrintStream
	 * 
	 * @param input
	 *            The input formula to be parsed
	 * @param verbose
	 *            Should there be output at runtime
	 * @param output
	 *            The PrintStream where the output should be directed
	 * @return
	 */
	public static Tree parse(String input, boolean verbose, PrintStream output) {
		String clean = clean(input);
		String[] tokenized = tokenize(clean);
		if (verbose) {
			for (String s : tokenized)
				System.out.print(s);
			output.println("");
		}

		String[] parsed = postfix(tokenized);
		if (verbose) {
			output.print("BNF: ");
			for (String s : parsed)
				System.out.print(s);
			output.println();
		}

		Tree tree = convertToTree(parsed);
		if (verbose) {
			output.print("tree: ");
			output.println(tree.toString());
		}

		return tree;
	}

	/**
	 * Clean the input to create a common format
	 * 
	 * @param input
	 *            The input formula
	 * @return The cleaned formula
	 */
	private static String clean(String input) {
		return input.replace(" ", "").replace("<->", "~").replace("->", ">").replace("|", "+");
	}

	/**
	 * Split the input formula into an array of tokens
	 * 
	 * @param input
	 *            The input formula to be tokenized
	 * @return The array for the tokenized formula
	 */
	private static String[] tokenize(String input) {
		Pattern pattern = Pattern.compile("[A-Z]+[0-9]*|[()&+->~|]");
		Matcher matcher = pattern.matcher(input);
		ArrayList<String> output = new ArrayList<>();
		while (matcher.find()) {
			output.add(matcher.group());
		}
		String[] outputA = output.toArray(new String[output.size()]);
		return outputA;
	}

	/**
	 * Does the given string represent an atom
	 * 
	 * @param s
	 *            The string to be checked
	 * @return A boolean indicating whether or not the given string is an atom
	 */
	private static boolean isAtom(String s) {
		return Pattern.matches("[A-Z]+[0-9]*", s);
	}

	/**
	 * Is the given string an operator
	 * 
	 * @param s
	 *            The string to be checked (should be a single character)
	 * @return A boolean indicating whether or not the given string is an operator
	 */
	private static boolean isOperator(String s) {
		return operators.contains(s.charAt(0));
	}

	/**
	 * Convert the tokenized formula into postfix notation
	 * 
	 * @param input
	 *            The tokenized formula in infix form
	 * @return The tokenized formula in postfix form
	 */
	private static String[] postfix(String[] input) {
		ArrayList<String> output = new ArrayList<String>();
		Deque<String> opStack = new ArrayDeque<String>();
		for (String s : input) {

			if (isAtom(s)) {
				output.add(s);
			} else if (isOperator(s)) {
				opStack.push(s);
			} else if (s.equals("(")) {
				opStack.push(s);
			} else if (s.equals(")")) {
				while (opStack.peek().equals(")")) {
					opStack.pop();
				}
				output.add(opStack.pop());
				while (!opStack.isEmpty() && opStack.peek().equals("(")) {
					opStack.pop();
				}
			}

		}
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Convert the postfix formula into a tree
	 * 
	 * @param input
	 *            The postfix formula to be converted
	 * @return The tree generated from the postfix formula
	 */
	private static Tree convertToTree(String[] input) {
		Deque<Tree> treeStack = new ArrayDeque<Tree>();

		for (String s : input) {
			if (isAtom(s)) {
				treeStack.push(new Branch(s, null, null));
			} else if (s.equals("-")) {
				Tree l = treeStack.pop();
				treeStack.push(new Branch("-", l, null));
			} else if (isOperator(s)) {
				Tree r = treeStack.pop();
				Tree l = treeStack.pop();
				treeStack.push(new Branch(s, l, r));
			}
		}
		return treeStack.pop();
	}

}
