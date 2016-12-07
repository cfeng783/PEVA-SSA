package topology;

import java.util.ArrayList;

public class NestedTopoplogy {
	public static ArrayList<Loc> make(int numOfLocs, int numberOfNest, int nestTopo, int globalTopo) {
		
		int numberOfLocsPerNest = numOfLocs / numberOfNest;
		
		ArrayList<Loc> ret = TopologyFactory.makeTopology(globalTopo, numberOfNest);
		int count = 0;
		int index = 0;
		for(int i=0; i<numberOfNest; i++) {
			if(count + numberOfLocsPerNest <= numOfLocs) {
				ArrayList<Loc> childLocs = TopologyFactory.makeTopology(nestTopo, numberOfLocsPerNest);
				for(Loc child: childLocs) {
					child.setXY(i, index++);
					ret.get(i).addChildLoc(child);
				}
				count += numberOfLocsPerNest;
			}else {
				int num = numOfLocs-count;
				ArrayList<Loc> childLocs = TopologyFactory.makeTopology(nestTopo, num);
				for(Loc child: childLocs) {
					child.setXY(i, index++);
					ret.get(i).addChildLoc(child);
				}
				count += num;
			}
		}
		
//		for(int i=0;i<ret.size();i++) {
//			System.out.println(ret.get(i).getChildLocs().size());
//		}
		
		return ret;
	}
}
