
public class Atom {

	private boolean negation = false;
	private String symbol;

	public Atom(String symbol, boolean negation) {
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
		return ((Atom) obj).getSymbol().equals(this.getSymbol()) && (((Atom) obj).isNegation() == this.isNegation());
	}

}
