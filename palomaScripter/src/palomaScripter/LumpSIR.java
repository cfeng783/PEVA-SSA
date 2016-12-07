package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;


public class LumpSIR {
	final static int patchNum = 4;
	final static int locationNum = 10;
	
	final static boolean initFromFile = false;
	
	final static String msg_contact = "contact";
	
	final static String act_move = "move";
	final static String act_recover = "recover";
	final static String act_reset = "reset";
	
	final static String st_s = "S";
	final static String st_i = "I";
	final static String st_r = "R";
	
	final static String eps = "eps";
	static DecimalFormat df = new DecimalFormat("##.00");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		double p = 0.5;
		double beta = 0.1;
		double gamma = 0.2;
		double r = 0.3;
		double alpha = 0.1;
			
		int sNum = 500;
		int iNum = 10; 
		int rNum = 500;
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("lumpSirScripter")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPAL/PALOMA models/lumpSIR")),true);  
			//PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("lumpSIR")),true);  
			
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//susceptible 
					for(int i=0; i<patchNum; i++) {
						for(int j=0; j<locationNum; j++) {
							String susceptible = st_s + comCod(i,j) + ":= ?("
									+ msg_contact + "," + p + ")@Pr{1/(|" + st_s + comCod(i,j) + "|+|" + st_i + comCod(i,j) + "|+|"
									//		+ st_r + comCod(i) + "|+0.0001)}." + st_i + comCod(i);
									+ st_r + comCod(i,j) + "|)}." + st_i + comCod(i,j);
							for(int k=0; k<locationNum; k++) {
								if(k != j) {
									susceptible += "+(" + act_move + "," + r + ")." 
											+ st_s + comCod(i,k);
								}	
							}
							susceptible += "+(" + act_move + "," + r + ")." 
									+ st_s + comCod(i,locationNum);
							susceptible += ";";
							pw.println(susceptible);
						}
						pw.println();
					}
					pw.println();
					
					//infected
					for(int i=0; i<patchNum; i++) {
						for(int j=0; j<locationNum; j++) {
							String infected = st_i + comCod(i,j) + ":= !("+ msg_contact + "," + beta;
							infected += ")@IR{local}." + st_i + comCod(i,j);
							infected += "+(" + act_recover + "," + gamma + ")." + st_r + comCod(i,j); 
							for(int k=0; k<locationNum; k++) {
								if(k != j) {
									infected += "+(" + act_move + "," + r + ")." 
											+ st_i + comCod(i,k);
								}	
							}
							infected += "+(" + act_move + "," + r + ")." 
									+ st_i + comCod(i,locationNum);
							infected += ";";
							pw.println(infected);
						}
						pw.println();
					}
					pw.println();
					
					//recovered
					for(int i=0; i<patchNum; i++) {
						for(int j=0; j<locationNum; j++) {
							String recoverd = st_r + comCod(i,j) + ":= (" + act_reset + "," + alpha + ")." + st_s + comCod(i,j);
							for(int k=0; k<locationNum; k++) {
								if(k != j) {
									recoverd += "+(" + act_move + "," + r + ")." 
											+ st_r + comCod(i,k);
								}	
							}
							recoverd += "+(" + act_move + "," + r + ")." 
									+ st_i + comCod(i,locationNum);
							recoverd += ";";
							pw.println(recoverd);
						}
						pw.println();
					}
					pw.println();
					
					
					//connected locations
					for(int i=0; i<patchNum; i++) {
						String susceptible = st_s + comCod(i,locationNum) + ":= ?("
								+ msg_contact + "," + p + ")@Pr{1/(|" + st_s + comCod(i,locationNum) + "|+|" + st_i + comCod(i,locationNum) + "|+|"
								//		+ st_r + comCod(i) + "|+0.0001)}." + st_i + comCod(i);
								+ st_r + comCod(i,locationNum) + "|)}." + st_i + comCod(i,locationNum);
						for(int k=0; k<locationNum; k++) {
							susceptible += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(i,k);
						}
						
						for(int j=0; j<patchNum; j++) {
							if(j != i) {
								susceptible += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(j,locationNum);
							}
						}
						susceptible += ";";
						pw.println(susceptible);
						pw.println();
					}
					
					for(int i=0; i<patchNum; i++) {
						String infected = st_i + comCod(i,locationNum) + ":= !("+ msg_contact + "," + beta;
						infected += ")@IR{local}." + st_i + comCod(i,locationNum);
						infected += "+(" + act_recover + "," + gamma + ")." + st_r + comCod(i,locationNum); 
						for(int k=0; k<locationNum; k++) {
							infected += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(i,k);
						}
						
						for(int j=0; j<patchNum; j++) {
							if(j != i) {
								infected += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(j,locationNum);
							}
						}
						infected += ";";
						pw.println(infected);
						pw.println();
					}
					
					for(int i=0; i<patchNum; i++) {
						String recoverd = st_r + comCod(i,locationNum) + ":= (" + act_reset + "," + alpha + ")." + st_s + comCod(i,locationNum);
						for(int k=0; k<locationNum; k++) {
							recoverd += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(i,k);
						}
						
						for(int j=0; j<patchNum; j++) {
							if(j != i) {
								recoverd += "+(" + act_move + "," + r + ")." 
										+ st_s + comCod(j,locationNum);
							}
						}
						recoverd += ";";
						pw.println(recoverd);
						pw.println();
					}
					
				}
				if(data.equalsIgnoreCase("#agents")){
					for(int i=0; i<patchNum; i++) {
						String Agents = "";
						for(int j=0; j<=locationNum; j++) {
							Agents += st_s + comCod(i,j) + "[" + sNum+ "] || ";
							Agents += st_i + comCod(i,j) + "[" + iNum + "] || ";
							Agents += st_r + comCod(i,j) + "[" + rNum + "] || ";
						}
						if(i == patchNum-1) {
							Agents = Agents.substring(0, Agents.length()-3);
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
	
	static String comCod(int i, int j) {
		return "(" + i + "," + j + ")";
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

