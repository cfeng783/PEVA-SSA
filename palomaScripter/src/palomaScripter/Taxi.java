package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import factory.ActionFactory;
import topology.Loc;
import topology.TopologyFactory;
import topology.TreeTopology;


public class Taxi {
	final static int LocNum = 25;

	
	final static String act_revoke = "revoke";
	final static String act_move = "move";
	final static String act_deliver= "deliver";
	
	
	final static double sigma = 0.001;
	static DecimalFormat df = new DecimalFormat("##.000");
	static Random rnd = new Random();
	
	static double[] revokeRate = new double[LocNum];
	static double[][] probDist = new double[LocNum][LocNum];
	static double callRate = 1;
	static double takeRate = 1;
	static double[][] moveRate = new double[LocNum][LocNum];
	static double[][] deliverRate = new double[LocNum][LocNum];
	
	static void init(ArrayList<Loc> locs) {
		
		rnd.setSeed(System.currentTimeMillis());
		
		for(int i=0; i<revokeRate.length; i++) {
			revokeRate[i] = rand(1);
		}
		
		for(int i=0;i<LocNum;i++) {
			for(int j=0; j<LocNum; j++) {
				moveRate[i][j] = rand(0.1);
				deliverRate[i][j] = rand(0.1);
				if(locs.get(i).isConnected(locs.get(j))) {
					probDist[i][j] = rand(2.0/LocNum)+0.001;
				}else {
					probDist[i][j] = rand(0.02/LocNum);
				}
				
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		ArrayList<Loc> locs = TopologyFactory.makeTopology(TopologyFactory.GRID, LocNum);
		for(Loc loc: locs) {
			loc.print();
		}
		
		init(locs);
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tempt.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Documents/backup/PSimPAL_fixedsampling/PALOMA models/taxi")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//user idle
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						String userIdle = getUserIdle(loc) + ":= ";
						boolean first = true;
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							if(first) {
								userIdle += ActionFactory.genSpNoMsgAction(act_revoke, revokeRate[i]*probDist[i][j]+"", getUserCall(loc, dest));
								first = false;
							}else {
								userIdle += "+" + ActionFactory.genSpNoMsgAction(act_revoke, revokeRate[i]*probDist[i][j]+"", getUserCall(loc, dest));
							}
							
						}
						
						userIdle += ";";
						pw.println(userIdle);
						pw.println();
					}
					
					//user call
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							
							String userCall = getUserCall(loc,dest) + ":=";
							
							userCall += ActionFactory.genSpUniAction(getCallMsg(dest), callRate+"", "local", getUserWait(loc,dest));
							userCall += ";";
							pw.println(userCall);
							pw.println();
						}
					}
					
					//user wait
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							
							String userWait = getUserWait(loc,dest) + ":=";
							
							userWait += ActionFactory.genIndUniAction(getTakeMsg(dest), "1", "1", getUserIdle(loc));
							userWait += ";";
							pw.println(userWait);
							pw.println();
						}
					}
					
					//taxi free
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						String taxiFree = getTaxiFree(loc) + ":= ";
						boolean first = true;
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							if(first) {
								taxiFree += ActionFactory.genIndUniAction(getCallMsg(dest), 1+"", 1+"", getTaxiBooked(loc,dest));
								first = false;
							}else {
								taxiFree += "+" + ActionFactory.genIndUniAction(getCallMsg(dest), 1+"", 1+"", getTaxiBooked(loc,dest));
							}
							
							if(loc.isConnected(dest)) {
								taxiFree += "+" + ActionFactory.genSpNoMsgAction(act_move, moveRate[i][j]+"", getTaxiFree(dest));
							}
						}
						
						taxiFree += ";";
						pw.println(taxiFree);
						pw.println();
					}
					
					
					//taxi booked
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							
							String taxiBooked = getTaxiBooked(loc, dest) + ":=";
							
							taxiBooked += ActionFactory.genSpUniAction(getTakeMsg(dest), takeRate+"", "local", getTaxiOccupied(loc, dest));
							taxiBooked += ";";
							pw.println(taxiBooked);
							pw.println();
						}
					}
					
					//taxi occupied
					for(int i=0; i<LocNum; i++) {
						Loc loc = locs.get(i);
						for(int j=0; j<LocNum; j++) {
							if(i == j) {
								continue;
							}
							Loc dest = locs.get(j);
							
							String taxiOccupied = getTaxiOccupied(loc, dest) + ":=";
							
							taxiOccupied += ActionFactory.genSpNoMsgAction(act_deliver, deliverRate[i][j]+"", getTaxiFree(dest));
							taxiOccupied += ";";
							pw.println(taxiOccupied);
							pw.println();
						}
					}
					
				}
					
					
				if(data.equalsIgnoreCase("#agents")){
					String Agents = "";
					for(int i=0; i<LocNum; i++) {
						//String Biker = "Biker" + comCod(i) + "[10]";
						Agents += getUserIdle(locs.get(i)) + "[" + 60 + "] || ";
//						Agents += getTaxiFree(locs.get(i)) + "[" + 10 + "] || ";

						if(i==LocNum-1) {
							Agents += getTaxiFree(locs.get(i)) + "[" + 40 + "]";
						}else {
							Agents += getTaxiFree(locs.get(i)) + "[" + 40 + "] || ";
						}
						
					}
					pw.println(Agents);
				}
			}
			br.close();
			pw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	static String getUserIdle(Loc loc) {
		return "UserIdle" + loc.toString();
	}
	
	static String getUserCall(Loc loc, Loc dest) {
		return "UserCall" + makeSubscript(dest.x, dest.y) + loc.toString();
	}
	
	static String getUserWait(Loc loc, Loc dest) {
		return "UserWait"  + makeSubscript(dest.x, dest.y) + loc.toString();
	}
	
	static String getTaxiFree(Loc loc) {
		return "TaxiFree" + loc.toString();
	}
	
	static String getTaxiBooked(Loc loc, Loc dest) {
		return "TaxiBooked" + makeSubscript(dest.x, dest.y) + loc.toString();
	}
	
	static String getTaxiOccupied(Loc loc, Loc dest) {
		return "TaxiOccupied" + makeSubscript(dest.x, dest.y) + loc.toString();
	}
	
	static String getCallMsg(Loc dest) {
		return "call" + makeSubscript(dest.x, dest.y);
	}
	
	static String getTakeMsg(Loc dest) {
		return "take" + makeSubscript(dest.x, dest.y); 
	}
	
	static String makeSubscript(int i, int j) {
		return "_" + i + "_" + j;
	}
	
	static String makeSubscript(int i) {
		return "_" + i;
	}
	
	public static double rand(double max) {
		double ret = rnd.nextDouble() * max;
		ret = Double.parseDouble(df.format(ret));
		return ret;
	}
	
	public static int randInt(int max) {
		return rnd.nextInt(max)+1;
	}

}
