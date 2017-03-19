import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

public class Formula extends HashSet<Clause> {

	private String originalFormula;
	private Tree parseTree;
	private Tree cnfTree;
	private Proof proof;
	private Conclusion conclusion;

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
	public Formula(String formula, boolean verbose, PrintStream output) {
		setOriginalFormula(formula);
		setConclusion(Conclusion.UNKNOWN);
	}

	/**
	 * Create an empty formula
	 */
	public Formula() {
	}

	/**
	 * Parse the formula into an AST
	 * 
	 * @param verbose
	 *            Should there be a verbose output
	 * @param output
	 *            Where should the output be directed
	 */
	public void parse(boolean verbose, PrintStream output) {
		setParseTree(Parser.parse(getOriginalFormula(), verbose, output));
		normaliseTree(verbose, output);
	}

	/**
	 * reduce the tree to CNF and setify the tree
	 * 
	 * @param verbose
	 *            Should the method print a verbose output
	 * @param output
	 *            Where should the verbose output be directed
	 */
	private void normaliseTree(boolean verbose, PrintStream output) {
		setCnfTree(removeImplications(getParseTree()));
		setCnfTree(pushNegations(getCnfTree()));
		setCnfTree(cnf(getCnfTree()));
		setify();
	}

	/**
	 * <ul>
	 * <li>(A & B) | (C & D) ::= p((A & B) + C) & p((A + B) & D)</li>
	 * <li>L + (A & B) ::= (A + L) & (B + L)</li>
	 * <li>(A & B) + L ::= (A + L) & (B + L)</li>
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
	 * <li>L ::= L</li>
	 * <li>~L ::= ~L</li>
	 * 
	 * <li>~T ::= ~p(T)
	 * <li>T > T ::= p(T) + ~p(T)</li>
	 * <li>T + T ::= p(T) + p(T)</li>
	 * <li>T & T ::= p(T) & p(T)</li>
	 * <li>A <> B ::= p(~A + B) & p(A + ~B)</li>
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
	 * <li>L ::= L</li>
	 * <li>~L ::= ~L</li>
	 * 
	 * <li>T&T ::= p(T) & p(T)</li>
	 * <li>T+T ::= p(T) + p(T)</li>
	 * <li>~(T&T) ::= p(~T) + p(~T)</li>
	 * <li>~(T+T) ::= p(~T) & p(~T)</li>
	 * <li>~(~T) ::= p(T)</li>
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
	 * @param proofType
	 *            The type of proof that should be completed
	 * @return The proof of the formula
	 */
	public Proof prove(ProofType proofType) {
		return prove(proofType, false, System.out);
	}

	/**
	 * Prove the formula with or without runtime output
	 * 
	 * @param proofType
	 *            The type of proof that should be completed
	 * @param verbose
	 *            A boolean indicating whether there should be a runtime output
	 * @param out
	 * @return The proof
	 */
	public Proof prove(ProofType proofType, boolean verbose) {
		return prove(proofType, verbose, System.out);
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
	public Proof prove(ProofType proofType, boolean verbose, PrintStream output) {
		if (proofType == ProofType.RESOLUTION) {
			Formula negation = this.getNegation(verbose, output);
			negation.parse(verbose, output);
			this.proof = new ResolutionProof(negation, this);
			this.proof.prove(verbose, output);
			return this.proof;
		}
		if (proofType == ProofType.DPLL) {
			this.proof = new DPLLProof(this);
			this.proof.prove(verbose, output);
			return this.proof;
		}
		return null;
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

	/**
	 * Get the original formula that the object represents
	 * 
	 * @return The original object that the object represents
	 */
	public String getOriginalFormula() {
		return originalFormula;
	}

	/**
	 * Set the formula that the object represents
	 * 
	 * @param originalFormula
	 *            The formula that the object represents
	 */
	public void setOriginalFormula(String originalFormula) {
		this.originalFormula = originalFormula;
	}

	/**
	 * Get the parse tree for the formula
	 * 
	 * @return The parse tree generated for this formula
	 */
	public Tree getParseTree() {
		return parseTree;
	}

	/**
	 * Set the parse tree for this formula
	 * 
	 * @param parseTree
	 *            The new parse tree for the formula
	 */
	public void setParseTree(Tree parseTree) {
		this.parseTree = parseTree;
	}

	/**
	 * Return the original formula or a message indicating that no formula has been specified
	 */
	@Override
	public String toString() {
		String r = "";

		if (getOriginalFormula() == null)
			r = r + "No formula specified\n";
		else
			r = r + "Original Formula: " + getOriginalFormula() + "\nClause Normal Form: " + super.toString();

		return r;
	}

	/**
	 * Get the tree in clause normal form
	 * 
	 * @return The tree in clause normal form
	 */
	public Tree getCnfTree() {
		return cnfTree;
	}

	/**
	 * set the tree in clause normal form
	 * 
	 * @param cnfTree
	 *            The new clause normal form tree
	 */
	public void setCnfTree(Tree cnfTree) {
		this.cnfTree = cnfTree;
	}

	/**
	 * Get the negated version of the formula
	 * 
	 * @param output
	 * @param verbose
	 * 
	 * @return The negated formula
	 */
	public Formula getNegation(boolean verbose, PrintStream output) {
		return new Formula("(-" + getOriginalFormula() + ")", verbose, output);
	}

	/**
	 * Get the conclusion of the proof on the formula
	 * 
	 * @return The conclusion for the formulas proof
	 */
	public Conclusion getConclusion() {
		return conclusion;
	}

	/**
	 * Set the conclusion for the formula
	 * 
	 * @param conclusion
	 *            The new conclusion for the formula
	 */
	public void setConclusion(Conclusion conclusion) {
		this.conclusion = conclusion;
	}

}
