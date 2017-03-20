package proof;
import java.util.HashMap;

public class Model extends HashMap<String, Interpretation> {

	@Override
	public String toString() {
		String r = "";
		for (Entry<String, Interpretation> e : this.entrySet()) {
			r = r + String.format("%10s %s\n", e.getKey(), e.getValue().toString());
		}
		return r;
	}

}
