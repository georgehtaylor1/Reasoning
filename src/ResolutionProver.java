import java.io.PrintStream;
import java.util.ArrayList;

public class ResolutionProver {

	private static Proof proof;
	private static Formula formula;
	private static boolean satisfiable;

	/**
	 * Create a new resolution prover and prove the given formula
	 * 
	 * @param formula
	 *            The formula to be proved given in conjunctive normal form
	 */
	public ResolutionProver(Formula formula) {
		setFormula(formula);
		proof = new Proof();
		setSatisfiable(resolve());
	}

	/**
	 * Resolve the formula to determine whether it is satisfiable or not
	 * 
	 * @return true if the formula is satisfiable, false if it is unsatisfiable
	 */
	public static boolean resolve() {
		return resolve(false, System.out);
	}

	/**
	 * Resolve the formula to determine whether it is satisfiable or not
	 * 
	 * @return true if the formula is satisfiable, false if it is unsatisfiable
	 */
	public static boolean resolve(boolean verbose, PrintStream output) {

		// Add all of the initial clauses to the proof
		for (Clause c : formula) {
			proof.add(new ProofLine(c));
		}

		int previousFormulaSize = formula.size();

		while (true) {
			if (resolutionStep())
				return false;

			if (formula.size() == previousFormulaSize)
				return true;

			previousFormulaSize = formula.size();
		}
	}

	/**
	 * Execute one run of the resolution algorithm
	 * 
	 * @return Whether the formula is unsatisfiable
	 */
	private static boolean resolutionStep() {

		ArrayList<Clause> clauseList = new ArrayList<Clause>(getFormula());

		for (int i = 0; i < clauseList.size() - 1; i++) {
			for (int j = i + 1; j < clauseList.size(); j++) {
				Clause first = clauseList.get(i);
				Clause second = clauseList.get(j);

				Literal l = getComplementaryLiteral(first, second);
				if (l != null) {
					Clause resolvant = resolveClauses(first, second, l);
					if (formula.add(resolvant))
						proof.add(new ProofLine(resolvant, i, j));
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
	 * Get the formula supplied to the prover
	 * 
	 * @return The formula supplied to the prover
	 */
	public static Formula getFormula() {
		return formula;
	}

	/**
	 * Set the formula to be proved
	 * 
	 * @param formula
	 *            The formula in conjunctive normal form to be proved
	 */
	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Proof getProof() {
		return proof;
	}

	public void setProof(Proof proof) {
		this.proof = proof;
	}

	public static boolean isSatisfiable() {
		return satisfiable;
	}

	public static void setSatisfiable(boolean satisfiable) {
		ResolutionProver.satisfiable = satisfiable;
	}

	@Override
	public String toString() {
		return proof.toString() + "  The formula is " + (isSatisfiable() ? "" : "un") + "satisfiable  ";

	}

}
