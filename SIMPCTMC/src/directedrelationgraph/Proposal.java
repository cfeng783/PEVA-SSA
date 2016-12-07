package directedrelationgraph;

import java.util.ArrayList;
import java.util.HashMap;

import trans.Trans;

public class Proposal {
	private double error;
	private ArrayList<Trans> skeletalTransArray = new ArrayList<Trans>();
	private HashMap<String, Integer> skeletalAgentMap = new HashMap<String, Integer>();
	
	public Proposal(double error, ArrayList<Trans> skeletalTransArray, HashMap<String, Integer> skeletalAgentMap) {
		this.error = error;
		this.skeletalAgentMap = skeletalAgentMap;
		this.skeletalTransArray = skeletalTransArray;
	}
	
	
	
	public ArrayList<Trans> getSkeletalTransArray() {
		return skeletalTransArray;
	}
	public void setSkeletalTransArray(ArrayList<Trans> skeletalTransArray) {
		this.skeletalTransArray = skeletalTransArray;
	}
	public HashMap<String, Integer> getSkeletalAgentMap() {
		return skeletalAgentMap;
	}
	public void setSkeletalAgentMap(HashMap<String, Integer> skeletalAgentMap) {
		this.skeletalAgentMap = skeletalAgentMap;
	}


	public double getError() {
		return error;
	}


	public void setError(double error) {
		this.error = error;
	}
}
