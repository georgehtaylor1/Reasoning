package proof;

public enum Conclusion {

	VALID("valid"), INVALID("invalid"), SATISFIABLE("satisfiable"), UNSATISFIABLE("unsatisfiable"), UNKNOWN("unknown");

	private final String name;

	/**
	 * Create the conclusion with the specified string representation
	 * 
	 * @param name
	 *            The string representation of the conclusion
	 */
	Conclusion(String name) {
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
