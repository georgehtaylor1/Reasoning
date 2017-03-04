
public class Literal {

	private boolean negation = false;
	private String symbol;

	/**
	 * Create a new literal with the given symbol and indicate whether or not it is negated
	 * 
	 * @param symbol
	 *            The symbol for the literal
	 * @param negation
	 *            A boolean indicating whether the literal is negated
	 */
	public Literal(String symbol, boolean negation) {
		setSymbol(symbol);
		setNegation(negation);
	}

	/**
	 * Is the literal negated
	 * 
	 * @return A boolean indicating whether the literal is negated
	 */
	public boolean isNegation() {
		return negation;
	}

	/**
	 * Set whether the literal is negated or not
	 * 
	 * @param negation
	 *            The new value for whether the literal is negated
	 */
	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	/**
	 * Get the symbol for the literal
	 * 
	 * @return The symbol of the literal
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Set the symbol of the literal
	 * 
	 * @param symbol
	 *            The new symbol for the literal
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Return a string for the literal
	 */
	public String toString() {
		return (negation ? "-" : "") + getSymbol();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		byte ascii[] = getSymbol().getBytes();
		int mul = 1;
		int val = 0;
		for (byte b : ascii) {
			val += mul * b;
			mul *= 255;
		}
		return val;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return ((Literal) obj).getSymbol().equals(this.getSymbol())
				&& (((Literal) obj).isNegation() == this.isNegation());
	}

	/**
	 * Return the compliment of this literal
	 * 
	 * @return The compliment of the literal
	 */
	public Literal getCompliment() {
		return new Literal(this.getSymbol(), !this.isNegation());
	}

}
