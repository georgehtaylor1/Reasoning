package proof;

import java.util.HashMap;

public class Model extends HashMap<String, Interpretation> {

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		String r = "";
		for (Entry<String, Interpretation> e : this.entrySet()) {
			r += String.format("%10s %s\n", e.getKey(), e.getValue().toString());
		}
		return r;
	}

}
