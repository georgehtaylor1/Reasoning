import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

public class Formula extends HashSet<Clause> {

	private String originalFormula;
	private Tree parseTree;
	private Tree cnfTree;
	private Proof proof;

	public boolean equals(Formula f) {
		return this.size() == f.size() && f.containsAll(this);
	}

	/**
	 * Create a new Formula object with the specified String formula
	 * 
	 * @param formula
	 *            The String formula that this object represents
	 * @param verbose
	 *            Should the formula ever produce a verbose output
	 * @param output
	 *            The PrintStream that should handle the runtime output
	 */
	public Formula(String formula, boolean prove, boolean verbose, PrintStream output) {
		setOriginalFormula(formula);
		setParseTree(Parser.parse(getOriginalFormula(), verbose, output));
		normaliseTree();
		proof = new Proof(this);
		if (prove)
			prove(verbose, output);
	}

	public Formula() {
		// TODO Auto-generated constructor stub
	}

	private void normaliseTree() {
		setCnfTree(removeImplications(getParseTree()));
		setCnfTree(pushNegations(getCnfTree()));
		setCnfTree(cnf(getCnfTree()));
		setify();
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

		if (tree.getSymbol().equals("&"))
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
	 * Convert the cnfTree into a Set of clauses
	 */
	private void setify() {
		this.addAll(setify(getCnfTree()));
	}

	/**
	 * Convert the given tree into Clause Normal Form
	 * 
	 * @param tree
	 *            The tree to be converted
	 * @return The set of sets representing the CNF form of the tree
	 */
	private Formula setify(Tree tree) {
		Formula tempSet = new Formula();

		if (tree.isDisjunctTree()) {
			Clause clause = collectDisjunctTerms(tree);
			tempSet.add(clause);
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
	private Clause collectDisjunctTerms(Tree tree) {

		if (tree.isEnd()) {

			if (tree.getSymbol() == "-") {
				Clause clause = new Clause();
				clause.add(new Literal(tree.getLeft().getSymbol(), true));
				return clause;
			} else {
				Clause clause = new Clause();
				clause.add(new Literal(tree.getSymbol(), false));
				return clause;
			}

		}

		Clause clause = new Clause();
		clause.addAll(collectDisjunctTerms(tree.getLeft()));
		clause.addAll(collectDisjunctTerms(tree.getRight()));
		return clause;

	}

	/**
	 * Prove the formula
	 * 
	 * @return The proof of the formula
	 */
	public Proof prove() {
		return prove(false, System.out);
	}

	/**
	 * Prove the formula with or without runtime output
	 * 
	 * @param verbose
	 *            A boolean indicating whether there should be a runtime output
	 * @return The proof
	 */
	public Proof prove(boolean verbose) {
		return prove(verbose, System.out);
	}

	/**
	 * Prove the formula with or without runtime oupt, specifying the PrintStream that should provide the output
	 * 
	 * @param verbose
	 *            A boolean indicating whether there should be a runtime output
	 * @param output
	 *            The PrintStream that should produce the output
	 * @return
	 */
	public Proof prove(boolean verbose, PrintStream output) {
		proof.resolve(verbose, output);
		return getProof();
	}

	/**
	 * Get the generated proof
	 * 
	 * @return The proof
	 */
	public Proof getProof() {
		return proof;
	}

	/**
	 * Set the proof
	 * 
	 * @param proof
	 *            The new proof
	 */
	public void setProof(Proof proof) {
		this.proof = proof;
	}

	public String getOriginalFormula() {
		return originalFormula;
	}

	public void setOriginalFormula(String originalFormula) {
		this.originalFormula = originalFormula;
	}

	public Tree getParseTree() {
		return parseTree;
	}

	public void setParseTree(Tree parseTree) {
		this.parseTree = parseTree;
	}

	public String toString() {
		String r = "";

		if (getOriginalFormula() == null)
			r = r + "No formula specified\n";
		else
			r = r + "Original Formula: " + getOriginalFormula() + "\nClause Normal Form: " + super.toString();

		return r;
	}

	public Tree getCnfTree() {
		return cnfTree;
	}

	public void setCnfTree(Tree cnfTree) {
		this.cnfTree = cnfTree;
	}

}
