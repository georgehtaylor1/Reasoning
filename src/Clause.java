import java.util.HashSet;

public class Clause extends HashSet<Literal> implements Comparable<Clause> {

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Clause c) {
		if (c == null)
			return -1;
		return (c.size() == this.size() && c.containsAll(this)) ? 0 : this.size() > c.size() ? 1 : -1;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractSet#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.compareTo((Clause) obj) == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractSet#hashCode()
	 */
	@Override
	public int hashCode() {
		int sum = 0;
		for (Literal l : this)
			sum += l.hashCode();
		return sum;
	}

}
