package topology;

import java.util.ArrayList;

public class TreeTopology {
	public static ArrayList<Loc> makeTreeTop(int numOfLocs){
		ArrayList<Loc> ret = new ArrayList<Loc>();
		int i = 0;
		while(true) {
			if(createSubTree(ret, i, numOfLocs) == false) {
				break;
			}
			i++;
			
		}
		return ret;
	}
	
	public static boolean createSubTree(ArrayList<Loc> locArray, int layer, int numberOfLocs) {
		int num = (int) Math.pow(2, layer);
		int startNum = locArray.size();
		for(int i=0; i<num; i++) {
			if(startNum+i >= numberOfLocs) {
				return false;
			}
			Loc loc = new Loc(startNum+i, startNum+i);
			locArray.add(loc);
			if(layer == 0) {
				return true;
			}
			if(loc.x % 2 == 1) {
				Loc father = locArray.get((loc.x-1)/2);
				Loc.conLocs(father, loc);
			}else {
				Loc father = locArray.get((loc.x-2)/2);
				Loc.conLocs(father, loc);
			}
			
			
		}
		return true;
	}
	
	
	
	
}
