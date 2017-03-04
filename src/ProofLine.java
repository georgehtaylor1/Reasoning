
public class ProofLine {

	private Clause resolvant;
	private int line1;
	private int line2;

	/**
	 * Create a new line of proof with only a premise
	 * 
	 * @param clause
	 *            The premise clause
	 */
	public ProofLine(Clause clause) {
		this(clause, -1, -1);
	}

	/**
	 * Create a new line of proof
	 * 
	 * @param clause
	 *            The resolvant clause
	 * @param line1
	 *            The line number of the first clause used in the resolution
	 * @param line2
	 *            The line number of the second clause used in the resolution
	 */
	public ProofLine(Clause clause, int line1, int line2) {
		setResolvant(clause);
		setLine1(line1);
		setLine2(line2);
	}

	/**
	 * Get the line number of the first clause used in the resolution
	 * 
	 * @return The line of the proof where the first clause was declared
	 */
	public int getLine1() {
		return line1;
	}

	/**
	 * Set the line number of the first clause that was used to generate the resolvant
	 * 
	 * @param line1
	 *            The new line number
	 */
	public void setLine1(int line1) {
		this.line1 = line1;
	}

	/**
	 * Get the line number of the second clause used in the resolution
	 * 
	 * @return The line of the proof where the second clause was declared
	 */
	public int getLine2() {
		return line2;
	}

	/**
	 * Set the line number of the second clause that was used to generate the resolvant
	 * 
	 * @param line1
	 *            The new line number
	 */
	public void setLine2(int line2) {
		this.line2 = line2;
	}

	/**
	 * Get the resolvant clause
	 * 
	 * @return The resolvant clause for this line of the proof
	 */
	public Clause getResolvant() {
		return resolvant;
	}

	/**
	 * Set the resolvant clause for this line of the proof
	 * 
	 * @param resolvant
	 *            The new resolvant clause for this line of the proof
	 */
	public void setResolvant(Clause resolvant) {
		this.resolvant = resolvant;
	}

}
