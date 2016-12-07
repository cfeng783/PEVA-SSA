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

import topology.Loc;
import topology.TopologyFactory;
import topology.TreeTopology;


public class BikeSchema {
	final static int StationNum = 9;
	
	final static boolean largeScript = false;
	final static boolean initFromFile = true;
	
	final static String msg_slotAvailable = "SlotAvailable";
	final static String msg_bikeAvailable = "BikeAvailable";
	final static String msg_return = "Return";
	final static String msg_borrow = "Borrow";
	
	final static String act_seekBike = "seekBike";
	final static String act_walk = "walk";
	final static String act_walk2station = "W2S";
	final static String act_seekSlot ="seekSlot";
	final static String act_ride = "ride";
	final static String act_ride2station = "R2S";
	
	final static String st_slot = "Slot";
	final static String st_bike = "Bike";
	final static String st_station = "Station";
	final static String st_pedestrian = "Pedestrian";
	final static String st_seekbike = "SeekBike";
	final static String st_walk2station = "Walk2Station";
	final static String st_checkbikenum = "CheckBikeNum";
	final static String st_borrowbike = "BorrowBike";
	final static String st_biker = "Biker";
	final static String st_seekslot = "SeekSlot";
	final static String st_ride2station = "Ride2Station";
	final static String st_checkslotnum = "CheckSlotNum";
	final static String st_returnbike = "ReturnBike";
	
	
	final static double sigma = 0.001;
	static DecimalFormat df = new DecimalFormat("##.000");
	
	final static int range = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		ArrayList<Loc> locs = TopologyFactory.makeTopology(TopologyFactory.GRID, StationNum);
		for(Loc loc: locs) {
			loc.print();
		}
		
		
		double gamma = 10;
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tempt.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPal_2015epew/PALOMA models/biketest")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//station 
					for(int i=0; i<StationNum; i++) {
						Loc loc = locs.get(i);
						String Station = st_station + loc.toString() + ":= !("
								+ msg_bikeAvailable + makeSubscript(i) + "," + gamma + ")@IR{" + getInflRange(loc, range)
										+ "}." + st_station + loc.toString()
								+ "+ !(" + msg_slotAvailable + makeSubscript(i) + "," + gamma + ")@IR{" + getInflRange(loc, range)
										+ "}." + st_station + loc.toString() + ";";
						pw.println(Station);
					}
					
					pw.println();
					
					//Bike and Slot
					for(int i=0; i<StationNum; i++) {
						Loc loc = locs.get(i);
						String Slot = st_slot + loc.toString() + ":= ??("
								+ msg_return + "," + 1 + ")@Wt{1}." + st_bike + loc.toString() + ";";
						String Bike = st_bike + loc.toString() + ":= ??("
								+ msg_borrow + "," + 1 + ")@Wt{1}." + st_slot + loc.toString() + ";";
						pw.println(Slot);
						pw.println(Bike);
					}
					
					pw.println();
					
					//bike user
					for(int i=0; i<StationNum; i++) {
						Loc loc = locs.get(i);
						String Pedestrian = st_pedestrian + loc.toString() + ":=("
								+ act_seekBike + "," + rand(0.04) + ")." + st_seekbike + loc.toString();
						for(int j=0; j<loc.getConnectedLocs().size(); j++) {
							Pedestrian += "+(" + act_walk + ",rw/" + loc.getNeighbours(1).size() + ")." 
										+ st_pedestrian + loc.getConnectedLocs().get(j).toString();  
						}
						Pedestrian += ";";
						pw.println(Pedestrian);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						Loc loc = locs.get(i);
						String SeekBike = st_seekbike + loc.toString() + ":=";
						for(int j=0; j<StationNum; j++) {
							if(loc.isConnected(locs.get(j)) || i==j) {
//							if(i==j) {
								if(!SeekBike.endsWith(":=")) {
									SeekBike += "+";
								}
								SeekBike += "?(" + msg_bikeAvailable + makeSubscript(j) + "," + 1 + ")"; 
								SeekBike += "@Pr{|" + st_bike + locs.get(j).toString() + "|/(" +
										"|" + st_bike + locs.get(j).toString() + "|+" + "|" +
											st_slot + locs.get(j).toString() + "|" + ") " + "}." + st_walk2station + makeSubscript(j) + loc.toString();
							}
							 

						}
						SeekBike += ";";
						pw.println(SeekBike);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						for(int j=0; j<StationNum; j++) {
							if(locs.get(i).isConnected(locs.get(j)) || i==j) {
//							if(i==j) {
								String Walk2Station = st_walk2station + makeSubscript(j) + locs.get(i).toString() + ":="
										+ "(" + act_walk2station + "," + rand(0.02) + ")"
										+ "." + st_borrowbike + locs.get(j).toString(); 
								Walk2Station += ";";
								pw.println(Walk2Station);
							}
						}
					}
					pw.println();
					
					
					for(int i=0; i<StationNum; i++) {
						String BorrowBike = st_borrowbike + locs.get(i).toString() + ":="
								+ "!!(" + msg_borrow + ","  + "o)@IR{local}."
								+ st_biker + locs.get(i).toString() + ";";
						pw.println(BorrowBike);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String Biker = st_biker + locs.get(i).toString() + ":=("
								+ act_seekSlot + "," + rand(0.05) + ")." + st_seekslot + locs.get(i).toString();
						for(int j=0; j<locs.get(i).getConnectedLocs().size(); j++) {
							Biker += "+(" + act_ride + ",rb/" + locs.get(i).getConnectedLocs().size() + ")." 
										+ st_biker + locs.get(i).getConnectedLocs().get(j).toString();  
						}
						Biker += ";";
						pw.println(Biker);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String SeekSlot = st_seekslot + locs.get(i).toString() + ":=";
						for(int j=0; j<StationNum; j++) {
							if(locs.get(i).isConnected(locs.get(j)) || i==j) {
//							if(i==j) {
								if(!SeekSlot.endsWith(":=")) {
									SeekSlot += "+";
								}
								SeekSlot += "?(" + msg_slotAvailable + makeSubscript(j) + "," + 1 + ")";
								SeekSlot += "@Pr{|" + st_slot + locs.get(j).toString() + "|/(" +
										"|" + st_bike + locs.get(j).toString() + "|+" + "|" +
											st_slot + locs.get(j).toString() + "|" + ") }." + st_ride2station + makeSubscript(j) + locs.get(i).toString(); 
							}
						}
						SeekSlot += ";";
						pw.println(SeekSlot);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						for(int j=0; j<StationNum; j++) {
							if(locs.get(i).isConnected(locs.get(j)) || i==j) {
//							if(i==j) {
								String Ride2Station = st_ride2station + makeSubscript(j) + locs.get(i).toString() + ":="
											+ "(" + act_ride2station + "," + rand(0.04) + ")"
											+ "." + st_returnbike + locs.get(j).toString(); 
								Ride2Station += ";";
								pw.println(Ride2Station);
							}
						}
					}
					pw.println();
					
					
					for(int i=0; i<StationNum; i++) {
						String ReturnBike = st_returnbike+ locs.get(i).toString() + ":="
								+ "!!(" + msg_return + ","  + "o)@IR{local}."
								+ st_pedestrian + locs.get(i).toString() + ";";
						pw.println(ReturnBike);
					}
					pw.println();
					
					
				}
				if(data.equalsIgnoreCase("#agents")){
					for(int i=0; i<StationNum; i++) {
						//String Biker = "Biker" + comCod(i) + "[10]";
						String Agents = "Pedestrian" + locs.get(i).toString() + "[" + 30 + "] || ";
						Agents += "Biker" + locs.get(i).toString() + "[" + 30 + "] || ";
						Agents += "Slot" + locs.get(i).toString() + "[" + 15 + "] || ";
						Agents += "Station" + locs.get(i).toString() + " || ";
						
						if(i==StationNum-1) {
							Agents += "Bike" + locs.get(i).toString() + "[" + 15 + "]";
						}else {
							Agents += "Bike" + locs.get(i).toString() + "[" + 15 + "] || ";
						}
						pw.println(Agents);
					}
				}
			}
			br.close();
			pw.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	static String getInflRange(Loc loc, int range) {
//		if(range == 1) {
//			ArrayList<Loc> locList = loc.getNeighbours(range);
//			String str = loc.toString();
//			for(int i=0; i<locList.size(); i++) {
////				if(i==0) {
////					str += locList.get(i).toString();
////				}else {
//					str += ", " +locList.get(i).toString();
//				//}
//			}
//			return str;
//		}else {
//			return loc.toString();
//		}
		return "range(d)";
	}
	
	
	static String makeSubscript(int i, int j) {
		return "_" + i + "_" + j;
	}
	
	static String makeSubscript(int i) {
		return "_" + i;
	}
	
	public static double rand(double max) {
		Random rnd = new Random();
		double ret = rnd.nextDouble() * max + 0.001;
		ret = Double.parseDouble(df.format(ret));
		return ret;
	}
	
	public static int randInt(int max) {
		Random rnd = new Random();
		return rnd.nextInt(max)+1;
	}

}
