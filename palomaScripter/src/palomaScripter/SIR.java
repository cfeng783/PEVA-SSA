package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;


public class SIR {
	final static int LocationNum = 100;
	final static int MatrixSize = 10;
	
	final static boolean initFromFile = false;
	
	final static String msg_contact = "contact";
	
	final static String act_move = "move";
	final static String act_recover = "recover";
	
	final static String st_s = "S";
	final static String st_i = "I";
	final static String st_r = "R";
	
	final static String eps = "eps";
	static DecimalFormat df = new DecimalFormat("##.00");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		
		//int matrix_size = Math.sqrt(StationNum);
		
		double p = 0.5;
		double rep = 0.1;
		double[][] q = new double[LocationNum][LocationNum];
		double beta[] = new double[LocationNum];
		double gamma = 0.2;
			
		int[] sNum = new int[LocationNum];
		int[] iNum = new int[LocationNum]; 
		int[] rNum = new int[LocationNum];
		
		
		
		//initialize random varibales
		try {
			if(!initFromFile) {
				PrintWriter fw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("randomVariablesSIR.txt")),true);
				
				for(int i=0; i<LocationNum; i++) {
					double r1 = rnd.nextDouble()*0.03+0.01;
					r1 = Double.parseDouble(df.format(r1));
					beta[i] = r1;
					fw.println(beta[i]);
					
					
					for(int j=0; j<LocationNum; j++) {
						loc li = getLoc(i);
						loc lj = getLoc(j);
						
						double rate1 = rnd.nextDouble()*0.03+0.01;
						q[i][j] = Double.parseDouble(df.format(rate1));
						
					}
				}
				
				for(int i=0; i<LocationNum; i++) {
					sNum[i] = rnd.nextInt(100)+100;
					iNum[i] = rnd.nextInt(4)+10;
					rNum[i] = rnd.nextInt(5)+10;
					fw.println(sNum[i]);
					fw.println(iNum[i]);
					fw.println(rNum[i]);
				}
				
				fw.close();
			}else {
				BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream("randomVariablesSIR.txt")));
				
				for(int i=0; i<LocationNum; i++) {
					beta[i] = Double.parseDouble(fr.readLine());
					
					for(int j=0; j<LocationNum; j++) {
						q[i][j] = Double.parseDouble(fr.readLine());
						
					}
				}
				
				for(int i=0; i<LocationNum; i++) {
					sNum[i] = Integer.parseInt(fr.readLine());
					iNum[i] = Integer.parseInt(fr.readLine());
					rNum[i] = Integer.parseInt(fr.readLine());
				}
				
				fr.close();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("absSIRscript.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPAL/PALOMA models/SIR")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//susceptible 
					for(int i=0; i<LocationNum; i++) {
						String susceptible = st_s + comCod(i) + ":= ?("
								+ msg_contact + "," + p + ")@Pr{1/(|" + st_s + comCod(i) + "|+|" + st_i + comCod(i) + "|+|"
								//		+ st_r + comCod(i) + "|+0.0001)}." + st_i + comCod(i);
								+ st_r + comCod(i) + "|)}." + st_i + comCod(i);
//								+ msg_contact + "," + p + ")@Pr{1/100}." + st_i + comCod(i);
						for(int j=0; j<LocationNum; j++) {
							if(isNearby(i,j)) {
								susceptible += "+(" + act_move + makeSubscript(i,j) + "," + q[i][j] + ")." 
										+ st_s + comCod(j);  
							}
						}
						susceptible += ";";
						pw.println(susceptible);
					}
					
					pw.println();
					
					//infected 
					for(int i=0; i<LocationNum; i++) {
						String infected = st_i + comCod(i) + ":= !("
								+ msg_contact + "," + beta[i];
						infected += ")@IR{range(" + rnd.nextDouble()*3 + ")}." + st_i + comCod(i);
						infected += "+(" + act_recover + "," + gamma + ")." + st_r + comCod(i); 
						for(int j=0; j<LocationNum; j++) {
							if(isNearby(i,j)) {
								infected += "+(" + act_move + makeSubscript(i,j) + "," + q[i][j] + ")." 
										+ st_i + comCod(j);  
							}
						}
						infected += ";";
						pw.println(infected);
					}
					
					pw.println();
					
					//recovered 
					for(int i=0; i<LocationNum; i++) {
						String recoverd = st_r + comCod(i) + ":= ";
						boolean isFirst = true;
						for(int j=0; j<LocationNum; j++) {
							if(isNearby(i,j)) {
								if(isFirst) {
									recoverd += "(" + act_move + makeSubscript(i,j) + "," + q[i][j] + ")." 
											+ st_r + comCod(j); 
									isFirst = false;
								}else {
									recoverd += "+(" + act_move + makeSubscript(i,j) + "," + q[i][j] + ")." 
											+ st_r + comCod(j);  
								}
								
							}
						}
						recoverd += "+(" + "reset" + "," + rep + ")." + st_s + comCod(i);
						recoverd += ";";
						pw.println(recoverd);
					}
					
					pw.println();
					
					
					
				}
				if(data.equalsIgnoreCase("#agents")){
					for(int i=0; i<LocationNum; i++) {
						//String Biker = "Biker" + comCod(i) + "[10]";
						String Agents = st_s + comCod(i) + "[" + sNum[i]+ "] || ";
						Agents += st_i + comCod(i) + "[" + iNum[i] + "] || ";
						if(i==LocationNum-1) {
							Agents += st_r + comCod(i) + "[" + rNum[i] + "]";
						}else {
							Agents += st_r + comCod(i) + "[" + rNum[i] + "] || ";
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
