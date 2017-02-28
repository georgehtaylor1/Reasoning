import java.util.HashMap;

public class AtomMap {

	private HashMap<String, Integer> atomMap;
	private int current = 0;

	public AtomMap() {
		atomMap = new HashMap<String, Integer>();
	}

	public void add(Atom atom) {
		atomMap.put(atom.toString(), current++);
	}
	
	public int get(Atom atom){
		return atomMap.get(atom.toString()); 
	}

}
