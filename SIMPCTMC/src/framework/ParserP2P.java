package framework;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import simulator.Model;
import trans.RPItem;
import trans.RateItem;
import trans.Trans;
import utality.Utality;

public class ParserP2P {
	final static int loc_num = 30;
	
	
	static double trip[][] = new double[loc_num][loc_num];
	static int valid[][] = new int[loc_num][loc_num];
	static double contact[] = new double[loc_num];
	static int initS[] = new int[loc_num];
	static int initI[] = new int[loc_num];
	static int initR[] = new int[loc_num];
	static DecimalFormat df = new DecimalFormat("##.00");
	static double recover = 0.1;
	static final int connected_num = 3;
	
	
	private static void init() {
		
		for(int i=0; i<loc_num; i++) {
			contact[i] = Double.parseDouble(df.format(Utality.nextDouble()));
			initS[i] = Utality.nextInt(50);
			initI[i] = Utality.nextInt(5);
			initR[i] = 0;
			
			int array[] = new int[connected_num];
			int k=0;
			while(k<connected_num) {
				int newvalue = Utality.nextInt(loc_num);
				if(newvalue != i) {
					boolean included = false;
					for(int l=0; l<k;l++) {
						if(array[l] == newvalue) {
							included = true;
						}
					}
					if(included == false) {
						array[k] = newvalue;
						k++;
					}
				}
			}
			
			for(int l=0; l<array.length; l++) {
				valid[i][array[l]] = 1;
			}
			
			
			for(int j=0; j<loc_num; j++) {
				if(valid[i][j] == 1) {
					trip[i][j] = Utality.nextDouble();
				}
			}
		}
	}
	
	
	private static void makeIdentical(int z1, int z2) {
//		if(Utality.nextDouble() <= 0.5) {
//			contact[z1] = contact[z2] + 0.01;
//		}else {
//			if(contact[z2] > 0.01) {
//				contact[z1] = contact[z2] - 0.01;
//			}
//			
//		}
//		
//		
//		for(int i=0; i<loc_num; i++) {
//			if(Utality.nextDouble() <= 0.5) {
//				trip[z1][i] = trip[z2][i] + 0.01;
//			}else {
//				if(trip[z2][i] > 0.01)
//					trip[z1][i] = trip[z2][i] - 0.01;
//			}
//			
//			if(Utality.nextDouble() <= 0.5) {
//				trip[i][z1] = trip[i][z2] + 0.01;
//			}else {
//				if(trip[i][z1] > 0.01)
//					trip[i][z1] = trip[i][z2] - 0.01;
//			}
//		}
		
		contact[z1] = contact[z2];
		
		for(int i=0; i<loc_num; i++) {	
			trip[z1][i] = trip[z2][i];
			trip[i][z1] = trip[i][z2];	
		}
		
		for(int i=0; i<loc_num; i++) {
			valid[z1][i] = valid[z2][i];
			valid[i][z1] = valid[i][z2];
		}
		
//		initS[z1] = initS[z2];
//		initI[z1] = initI[z2];
	}

	public static void parse(Model model) {
		init();
		
//		for(int i=0; i<9; i++) {
//			makeIdentical(i,i+1);
//		}
//		for(int i=10; i<19; i++) {
//			makeIdentical(i,i+1);
//		}
//		for(int i=20; i<28; i++) {
//			makeIdentical(i,i+1);
//		}
		
//		makeIdentical(0,1);
//		makeIdentical(10,11);
		
		ArrayList<String> species = new ArrayList<String>();
		ArrayList<Trans> transArray = new ArrayList<Trans>();
		
		
		for(int i=0; i<loc_num; i++) {
			species.add("S" + "("+ i+ ")");
			species.add("I" + "("+ i+ ")");
//			species.add("R" + "("+ i+ ")");
			transArray.addAll(makeTransForS(i));
			transArray.addAll(makeTransForI(i));
//			transArray.addAll(makeTransForR(i));
		}
		
		for(Trans trans: transArray) {
			System.out.println(trans.toString());
		}
		
		HashMap<String, Integer> agentMap = new HashMap<String, Integer>();
		for(int i=0; i<loc_num; i++) {
			agentMap.put("S" + "("+ i+ ")", initS[i]);
			agentMap.put("I" + "("+ i+ ")", initI[i]);
//			agentMap.put("R" + "("+ i+ ")", initR[i]);
		}
		
		for(String key: agentMap.keySet()) {
			System.out.println(key + ": " + agentMap.get(key));
		}
		
		model.init(transArray, agentMap);
		
	}
	
	public static ArrayList<Trans> makeTransForS(int loc) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
		for(int dest =0; dest<loc_num; dest++) {
			if(valid[loc][dest] == 1) {
				ArrayList<RPItem> reactants = new ArrayList<RPItem>();
				ArrayList<RPItem> products = new ArrayList<RPItem>();
				ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
				
				RPItem reactItem = new RPItem("S" + "("+ loc+ ")", 1);
				reactants.add(reactItem);
				
				RateItem rateItem = new RateItem("S" + "("+ loc+ ")", 1);
				rateItems.add(rateItem);
				
				RPItem productItem = new RPItem("S" + "("+ dest+ ")", 1);
				products.add(productItem);
				
				Trans trans = new Trans(reactants, products,
						trip[loc][dest],rateItems);
				ret.add(trans);
			}
		}
		
		ArrayList<RPItem> reactants = new ArrayList<RPItem>();
		ArrayList<RPItem> products = new ArrayList<RPItem>();
		ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
		
		RPItem reactItem = new RPItem("S" + "("+ loc+ ")", 1);
		reactants.add(reactItem);
		
		RateItem rateItem = new RateItem("S" + "("+ loc+ ")", 1);
		rateItems.add(rateItem);
		RateItem rateItem2 = new RateItem("I" + "("+ loc+ ")", 1);
		rateItems.add(rateItem2);
		
		RPItem productItem = new RPItem("I" + "("+ loc+ ")", 1);
		products.add(productItem);
		
		Trans trans = new Trans(reactants, products,
				contact[loc],rateItems);
		ret.add(trans);
		
		return ret;
	}
	
	public static ArrayList<Trans> makeTransForI(int loc) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
		for(int dest =0; dest<loc_num; dest++) {
			if(valid[loc][dest] == 1) {
				ArrayList<RPItem> reactants = new ArrayList<RPItem>();
				ArrayList<RPItem> products = new ArrayList<RPItem>();
				ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
				
				RPItem reactItem = new RPItem("I" + "("+ loc+ ")", 1);
				reactants.add(reactItem);
				
				RateItem rateItem = new RateItem("I" + "("+ loc+ ")", 1);
				rateItems.add(rateItem);
				
				RPItem productItem = new RPItem("I" + "("+ dest+ ")", 1);
				products.add(productItem);
				
				Trans trans = new Trans(reactants, products,
						trip[loc][dest],rateItems);
				ret.add(trans);
			}
		}
		
		ArrayList<RPItem> reactants = new ArrayList<RPItem>();
		ArrayList<RPItem> products = new ArrayList<RPItem>();
		ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
		
		RPItem reactItem = new RPItem("I" + "("+ loc+ ")", 1);
		reactants.add(reactItem);
		
		RateItem rateItem = new RateItem("I" + "("+ loc+ ")", 1);
		rateItems.add(rateItem);;
		
		RPItem productItem = new RPItem("S" + "("+ loc+ ")", 1);
		products.add(productItem);
		
		Trans trans = new Trans(reactants, products,
				recover,rateItems);
		ret.add(trans);
		
		return ret;
	}
	
	public static ArrayList<Trans> makeTransForR(int loc) {
		ArrayList<Trans> ret = new ArrayList<Trans>();
		
		for(int dest =0; dest<loc_num; dest++) {
			if(valid[loc][dest] == 1) {
				ArrayList<RPItem> reactants = new ArrayList<RPItem>();
				ArrayList<RPItem> products = new ArrayList<RPItem>();
				ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
				
				RPItem reactItem = new RPItem("R" + "("+ loc+ ")", 1);
				reactants.add(reactItem);
				
				RateItem rateItem = new RateItem("R" + "("+ loc+ ")", 1);
				rateItems.add(rateItem);
				
				RPItem productItem = new RPItem("R" + "("+ dest+ ")", 1);
				products.add(productItem);
				
				Trans trans = new Trans(reactants, products,
						trip[loc][dest],rateItems);
				ret.add(trans);
			}
		}
		
		ArrayList<RPItem> reactants = new ArrayList<RPItem>();
		ArrayList<RPItem> products = new ArrayList<RPItem>();
		ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
		
		RPItem reactItem = new RPItem("R" + "("+ loc+ ")", 1);
		reactants.add(reactItem);
		
		RateItem rateItem = new RateItem("R" + "("+ loc+ ")", 1);
		rateItems.add(rateItem);;
		
		RPItem productItem = new RPItem("S" + "("+ loc+ ")", 1);
		products.add(productItem);
		
		Trans trans = new Trans(reactants, products,
				recover,rateItems);
		ret.add(trans);
		
		return ret;
	}

}
