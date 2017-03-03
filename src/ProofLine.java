
public class ProofLine {

	private Clause resolvant;
	private int line1;
	private int line2;

	public ProofLine(Clause clause) {
		this(clause, -1, -1);
	}

	public ProofLine(Clause clause, int line1, int line2) {
		setResolvant(clause);
		setLine1(line1);
		setLine2(line2);
	}

	public int getLine1() {
		return line1;
	}

	public void setLine1(int line1) {
		this.line1 = line1;
	}

	public int getLine2() {
		return line2;
	}

	public void setLine2(int line2) {
		this.line2 = line2;
	}

	public Clause getResolvant() {
		return resolvant;
	}

	public void setResolvant(Clause resolvant) {
		this.resolvant = resolvant;
	}

}
