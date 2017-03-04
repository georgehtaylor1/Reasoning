
public class Branch extends Tree {

	private String symbol;

	private Tree left;
	private Tree right;

	/**
	 * Create a new branch
	 * 
	 * @param symbol
	 *            The symbol at the root of the branch
	 * @param left
	 *            The left subtree
	 * @param right
	 *            The right subtree
	 */
	public Branch(String symbol, Tree left, Tree right) {
		this.symbol = symbol;
		this.left = left;
		this.right = right;
	}

	/* (non-Javadoc)
	 * @see Tree#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return getLeft() == null && getRight() == null;
	}

	/* (non-Javadoc)
	 * @see Tree#getLeft()
	 */
	@Override
	public Tree getLeft() {
		return left;
	}

	/* (non-Javadoc)
	 * @see Tree#getRight()
	 */
	@Override
	public Tree getRight() {
		return right;
	}

	/* (non-Javadoc)
	 * @see Tree#getSymbol()
	 */
	@Override
	public String getSymbol() {
		return symbol;
	}

	/* (non-Javadoc)
	 * @see Tree#toString()
	 */
	@Override
	public String toString() {
		return isLeaf() ? getSymbol()
				: String.format("(%s, %s, %s)", getSymbol(), getLeft() == null ? "" : getLeft().toString(),
						getRight() == null ? "" : getRight().toString());
	}

	/* (non-Javadoc)
	 * @see Tree#isEnd()
	 */
	@Override
	public boolean isEnd() {
		return isLeaf() || (getLeft().isLeaf() && getSymbol().equals("-"));
	}

	/* (non-Javadoc)
	 * @see Tree#containsAnd()
	 */
	@Override
	public boolean containsAnd() {
		return getSymbol().equals("&") || getLeft().containsAnd() || getRight().containsAnd();

	}

	/* (non-Javadoc)
	 * @see Tree#isDisjunctTree()
	 */
	@Override
	public boolean isDisjunctTree() {
		return isEnd() || (getSymbol().equals("+") && getLeft().isDisjunctTree() && getRight().isDisjunctTree());
	}

	/* (non-Javadoc)
	 * @see Tree#isCNF()
	 */
	@Override
	public boolean isCNF() {
		return isDisjunctTree() || (getSymbol().equals("&") && getLeft().isCNF() && getRight().isCNF());
	}

	/* (non-Javadoc)
	 * @see Tree#prettyPrint()
	 */
	@Override
	public void prettyPrint() {
		prettyPrint("");
	}

	/* (non-Javadoc)
	 * @see Tree#prettyPrint(java.lang.String)
	 */
	@Override
	public void prettyPrint(String prefix) {
		if (getLeft() != null)
			getLeft().prettyPrint(prefix + "| ");
		System.out.println(prefix + getSymbol());
		if (getRight() != null)
			getRight().prettyPrint(prefix + "| ");
	}

}
