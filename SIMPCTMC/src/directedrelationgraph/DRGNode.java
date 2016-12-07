package directedrelationgraph;

public class DRGNode {

	public static final int TNS = 101;
	public static final int POP = 102; 
	
	private String name;
	private int type;
	private double value;
	
	public DRGNode(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	
	
}
