
public class Branch extends Tree {

	private String symbol;

	private Tree left;
	private Tree right;

	public Branch(String symbol, Tree left, Tree right) {
		this.symbol = symbol;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean isLeaf() {
		return getLeft() == null && getRight() == null;
	}

	@Override
	public Tree getLeft() {
		return left;
	}

	@Override
	public Tree getRight() {
		return right;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return isLeaf() ? getSymbol()
				: String.format("(%s, %s, %s)", getSymbol(), getLeft() == null ? "" : getLeft().toString(),
						getRight() == null ? "" : getRight().toString());
	}

	@Override
	public boolean isEnd() {
		return isLeaf() || (getLeft().isLeaf() && getSymbol().equals("-"));
	}

	@Override
	public boolean containsAnd() {
		return getSymbol().equals("&") || getLeft().containsAnd() || getRight().containsAnd();

	}

	@Override
	public boolean isLiteral() {
		return !containsAnd();
	}

	@Override
	public boolean isDisjunctTree() {
		return isEnd() || (getSymbol().equals("|") && getLeft().isDisjunctTree() && getRight().isDisjunctTree());
	}

	@Override
	public boolean isCNF() {
		return isDisjunctTree() || (getSymbol().equals("&") && getLeft().isCNF() && getRight().isCNF());
	}

}
