package topology;

import java.util.ArrayList;

public class GridTopology {
	public static ArrayList<Loc> make(int numOfLocs) {
		ArrayList<Loc> ret = new ArrayList<Loc>();
		
		int size = (int) Math.sqrt(numOfLocs);
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				Loc loc = new Loc(i,j);
				ret.add(loc);
			}
		}
		
		for(int i=0; i<numOfLocs; i++) {
			Loc loc = ret.get(i);
			int row = i/size;
			int colum = i%size;
			
			if(row >= 1) {
				Loc upLoc = ret.get(i-size);
				Loc.conLocs(upLoc, loc);
			} 
			if((row+1)*size+colum < numOfLocs) {
				Loc downLoc = ret.get((row+1)*size+colum);
				Loc.conLocs(downLoc, loc);
			}
			if(colum >= 1) {
				Loc leftLoc = ret.get(i-1);
				Loc.conLocs(leftLoc, loc);
			} 
			if(colum < size-1 && i+1<numOfLocs) {
				Loc rightLoc = ret.get(i+1);
				Loc.conLocs(rightLoc, loc);
			}
			
		}
		
		return ret;
	}
}
