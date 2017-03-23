package proof;

public enum Interpretation {
	FREE("Free"), TRUE("True"), FALSE("False");

	private String name;

	/**
	 * Create a new interpretation with the specified name string
	 * 
	 * @param name
	 *            The name of the interpretation
	 */
	private Interpretation(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
}
