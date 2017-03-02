import java.util.HashSet;

public class Clause extends HashSet<Literal> implements Comparable<Clause> {

	@Override
	public int compareTo(Clause c) {
		if (c == null)
			return -1;
		return (c.size() == this.size() && c.containsAll(this)) ? 0 : this.size() > c.size() ? 1 : -1;
	}

	public boolean equals(Clause c) {
		return this.compareTo(c) == 0;
	}

	public int hashCode() {
		return 0;
	}

}
