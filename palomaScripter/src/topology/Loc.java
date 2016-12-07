package topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Loc {
	public int x;
	public int y;
	private String key;
	private ArrayList<Loc> connectedLocs;
	private ArrayList<Loc> childLocs;
	
	public Loc(int x, int y) {
		this.x = x;
		this.y = y;
		this.key = x+"_"+y;
		this.connectedLocs = new ArrayList<Loc>();
		this.childLocs = new ArrayList<Loc>();
	}

	public void addConnectedLoc(Loc loc) {
		this.connectedLocs.add(loc);
	}
	
	public void addChildLoc(Loc loc) {
		this.childLocs.add(loc);
	}
	
	public ArrayList<Loc> getConnectedLocs() {
		return connectedLocs;
	}
	
	public ArrayList<Loc> getChildLocs() {
		return childLocs;
	}
	
	public ArrayList<Loc> getNeighbours(int range) {
		Set<Loc> set = new HashSet<Loc>();
		
		ArrayList<Loc> ret = new ArrayList<Loc>();
		
		for(Loc loc: connectedLocs) {
			set.add(loc);
			if(range >= 2) {
				for(Loc secLoc: loc.getConnectedLocs()) {
					if(secLoc != this) {
						set.add(secLoc);
					}
					if(range >= 3) {
						for(Loc thirdLoc: secLoc.getConnectedLocs()) {
							if(thirdLoc != this) {
								set.add(thirdLoc);
							}
							
						}
					}
				}
			}
			
		}
		for(Loc loc : set) {
			ret.add(loc);
		}
		return ret;
		
	}


	public void setConnectedLocs(ArrayList<Loc> connectedLocs) {
		this.connectedLocs = connectedLocs;
	}
	
	public String toString(){
		return "(" + x + "," + y + ")";
	}
	
	public void print() {
		String str = x+" " + y +"-- ";
		for(Loc loc: connectedLocs) {
			str += loc.x + "," + loc.y + " ";
		}
		System.out.println(str);
	}
	
	public static void conLocs(Loc loc1, Loc loc2) {
		if(loc1.isConnected(loc2)) {
			return;
		}
		loc1.addConnectedLoc(loc2);
		loc2.addConnectedLoc(loc1);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isConnected(Loc loc) {
		boolean ret = false;
		for(Loc nl: this.connectedLocs) {
			if(nl.getKey().equals(loc.getKey())) {
				return true;
			}
		}
		return ret;
	}
	
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
}
