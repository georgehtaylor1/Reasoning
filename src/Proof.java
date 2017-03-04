import java.io.PrintStream;
import java.util.ArrayList;

public class Proof extends ArrayList<ProofLine> {

	private boolean satisfiable;
	private Formula formula;
	private boolean proven;

	public Proof(Formula formula) {
		setFormula((Formula) formula.clone());
		setProven(false);
	}

	/**
	 * Resolve the formula to determine whether it is satisfiable or not
	 */
	public void resolve() {
		resolve(false, System.out);
	}

	/**
	 * Resolve the formula to determine whether it is satisfiable or not and specify whether there should be a verbose runtime output
	 * 
	 * @param verbose
	 *            Should there be a runtime output
	 */
	public void resolve(boolean verbose) {
		resolve(verbose, System.out);
	}

	/**
	 * Resolve the formula to determine whether it is satisfiable or not and specify whether there should be a verbose runtime output and where that output
	 * should go
	 * 
	 * @param verbose
	 *            Should there be a runtime output
	 * @param output
	 *            Where should the output go
	 */
	public void resolve(boolean verbose, PrintStream output) {

		if (verbose)
			output.println("Attempting to resolve the formula: " + getFormula().toString());

		// Add all of the initial clauses to the proof
		for (Clause c : getFormula()) {
			this.add(new ProofLine(c));
		}

		int previousFormulaSize = this.size();
		byte proved = 0;

		while (proved == 0) {
			if (resolutionStep(verbose, output)) {
				if (verbose)
					output.println("Box found, formula unsatisfiable");
				proved = 2;
			}

			if (this.size() == previousFormulaSize) {
				if (verbose)
					output.println("No more clauses resolved, formula satisfiable");
				proved = 1;
			}

			previousFormulaSize = this.size();
		}

		// If proved == 1 then it is satisfiable otherwise it is unsatisfiable 
		setSatisfiable(proved == 1);
		setProven(true);

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

		ArrayList<Clause> clauseList = new ArrayList<Clause>(getFormula());

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
					if (getFormula().add(resolvant))
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

	@Override
	public String toString() {
		String r = "#===============================================#\n";
		r = r + "#                                               #\n";
		r = r + String.format("# Proof for: %-34s #\n", getFormula().getOriginalFormula());
		r = r + "#                                               #\n";
		r = r + "# Line no. | Clauses    | Resolvant             #\n";
		r = r + "# ---------+------------+---------------------- #\n";

		int i = 0;
		for (ProofLine l : this) {
			r = r + String.format("# %8d |  %3s, %3s  | %-21s #\n", i++, lineNumber(l.getLine1()),
					lineNumber(l.getLine2()), l.getResolvant().toString());
		}
		r = r + "#                                               #\n";
		r = r + "# This formula is " + (isSatisfiable() ? "satisfiable  " : "unsatisfiable") + "                 #\n";
		r = r + "#                                               #\n";
		r = r + "#===============================================#\n";
		return r;
	}

	private String lineNumber(int lineNumber) {
		if (lineNumber == -1)
			return "-";
		return Integer.toString(lineNumber);
	}

	public boolean isSatisfiable() {
		return satisfiable;
	}

	public void setSatisfiable(boolean satisfiable) {
		this.satisfiable = satisfiable;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public boolean isProven() {
		return proven;
	}

	public void setProven(boolean proven) {
		this.proven = proven;
	}

}
