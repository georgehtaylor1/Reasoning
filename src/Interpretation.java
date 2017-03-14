
public enum Interpretation {
	FREE("Free"), TRUE("True"), FALSE("False");
	
	private String name;
	private Interpretation(String name){
		this.name = name;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
