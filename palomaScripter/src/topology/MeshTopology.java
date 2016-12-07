package topology;

import java.util.ArrayList;

public class MeshTopology {
	public static ArrayList<Loc> make(int numOfLocs) {
		ArrayList<Loc> ret = new ArrayList<Loc>();
		
		for(int i=0; i<numOfLocs; i++) {
			Loc loc = new Loc(i,i);
			ret.add(loc);
		}
		
		for(int i=0; i<numOfLocs; i++) {
			for(int j=0; j<numOfLocs; j++) {
				if(i!=j) {
					Loc.conLocs(ret.get(i), ret.get(j));
				}
			}
		}
		
		return ret;
	}
}
