
public class ProofTree {

	private Clause resolvant;
	private ProofTree leftDerivative;
	private ProofTree rightDerivative;

	public Clause getResolvant() {
		return resolvant;
	}

	public void setResolvant(Clause resolvant) {
		this.resolvant = resolvant;
	}

	public ProofTree getLeftDerivative() {
		return leftDerivative;
	}

	public void setLeftDerivative(ProofTree leftDerivative) {
		this.leftDerivative = leftDerivative;
	}

	public ProofTree getRightDerivative() {
		return rightDerivative;
	}

	public void setRightDerivative(ProofTree rightDerivative) {
		this.rightDerivative = rightDerivative;
	}

}
