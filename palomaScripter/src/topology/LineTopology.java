package topology;

import java.util.ArrayList;

public class LineTopology {
	public static ArrayList<Loc> makeLineTop(int numOfLocs){
		ArrayList<Loc> ret = new ArrayList<Loc>();
		for(int i=0; i<numOfLocs; i++) {
			Loc loc = new Loc(i,i);
			if(i != 0) {
				Loc.conLocs(loc, ret.get(i-1));
			}
			ret.add(loc);
		}
		return ret;
	}
}
