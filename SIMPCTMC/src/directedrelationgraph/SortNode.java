package directedrelationgraph;


public class SortNode implements Comparable<SortNode>{
	private String name;
	private double value;
	
	public SortNode(String name, double value) {
		this.name = name;
		this.value = value;
//		System.out.println(name + " " + value);
	}


	@Override
	public int compareTo(SortNode o) {
		if(this.value < o.value) {
			return -1;
		}
		
		if(this.value > o.value) {
			return 1;
		}
		
		return 0;
	}




	public String getName() {
		return name;
	}

	public double getValue() {
		return this.value;
	}


	public void setName(String name) {
		this.name = name;
	}
}