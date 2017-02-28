import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reasoning {

	private static Set<String> terminals;

	public static void main(String[] args) {

		terminals = new HashSet<String>(Arrays.asList("&", "+", ">", "~", "-"));

		String input = "((A & (A -> B)) <-> (A & ((-A) | B)))";

		String clean = clean(input);
		String[] tokenized = tokenize(clean);
		for (String s : tokenized)
			System.out.print(s);
		System.out.println("");

		String[] parsed = parse(tokenized);
		System.out.print("BNF: ");
		for (String s : parsed)
			System.out.print(s);
		System.out.println();

		Tree tree = convertToTree(parsed);
		System.out.print("tree: ");
		System.out.println(tree.toString());

		tree = removeImplications(tree);
		System.out.print("implication free tree: ");
		System.out.println(tree.toString());

		tree = pushNegations(tree);
		System.out.print("negation free tree: ");
		System.out.println(tree.toString());
		tree.prettyPrint("");
		
		tree = cnf(tree);
		System.out.print("CNF tree: ");
		System.out.println(tree.toString());
		//tree.prettyPrint("");

		HashSet<HashSet<Atom>> set = setify(tree);
		System.out.print("Set: ");
		System.out.println(set.toString());

	}

	private static String clean(String input) {
		return input.replace(" ", "").replace("<->", "~").replace("->", ">").replace("|", "+");
	}

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

	private static boolean isAtom(String s) {
		return Pattern.matches("[A-Z]+[0-9]*", s);
	}

	private static boolean isTerminal(String s) {
		return terminals.contains(s);
	}

	private static String[] parse(String[] input) {
		ArrayList<String> output = new ArrayList<String>();
		Deque<String> opStack = new ArrayDeque<String>();
		for (String s : input) {

			if (isAtom(s)) {
				output.add(s);
			} else if (isTerminal(s)) {
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

	private static Tree convertToTree(String[] input) {
		Deque<Tree> treeStack = new ArrayDeque<Tree>();

		for (String s : input) {
			if (isAtom(s)) {
				treeStack.push(new Branch(s, null, null));
			} else if (s.equals("-")) {
				Tree l = treeStack.pop();
				treeStack.push(new Branch("-", l, null));
			} else if (isTerminal(s)) {
				Tree r = treeStack.pop();
				Tree l = treeStack.pop();
				treeStack.push(new Branch(s, l, r));
			}
		}
		return treeStack.pop();
	}

	/**
	 * <ul>
	 * <li>(A & B) | (C & D) => p((A & B) + C) & p((A + B) & D)</li>
	 * <li>L + (A & B) => (A + L) & (B + L)</li>
	 * <li>(A & B) + L => (A + L) & (B + L)</li>
	 * </ul>
	 * 
	 * @param tree
	 *            The tree with all negations pushed to the leaves to be put into clause normal form
	 * @return The tree in clause normal form
	 */
	private static Tree cnf(Tree tree) {

		if (tree.isCNF())
			return tree;

		Tree left = cnf(tree.getLeft());
		Tree right = cnf(tree.getRight());

		if(tree.getSymbol().equals("&"))
			return new Branch("&", left, right);
		
		if (left.getSymbol().equals("&") && right.getSymbol().equals("&"))
			return new Branch("&", cnf(new Branch("+", left, right.getLeft())),
					cnf(new Branch("+", left, right.getRight())));

		if (left.getSymbol().equals("&") && (right.getSymbol().equals("+") || right.isEnd()))
			return new Branch("&", cnf(new Branch("+", left.getLeft(), right)),
					cnf(new Branch("+", left.getRight(), right)));

		if ((left.getSymbol().equals("+") || left.isEnd()) && right.getSymbol().equals("&"))
			return new Branch("&", cnf(new Branch("+", right.getLeft(), left)),
					cnf(new Branch("+", right.getRight(), left)));

		//System.out.println("ERR");
		return tree;
	}

	/**
	 * Remove implications according to the following rules:
	 * <ul>
	 * <li>L => L</li>
	 * <li>~L => ~L</li>
	 * 
	 * <li>~T => ~p(T)
	 * <li>T > T => p(T) + ~p(T)</li>
	 * <li>T + T => p(T) + p(T)</li>
	 * <li>T & T => p(T) & p(T)</li>
	 * <li>A <> B => p(~A + B) & p(A + ~B)</li>
	 * </ul>
	 * 
	 * @param tree
	 *            The tree to be reduced
	 * @return The new tree with all implications removed
	 */
	private static Tree removeImplications(Tree tree) {

		if (tree == null)
			return null;

		if (tree.isEnd())
			return tree;

		if (tree.getSymbol().equals("~"))
			return new Branch("&", removeImplications(new Branch(">", tree.getLeft(), tree.getRight())),
					removeImplications(new Branch(">", tree.getRight(), tree.getLeft())));

		if (tree.getSymbol().equals("-"))
			return new Branch("-", removeImplications(tree.getLeft()), null);

		if (tree.getSymbol().equals(">"))
			return new Branch("+", new Branch("-", removeImplications(tree.getLeft()), null),
					removeImplications(tree.getRight()));

		if (tree.getSymbol().equals("+"))
			return new Branch("+", removeImplications(tree.getLeft()), removeImplications(tree.getRight()));

		if (tree.getSymbol().equals("&"))
			return new Branch("&", removeImplications(tree.getLeft()), removeImplications(tree.getRight()));

		return null;
	}

	/**
	 * Negations are removed according to the following rules: (T = tree, L = leaf)
	 * <ul>
	 * <li>L => L</li>
	 * <li>-L => -L</li>
	 * 
	 * <li>T&T => p(T) & p(T)</li>
	 * <li>T+T => p(T) + p(T)</li>
	 * <li>~(T&T) => p(~T) + p(~T)</li>
	 * <li>~(T+T) => p(~T) & p(~T)</li>
	 * <li>~(~T) => p(T)</li>
	 * </ul>
	 * 
	 * @param tree
	 *            The tree to have the negations pushed through
	 * @return The reduced tree
	 */
	private static Tree pushNegations(Tree tree) {

		if (tree == null)
			return null;

		if (tree.isEnd())
			return tree;

		if (tree.getSymbol().equals("&"))
			return new Branch("&", pushNegations(tree.getLeft()), pushNegations(tree.getRight()));

		if (tree.getSymbol().equals("+"))
			return new Branch("+", pushNegations(tree.getLeft()), pushNegations(tree.getRight()));

		if (tree.getSymbol().equals("-")) {

			if (tree.getLeft().getSymbol().equals("&"))
				return new Branch("+", pushNegations(new Branch("-", tree.getLeft().getLeft(), null)),
						pushNegations(new Branch("-", tree.getLeft().getRight(), null)));

			if (tree.getLeft().getSymbol().equals("+"))
				return new Branch("&", pushNegations(new Branch("-", tree.getLeft().getLeft(), null)),
						pushNegations(new Branch("-", tree.getLeft().getRight(), null)));

			if (tree.getLeft().getSymbol().equals("-"))
				return pushNegations(tree.getLeft().getLeft());

		}

		return null;
	}

	/**
	 * Convert the given tree into Clause Normal Form
	 * 
	 * @param tree
	 *            The tree to be converted
	 * @return The set of sets representing the CNF form of the tree
	 */
	private static HashSet<HashSet<Atom>> setify(Tree tree) {
		HashSet<HashSet<Atom>> tempSet = new HashSet<HashSet<Atom>>();

		if (tree.isDisjunctTree()) {
			HashSet<Atom> clauseSet = collectDisjunctTerms(tree);
			tempSet.add(clauseSet);
		} else {

			tempSet.addAll(setify(tree.getLeft()));
			tempSet.addAll(setify(tree.getRight()));

		}

		return tempSet;
	}

	/**
	 * Convert a disjunct tree into a set
	 * 
	 * @param tree
	 *            The disjunct tree to be converted into a clause set
	 * @return The clause set generated from the tree
	 */
	private static HashSet<Atom> collectDisjunctTerms(Tree tree) {

		if (tree.isEnd()) {

			if (tree.getSymbol() == "-") {
				HashSet<Atom> set = new HashSet<Atom>();
				set.add(new Atom(tree.getLeft().getSymbol(), true));
				return set;
			} else {
				HashSet<Atom> set = new HashSet<Atom>();
				set.add(new Atom(tree.getSymbol(), false));
				return set;
			}

		}

		HashSet<Atom> set = new HashSet<Atom>();
		set.addAll(collectDisjunctTerms(tree.getLeft()));
		set.addAll(collectDisjunctTerms(tree.getRight()));
		return set;

	}
	
}
