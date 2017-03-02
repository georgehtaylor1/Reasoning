import java.util.HashMap;

public class AtomMap {

	private HashMap<String, Integer> atomMap;
	private int current = 0;

	public AtomMap() {
		atomMap = new HashMap<String, Integer>();
	}

	public void add(Literal atom) {
		atomMap.put(atom.toString(), current++);
	}
	
	public int get(Literal atom){
		return atomMap.get(atom.toString()); 
	}

}
