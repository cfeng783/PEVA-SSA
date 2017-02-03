package directedrelationgraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import trans.Trans;

public class DRG {
	private ArrayList<DRGNode> nodeList = new ArrayList<DRGNode>();
	private HashMap<String, Integer> nodeIndexMap = new HashMap<String, Integer>();
	private HashMap<String, Trans> transMap = new HashMap<String, Trans>();
	private double dist[][];
	
	public DRG(HashMap<String, Integer> initAgentMap, ArrayList<Trans> transArray) {
		int index = 0;
		for(String agent: initAgentMap.keySet()) {
			DRGNode node = new DRGNode(agent, DRGNode.POP);
			
			double value = 0;
			for(Trans trans: transArray) {
				int update = trans.produced(agent) - trans.consumed(agent);
				value += Math.abs(update)*trans.getFireCount();
			}
			node.setValue(value);
			
			nodeList.add(node);
			nodeIndexMap.put(agent, index);
			index++;
		}
		
		for(Trans trans: transArray) {
			DRGNode node = new DRGNode(trans.getID(), DRGNode.TNS);
			node.setValue(trans.getFireCount());
			nodeList.add(node);
			transMap.put(trans.getID(), trans);
			nodeIndexMap.put(trans.getID(), index);
			index++;
		}
	}
	
	public void makeGraph() {		
		dist = new double[nodeList.size()][nodeList.size()];	
		
		for(int i=0; i<nodeList.size(); i++) {
			for(int j=0; j<nodeList.size(); j++) {
				dist[i][j] = 0;
				DRGNode node1 = nodeList.get(i);
				DRGNode node2 = nodeList.get(j);
				if(node1.getType() == node2.getType()) {
					dist[i][j] = 0;
				}
				
				if(node1.getType() == DRGNode.POP && node2.getType() == DRGNode.TNS) {
					Trans trans = transMap.get(node2.getName());
					
					int update = trans.consumed(node1.getName()) - trans.produced(node1.getName());
							
					if(update != 0) {
						dist[i][j] = node2.getValue()*Math.abs(update) / node1.getValue();
					}else {
						dist[i][j] = 0;
					}
				}
				
				if(node1.getType() == DRGNode.TNS && node2.getType() == DRGNode.POP) {
					Trans trans = transMap.get(node1.getName());
					if(trans.isContributor(node2.getName())) {
						dist[i][j] = 1;
					}else {
						dist[i][j] = 0;
					}
				}
				
			}
		}
		
		for(int k=0; k<nodeList.size(); k++) {
			for(int i=0; i<nodeList.size(); i++) {
				for(int j=0; j<nodeList.size(); j++) {					
					if(dist[i][j] < dist[i][k] * dist[k][j])
						dist[i][j] = dist[i][k] * dist[k][j];
				}
			}
		}
		
		for(int k=0; k<nodeList.size(); k++) {		
			dist[k][k] = 1;
		}
	}
	
	public HashSet<String> makeSkeletalMechanism(Set<String> targetSet, double threshold) {
		HashSet<String> skeletalSet = new HashSet<String>();
		
		for(String target: targetSet) {
			int headIndex = nodeIndexMap.get(target);
			
			SortNode[] sortArray = new SortNode[transMap.keySet().size()];
			int index = 0;
			for(String transKey: transMap.keySet()) {
				int tailIndex = nodeIndexMap.get(transKey);
				double pathSum = dist[headIndex][tailIndex];				
				sortArray[index] = new SortNode(transKey, pathSum);
				index++;
			}
			Arrays.sort(sortArray);
			
			//print coupling coefficients
			print2File(sortArray);
			
			for(int i=0; i<sortArray.length; i++) {
				if(sortArray[i].getValue() > threshold) {
					skeletalSet.add(sortArray[i].getName());
				}
			}
			
			
		}
		
		return skeletalSet;
		
	}
	
	public void print2File(SortNode[] sortArray) {
//		System.out.println("print to file");
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/Desktop/before.txt", false)));
			for (int i=0; i<sortArray.length; i++) {
				out.println(sortArray[i].getValue());
			}
		    out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
