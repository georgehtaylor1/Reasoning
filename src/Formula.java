import java.util.HashSet;

public class Formula extends HashSet<Clause> {

	public boolean equals(Formula f) {

		return this.size() == f.size() && f.containsAll(this);

	}

}
