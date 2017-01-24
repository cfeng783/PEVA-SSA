package framework;

import java.util.ArrayList;
import java.util.HashMap;

import simulator.Model;
import topology.Loc;
import topology.NestedTopoplogy;
import topology.TopologyFactory;
import trans.RPItem;
import trans.RateItem;
import trans.Trans;
import utality.Utality;

public class ParseHouseSIR {
	final static int patch = 10;
	final static int patch_size = 6;
	
	public static void parse(Model model) {
		ArrayList<String> species = new ArrayList<String>();
		ArrayList<Trans> transArray = new ArrayList<Trans>();
		
		ArrayList<Loc> locs = NestedTopoplogy.make(patch*patch_size, patch, TopologyFactory.RING, TopologyFactory.RING);
		for(Loc loc: locs) {
//			System.out.println(loc.toString());
//			System.out.println("children:");
			for(int i =0; i<loc.getChildLocs().size(); i++) {
				Loc child = loc.getChildLocs().get(i);
//				System.out.println(child.toString());
				species.add("S" + child.toString());
				species.add("I" + child.toString());
				transArray.addAll(makeTransForS(child, loc));
				transArray.addAll(makeTransForI(child, loc));
			}
			
//			System.out.println();
			
			//transArray.addAll(makeTransForR(loc));
		}
		
		for(Trans trans: transArray) {
			System.out.println(trans.toString());
		}
		
		HashMap<String, Integer> agentMap = new HashMap<String, Integer>();
		for(Loc loc: locs) {
			for(int i =0; i<loc.getChildLocs().size(); i++) {
				Loc child = loc.getChildLocs().get(i);
				agentMap.put("S" + child.toString(), 5);
				if(Utality.nextDouble() < 0.1) {
					agentMap.put("I" + child.toString(), 2);
				}else {
					agentMap.put("I" + child.toString(), 0);
				}
				agentMap.put("R" + child.toString(), 0);
			}
			
		}
		
		for(String key: agentMap.keySet()) {
			System.out.println(key + ": " + agentMap.get(key));
		}
		
		model.init(transArray, agentMap);
		
	}
	
	public static ArrayList<Trans> makeTransForS(Loc loc, Loc parent) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
		for(Loc nebLoc: parent.getChildLocs()) {
			ArrayList<RPItem> reactants = new ArrayList<RPItem>();
			ArrayList<RPItem> products = new ArrayList<RPItem>();
			ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
			
			RPItem reactItem = new RPItem("S"+loc.toString(), 1);
			reactants.add(reactItem);
			
			RateItem rateItem = new RateItem("S"+loc.toString(), 1);
			rateItems.add(rateItem);
			RateItem rateItem2 = new RateItem("I"+nebLoc.toString(), 1);
			rateItems.add(rateItem2);
			
			RPItem productItem = new RPItem("I"+loc.toString(), 1);
			products.add(productItem);
			
			Trans trans = new Trans(reactants, products,
					0.01*Utality.nextDouble(),rateItems);
			ret.add(trans);
		}
		
		
		
		for(int i=0; i<parent.getConnectedLocs().size(); i++) {
			int visitNum = Utality.nextInt(parent.getConnectedLocs().get(i).getChildLocs().size());
			ArrayList<RPItem> reactants = new ArrayList<RPItem>();
			ArrayList<RPItem> products = new ArrayList<RPItem>();
			ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
			
			RPItem reactItem = new RPItem("S"+loc.toString(), 1);
			reactants.add(reactItem);
			
			RateItem rateItem = new RateItem("S"+loc.toString(), 1);
			rateItems.add(rateItem);
			
			RPItem productItem = new RPItem("S"+parent.getConnectedLocs().get(i).getChildLocs().get(visitNum).toString(), 1);
			products.add(productItem);
			
			Trans trans = new Trans(reactants, products,
					0.01*Utality.nextDouble(),rateItems);
			ret.add(trans);
		}
		
		ArrayList<RPItem> reactants = new ArrayList<RPItem>();
		ArrayList<RPItem> products = new ArrayList<RPItem>();
		ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
		
		RPItem reactItem = new RPItem("S"+loc.toString(), 1);
		reactants.add(reactItem);
		
		RateItem rateItem = new RateItem("S"+loc.toString(), 1);
		rateItems.add(rateItem);
		RateItem rateItem2 = new RateItem("I"+loc.toString(), 1);
		rateItems.add(rateItem2);
		
		RPItem productItem = new RPItem("I"+loc.toString(), 1);
		products.add(productItem);
		
		Trans trans = new Trans(reactants, products,
				0.02*Utality.nextDouble(),rateItems);
		ret.add(trans);
		
		return ret;
	}
	
	public static ArrayList<Trans> makeTransForI(Loc loc, Loc parent) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
//		for(int i=0; i<parent.getConnectedLocs().size(); i++) {
//			int visitNum = Utality.nextInt(parent.getChildLocs().get(i).getChildLocs().size());
//			ArrayList<RPItem> reactants = new ArrayList<RPItem>();
//			ArrayList<RPItem> products = new ArrayList<RPItem>();
//			ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
//			
//			RPItem reactItem = new RPItem("I"+loc.toString(), 1);
//			reactants.add(reactItem);
//			
//			RateItem rateItem = new RateItem("I"+loc.toString(), 1);
//			rateItems.add(rateItem);
//			
//			RPItem productItem = new RPItem("I"+parent.getChildLocs().get(i).getChildLocs().get(visitNum).toString(), 1);
//			products.add(productItem);
//			
//			Trans trans = new Trans(reactants, products,
//					0.01*Utality.nextDouble(),rateItems);
//			ret.add(trans);
//		}
		
		ArrayList<RPItem> reactants = new ArrayList<RPItem>();
		ArrayList<RPItem> products = new ArrayList<RPItem>();
		ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
		
		RPItem reactItem = new RPItem("I"+loc.toString(), 1);
		reactants.add(reactItem);
		
		RateItem rateItem = new RateItem("I"+loc.toString(), 1);
		rateItems.add(rateItem);;
		
		RPItem productItem = new RPItem("R"+loc.toString(), 1);
		products.add(productItem);
		
		Trans trans = new Trans(reactants, products,
				0.2,rateItems);
		ret.add(trans);
		
		return ret;
	}
	
	public static ArrayList<Trans> makeTransForR(Loc loc) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
		for(Loc nebLoc: loc.getConnectedLocs()) {
			ArrayList<RPItem> reactants = new ArrayList<RPItem>();
			ArrayList<RPItem> products = new ArrayList<RPItem>();
			ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
			
			RPItem reactItem = new RPItem("R"+loc.toString(), 1);
			reactants.add(reactItem);
			
			RateItem rateItem = new RateItem("R"+loc.toString(), 1);
			rateItems.add(rateItem);
			
			RPItem productItem = new RPItem("R"+nebLoc.toString(), 1);
			products.add(productItem);
			
			Trans trans = new Trans(reactants, products,
					0.2,rateItems);
			ret.add(trans);
		}
		
		return ret;
	}

}
