package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;


public class bikeSharing {
	final static int StationNum = 9;
	final static int MatrixSize = 3;
	
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
	static DecimalFormat df = new DecimalFormat("##.00");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		
		//int matrix_size = Math.sqrt(StationNum);
		
		double gamma = 10;
		double b[] = new double[StationNum]; //seek bike rate
		
		double r[] = new double[StationNum]; // return bike rate
		
		double[][] w2s = new double[StationNum][StationNum];
		double[][] r2s = new double[StationNum][StationNum];
		
		double[][] Qp = new double[StationNum][StationNum]; //walk to other zone rate
		
		double[][] Qb = new double[StationNum][StationNum]; //bike to other zone rate 
		
		int[] pedestrianNum = new int[StationNum];
		int[] slotNum = new int[StationNum]; 
		int[] bikeNum = new int[StationNum];
		
		
		
		//initialize random varibales
		try {
			if(!initFromFile) {
				PrintWriter fw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("randomVariables.txt")),true);
				
				for(int i=0; i<StationNum; i++) {
					double r1 = rnd.nextDouble()+0.1;
					r1 = Double.parseDouble(df.format(r1));
					b[i] = r1;
					fw.println(b[i]);
					
					double r2 = rnd.nextDouble()+0.1;
					r2 = Double.parseDouble(df.format(r2));
					r[i] = r2;
					fw.println(r[i]);
					
					for(int j=0; j<StationNum; j++) {
						loc li = getLoc(i);
						loc lj = getLoc(j);
						
						double d_ij = getDist(li,lj);
						
						double rate1 = 0.05/(d_ij+1);
						rate1 = Double.parseDouble(df.format(rate1)); 
						w2s[i][j] = rate1;
						fw.println(w2s[i][j]);
						
						double rate2 = 0.1/(d_ij+1);
						rate2 = Double.parseDouble(df.format(rate2)); 
						r2s[i][j] = rate2;
						fw.println(r2s[i][j]);
						
						rate1 = rnd.nextDouble()+0.1;
						rate1 = Double.parseDouble(df.format(rate1));
						Qp[i][j] = rate1;
						fw.println(Qp[i][j]);
						
						rate2 = rnd.nextDouble()+0.5;
						rate2 = Double.parseDouble(df.format(rate2));
						Qb[i][j] = rate2;
						fw.println(Qb[i][j]);
					}
				}
				
				if(!largeScript) {
					for(int i=0; i<StationNum; i++) {
						pedestrianNum[i] = rnd.nextInt(50)+30;
						slotNum[i] = rnd.nextInt(15)+1;
						bikeNum[i] = 40-slotNum[i];
						fw.println(pedestrianNum[i]);
						fw.println(slotNum[i]);
						fw.println(bikeNum[i]);
					}
				}else {
					for(int i=0; i<StationNum; i++) {
						pedestrianNum[i] = rnd.nextInt(500)+500;
						slotNum[i] = rnd.nextInt(150)+1;
						bikeNum[i] = 400-slotNum[i];
						fw.println(pedestrianNum[i]);
						fw.println(slotNum[i]);
						fw.println(bikeNum[i]);
					}
				}
				fw.close();
			}else {
				BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream("randomVariables.txt")));
				for(int i=0; i<StationNum; i++) {
					b[i] = Double.parseDouble(fr.readLine());
					r[i] = Double.parseDouble(fr.readLine());
					
					for(int j=0; j<StationNum; j++) {			
						w2s[i][j] = Double.parseDouble(fr.readLine());
						r2s[i][j] = Double.parseDouble(fr.readLine());						
						Qp[i][j] = Double.parseDouble(fr.readLine());
						Qb[i][j] = Double.parseDouble(fr.readLine());
					}
				}
				
				for(int i=0; i<StationNum; i++) {
					pedestrianNum[i] = Integer.parseInt(fr.readLine());
					slotNum[i] = Integer.parseInt(fr.readLine());
					bikeNum[i] = Integer.parseInt(fr.readLine());
				}
				
				fr.close();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("tempt.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPAL/PALOMA models/bike")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//station 
					for(int i=0; i<StationNum; i++) {
						String Station = st_station + comCod(i) + ":= !("
								+ msg_bikeAvailable + makeSubscript(i) + "," + gamma + ")@IR{range(d)}." + st_station + comCod(i)
								+ "+ !(" + msg_slotAvailable + makeSubscript(i) + "," + gamma + ")@IR{range(d)}." + st_station + comCod(i) + ";";
						pw.println(Station);
					}
					
					pw.println();
					
					//Bike and Slot
					for(int i=0; i<StationNum; i++) {
						String Slot = st_slot + comCod(i) + ":= ??("
								+ msg_return + "," + 1 + ")@Wt{1}." + st_bike + comCod(i) + ";";
						String Bike = st_bike + comCod(i) + ":= ??("
								+ msg_borrow + "," + 1 + ")@Wt{1}." + st_slot + comCod(i) + ";";
						pw.println(Slot);
						pw.println(Bike);
					}
					
					pw.println();
					
					//bike user
					for(int i=0; i<StationNum; i++) {
						String Pedestrian = st_pedestrian + comCod(i) + ":=("
								+ act_seekBike + "," + b[i] + ")." + st_seekbike + comCod(i);
						for(int j=0; j<StationNum; j++) {
							if(isNearby(i,j)) {
								Pedestrian += "+(" + act_walk + makeSubscript(i,j) + "," + Qp[i][j] + ")." 
										+ st_pedestrian + comCod(j);  
							}
						}
						Pedestrian += ";";
						pw.println(Pedestrian);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String SeekBike = st_seekbike + comCod(i) + ":=";
						for(int j=0; j<StationNum; j++) {
							if(j!=0) {
								SeekBike += "+";
							}
							SeekBike += "?(" + msg_bikeAvailable + makeSubscript(j) + "," + 1 + ")";
//							double dist = getDist(getLoc(i), getLoc(j));
//							SeekBike += "@Pr{theta0 + theta1*(|" + st_bike + comCod(j) + "|/(" +
//								"|" + st_bike + comCod(j) + "|+" + "|" +
//									st_slot + comCod(j) + "|" + ")) " + "+theta2*" + dist + "/d"
//											+ "}." + st_walk2station + makeSubscript(j) + comCod(i); 
							SeekBike += "@Pr{|" + st_bike + comCod(j) + "|/(" +
									"|" + st_bike + comCod(j) + "|+" + "|" +
										st_slot + comCod(j) + "|" + ") " + "}." + st_walk2station + makeSubscript(j) + comCod(i); 

						}
						SeekBike += ";";
						pw.println(SeekBike);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						for(int j=0; j<StationNum; j++) {
							String Walk2Station = st_walk2station + makeSubscript(j) + comCod(i) + ":="
										+ "(" + act_walk2station + makeSubscript(i,j) + "," + w2s[i][j] + ")"
										+ "." + st_checkbikenum + comCod(j); 
							Walk2Station += ";";
							pw.println(Walk2Station);
						}
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String CheckBikeNum = st_checkbikenum + comCod(i) + ":="
								+ "?(" + msg_bikeAvailable + makeSubscript(i) + "," + 1 + ")@Pr{"
								+ "|" + st_bike + comCod(i) + "|/(|" + st_bike + comCod(i) + "|+" + sigma + ")}."
								+ st_borrowbike + comCod(i) + ";";
						pw.println(CheckBikeNum);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String BorrowBike = st_borrowbike+ comCod(i) + ":="
								+ "!!(" + msg_borrow + ","  + "o)@IR{local}."
								+ st_biker + comCod(i) + ";";
						pw.println(BorrowBike);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String Biker = st_biker + comCod(i) + ":=("
								+ act_seekSlot + "," + r[i] + ")." + st_seekslot + comCod(i);
						for(int j=0; j<StationNum; j++) {
							if(isNearby(i,j)) {
								Biker += "+(" + act_ride + makeSubscript(i,j) + "," + Qb[i][j] + ")." 
										+ st_biker + comCod(j);  
							}
						}
						Biker += ";";
						pw.println(Biker);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String SeekSlot = st_seekslot + comCod(i) + ":=";
						for(int j=0; j<StationNum; j++) {
							if(j!=0) {
								SeekSlot += "+";
							}
							SeekSlot += "?(" + msg_slotAvailable + makeSubscript(j) + "," + 1 + ")";
							SeekSlot += "@Pr{|" + st_slot + comCod(j) + "|/(" +
									"|" + st_bike + comCod(j) + "|+" + "|" +
										st_slot + comCod(j) + "|" + ") }." + st_ride2station + makeSubscript(j) + comCod(i); 
						}
						SeekSlot += ";";
						pw.println(SeekSlot);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						for(int j=0; j<StationNum; j++) {
							String Ride2Station = st_ride2station + makeSubscript(j) + comCod(i) + ":="
										+ "(" + act_ride2station + makeSubscript(i,j) + "," + r2s[i][j] + ")"
										+ "." + st_checkslotnum + comCod(j); 
							Ride2Station += ";";
							pw.println(Ride2Station);
						}
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String CheckSlotNum = st_checkslotnum + comCod(i) + ":="
								+ "?(" + msg_slotAvailable + makeSubscript(i) + "," + 1 + ")@Pr{"
								+ "|" + st_slot + comCod(i) + "|/(|" + st_slot + comCod(i) + "|+" + sigma + ")}."
								+ st_returnbike + comCod(i) + ";";
						pw.println(CheckSlotNum);
					}
					pw.println();
					
					for(int i=0; i<StationNum; i++) {
						String ReturnBike = st_returnbike+ comCod(i) + ":="
								+ "!!(" + msg_return + ","  + "o)@IR{local}."
								+ st_pedestrian + comCod(i) + ";";
						pw.println(ReturnBike);
					}
					pw.println();
					
					
				}
				if(data.equalsIgnoreCase("#agents")){
					for(int i=0; i<StationNum; i++) {
						//String Biker = "Biker" + comCod(i) + "[10]";
						String Agents = "Pedestrian" + comCod(i) + "[" + pedestrianNum[i]+ "] || ";
						Agents += "Slot" + comCod(i) + "[" + slotNum[i] + "] || ";
						Agents += "Station" + comCod(i) + " || ";
						if(i==StationNum-1) {
							Agents += "Bike" + comCod(i) + "[" + bikeNum[i] + "]";
						}else {
							Agents += "Bike" + comCod(i) + "[" + bikeNum[i] + "] || ";
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
	
	static String comCod(int i) {
		loc l = getLoc(i);
		return "(" + l.x + "," + l.y + ")";
	}
	
	static loc getLoc(int index) {
		int x = index / MatrixSize;
		int y = index % MatrixSize;
		loc ret = new loc(x,y);
		return ret;
	}
	
	static double getDist(loc l1, loc l2) {
		double ret = Math.sqrt(Math.pow(l1.x-l2.x, 2)+Math.pow(l1.y-l2.y, 2));
		return Double.parseDouble(df.format(ret));
	}
	
	static boolean isNearby(int i, int j) {
		if( getDist( getLoc(i), getLoc(j) ) == 1 ) {
			return true;
		}else {
			return false;
		}
	}
	
	static String makeSubscript(int i, int j) {
		return "_" + i + "_" + j;
	}
	
	static String makeSubscript(int i) {
		return "_" + i;
	}
	
	public static class loc {
		public int x;
		public int y;
		public loc(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

}
