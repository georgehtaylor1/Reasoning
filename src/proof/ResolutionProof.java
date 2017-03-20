package proof;
import java.io.PrintStream;
import java.util.ArrayList;

import formula.Clause;
import formula.Formula;
import formula.Literal;

public class ResolutionProof extends ArrayList<ProofLine> implements Proof {

	private Formula formula;
	private Formula originalFormula;
	private Formula modFormula;
	private boolean proven;

	private long executionTime;

	/**
	 * Takes a negated formula and attempts to prove it through resolution
	 * 
	 * @param formula
	 *            The negated formula
	 */
	public ResolutionProof(Formula formula, Formula originalFormula) {
		setFormula(formula);
		setOriginalFormula(originalFormula);
		setModFormula((Formula) getFormula().clone());
		setProven(false);
	}

	/* (non-Javadoc)
	 * @see Proof#prove()
	 */
	@Override
	public void prove() {
		prove(false, System.out);
	}

	/* (non-Javadoc)
	 * @see Proof#prove(boolean)
	 */
	@Override
	public void prove(boolean verbose) {
		prove(verbose, System.out);
	}

	/* (non-Javadoc)
	 * @see Proof#prove(boolean, java.io.PrintStream)
	 */
	@Override
	public void prove(boolean verbose, PrintStream output) {

		if (verbose)
			output.println("Attempting to resolve the formula: " + getFormula().toString());

		System.gc();
		long startTime = System.nanoTime();

		// Add all of the initial clauses to the proof
		for (Clause c : getModFormula()) {
			this.add(new ProofLine(c));
		}

		int previousFormulaSize = this.size();
		byte proved = 0;

		while (proved == 0) {
			if (resolutionStep(verbose, output)) {
				if (verbose)
					output.println("Box found, negated formula unsatisfiable");
				proved = 2;
			}

			if (this.size() == previousFormulaSize) {
				if (verbose)
					output.println("No more clauses resolved, negated formula satisfiable");
				proved = 1;
			}

			previousFormulaSize = this.size();
		}

		// If proved == 1 then it is satisfiable otherwise it is unsatisfiable
		getFormula().setConclusion(proved == 1 ? Conclusion.SATISFIABLE : Conclusion.UNSATISFIABLE);
		getOriginalFormula().setConclusion(proofConclusion(this.getFormula().getConclusion()));
		setProven(true);

		long endTime = System.nanoTime();
		setExecutionTime(endTime - startTime);

		if (verbose) {
			output.println(this.toString());
		}
	}

	/**
	 * Execute one run of the resolution algorithm
	 * 
	 * @return Whether the formula is unsatisfiable
	 */
	private boolean resolutionStep(boolean verbose, PrintStream output) {

		ArrayList<Clause> clauseList = new ArrayList<Clause>(getModFormula());

		for (int i = 0; i < clauseList.size() - 1; i++) {
			for (int j = i + 1; j < clauseList.size(); j++) {
				Clause first = clauseList.get(i);
				Clause second = clauseList.get(j);
				if (verbose)
					output.println("Attempting to resolve " + first.toString() + " & " + second.toString());

				Literal l = getComplementaryLiteral(first, second);
				if (l != null) {
					Clause resolvant = resolveClauses(first, second, l);
					if (verbose) {
						output.println("Found complimentary literal " + l.toString());
						output.println("Clauses resolved to " + resolvant.toString());
					}
					if (getModFormula().add(resolvant))
						this.add(new ProofLine(resolvant, i, j));
					if (resolvant.isEmpty())
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Resolve two clauses. Precondition: The two clauses contain at least one complimentary literal
	 * 
	 * @param c1
	 *            The first clause (The literal came from here)
	 * @param c2
	 *            The second clause
	 * @param a
	 *            The complimentary atom
	 * @return The clause created by the resolution
	 */
	private static Clause resolveClauses(Clause c1, Clause c2, Literal a) {

		Clause newC1 = new Clause();
		newC1.addAll(c1);
		newC1.remove(a);

		Clause newC2 = new Clause();
		newC2.addAll(c2);
		newC2.remove(a.getCompliment());

		Clause result = new Clause();
		result.addAll(newC1);
		result.addAll(newC2);

		return result;
	}

	/**
	 * Do two sets contain a complimentary literal
	 * 
	 * @param s1
	 *            The first set
	 * @param s2
	 *            The second set
	 * @return Whether or not the sets contain complimentary literals
	 */
	private static Literal getComplementaryLiteral(Clause s1, Clause s2) {
		for (Literal l : s1) {
			Literal testLiteral = l.getCompliment();
			if (s2.contains(testLiteral))
				return l;
		}
		return null;
	}

	/**
	 * Produce a pretty string for the proof
	 */
	@Override
	public String toString() {
		String r = "#=================================================#\n";
		r = r + "#                                                 #\n";
		r = r + String.format("# Proving: %-36s   #\n", getOriginalFormula().getOriginalFormula());
		r = r + "#                                                 #\n";
		r = r + String.format("# Proof for: %-34s   #\n", getFormula().getOriginalFormula());
		r = r + "#                                                 #\n";
		if (isProven()) {
			r = r + "# Line no. | Clauses    | Resolvant               #\n";
			r = r + "# ---------+------------+----------------------   #\n";

			int i = 0;
			for (ProofLine l : this) {
				r = r + String.format("# %8d |  %3s, %3s  | %-21s   #\n", i++, lineNumber(l.getLine1()),
						lineNumber(l.getLine2()), l.getResolvant().toString());
			}
			r = r + "#                                                 #\n";
			r = r + String.format("# This negated formula is %-23s #\n", getFormula().getConclusion().toString());
			r = r + String.format("# Therefore the original formula is %-13s #\n",
					proofConclusion(getFormula().getConclusion()).toString());
			r = r + "#                                                 #\n";
			r = r + "# Execution completed in:                         #\n";
			r = r + String.format("# %35d nanoseconds #\n", getExecutionTime());
		} else {
			r = r + "# This formula is not yet proven                  #\n";
		}
		r = r + "#                                                 #\n";
		r = r + "#=================================================#\n";
		return r;
	}

	/**
	 * Convert a refutation conclusion into the actual conclusion
	 * 
	 * @param c
	 *            The refutation conclusion
	 * @return The actual conclusion
	 */
	private Conclusion proofConclusion(Conclusion c) {
		if (c == Conclusion.SATISFIABLE)
			return Conclusion.INVALID;
		if (c == Conclusion.UNSATISFIABLE)
			return Conclusion.VALID;
		return Conclusion.UNKNOWN;
	}

	/**
	 * Return a string for the line number where any -1's are replaced by '-'
	 * 
	 * @param lineNumber
	 *            The line number to be displayed
	 * @return The new line number
	 */
	private String lineNumber(int lineNumber) {
		if (lineNumber == -1)
			return "-";
		return Integer.toString(lineNumber);
	}

	/**
	 * Get the formula for this proof
	 * 
	 * @return The formula used for this proof
	 */
	public Formula getFormula() {
		return formula;
	}

	/**
	 * Set the formula for this proof
	 * 
	 * @param formula
	 *            The formula to be proven
	 */
	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	/* (non-Javadoc)
	 * @see Proof#isProven()
	 */
	@Override
	public boolean isProven() {
		return proven;
	}

	/* (non-Javadoc)
	 * @see Proof#setProven(boolean)
	 */
	@Override
	public void setProven(boolean proven) {
		this.proven = proven;
	}

	/**
	 * Get the modifiable formula
	 * 
	 * @return The modifiable formula
	 */
	public Formula getModFormula() {
		return modFormula;
	}

	/**
	 * Set the modifiable formula
	 * 
	 * @param modFormula
	 *            The new modifiable formula
	 */
	public void setModFormula(Formula modFormula) {
		this.modFormula = modFormula;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public Formula getOriginalFormula() {
		return originalFormula;
	}

	public void setOriginalFormula(Formula originalFormula) {
		this.originalFormula = originalFormula;
	}

}
