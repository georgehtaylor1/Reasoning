import java.io.PrintStream;

public interface Proof {

	/**
	 * Prove the formula to determine whether it is satisfiable or not
	 */
	public abstract void prove();

	/**
	 * Prove the formula to determine whether it is satisfiable or not and specify whether there should be a verbose runtime output
	 * 
	 * @param verbose
	 *            Should there be a runtime output
	 */
	public abstract void prove(boolean verbose);

	/**
	 * Prove the formula to determine whether it is satisfiable or not and specify whether there should be a verbose runtime output and where that output should
	 * go
	 * 
	 * @param verbose
	 *            Should there be a runtime output
	 * @param output
	 *            Where should the output go
	 */
	public abstract void prove(boolean verbose, PrintStream output);

	/**
	 * Has this formula been proven
	 * 
	 * @return A boolean representing whether or not this formula has been proven
	 */
	public abstract boolean isProven();

	/**
	 * Set whether or not the formula has been proven
	 * 
	 * @param proven
	 *            The new value for whether or not the formula has been proven
	 */
	public abstract void setProven(boolean proven);

	/**
	 * Get a string for the proof
	 * 
	 * @return The string representation of the proof
	 */
	@Override
	public abstract String toString();

}
