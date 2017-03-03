import java.util.ArrayList;

public class Proof extends ArrayList<ProofLine> {

	@Override
	public String toString() {
		String r = "";

		int i = 0;
		for (ProofLine l : this) {
			r = r + String.format("%4d|  %3d, %3d  |  %20s\n", i++, l.getLine1(), l.getLine2(), l.getResolvant().toString());
		}
		return r;
	}

}
