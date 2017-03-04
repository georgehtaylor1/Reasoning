
/**
 * @author george
 *
 */
public abstract class Tree {

	/**
	 * @return A boolean indicating whether or not this is a leaf node
	 */
	public abstract boolean isLeaf();

	/**
	 * @return A boolean indicating whether or not this is a literal
	 */
	public abstract boolean isEnd();

	/**
	 * @return The left subtree
	 */
	public abstract Tree getLeft();

	/**
	 * @return The right subtree
	 */
	public abstract Tree getRight();

	/**
	 * @return The symbol at the root of the tree
	 */
	public abstract String getSymbol();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	/**
	 * @return A boolean indicating whether this tree contains any conjunctions
	 */
	public abstract boolean containsAnd();

	/**
	 * @return A boolean indicating whether or not the tree is a disjunct tree (contains only or's)
	 */
	public abstract boolean isDisjunctTree();

	/**
	 * @return A boolean indicating whether the tree is in clause normal form
	 */
	public abstract boolean isCNF();

	/**
	 * Pretty print the tree
	 */
	public abstract void prettyPrint();

	/**
	 * Pretty print the tree with the given prefix
	 * 
	 * @param string
	 *            The prefix used to represent the current depth of the tree
	 */
	public abstract void prettyPrint(String string);

}
