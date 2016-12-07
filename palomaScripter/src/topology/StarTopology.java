package topology;

import java.util.ArrayList;

public class StarTopology {
	public static ArrayList<Loc> make(int numOfLocs){
		ArrayList<Loc> ret = new ArrayList<Loc>();
		Loc center = new Loc(0,0);
		ret.add(center);
		for(int i=1; i<numOfLocs; i++) {
			Loc loc = new Loc(i,i);
			Loc.conLocs(loc, center);
			ret.add(loc);
		}
		return ret;
	}
}
