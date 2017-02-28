
public abstract class Tree {

	public abstract boolean isLeaf();

	public abstract boolean isEnd();

	public abstract Tree getLeft();

	public abstract Tree getRight();

	public abstract String getSymbol();

	public abstract String toString();

	public abstract boolean containsAnd();

	public abstract boolean isLiteral();

	public abstract boolean isDisjunctTree();

	public abstract boolean isCNF();

}
