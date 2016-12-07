package topology;

import java.util.ArrayList;

public class TopologyFactory {
	final public static int TREE = 101;
	final public static int LINE = 102;
	final public static int STAR = 103;
	final public static int RING = 104;
	final public static int MESH = 105;
	final public static int GRID = 106;
	
	public static ArrayList<Loc> makeTopology(int type, int numOfLocs) {
		switch(type) {
			case TREE: return TreeTopology.makeTreeTop(numOfLocs); 
			case LINE: return LineTopology.makeLineTop(numOfLocs);
			case STAR: return StarTopology.make(numOfLocs);
			case RING: return RingTopology.make(numOfLocs);
			case GRID: return GridTopology.make(numOfLocs);
			case MESH: return MeshTopology.make(numOfLocs);
			default: return null;
		}
	}
	
}
