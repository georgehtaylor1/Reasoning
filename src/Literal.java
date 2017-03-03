
public class Literal {

	private boolean negation = false;
	private String symbol;

	public Literal(String symbol, boolean negation) {
		setSymbol(symbol);
		setNegation(negation);
	}

	public boolean isNegation() {
		return negation;
	}

	public void setNegation(boolean negation) {
		this.negation = negation;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String toString() {
		return (negation ? "-" : "") + getSymbol();
	}

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
