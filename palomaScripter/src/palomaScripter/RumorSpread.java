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

public class RumorSpread {

	final static String msg_contact = "contact";
	
	final static String act_move = "move";
	final static String act_recover = "recover";
	
	final static String st_s = "S";
	final static String st_i = "I";
	final static String st_r = "R";
	
	static DecimalFormat df = new DecimalFormat("##.00");
	
	static double p = 0.5;
	static double rep = 0.1;
	static double gamma = 0.2;
	static double beta = 0.5;
	
	public static void main(String[] args) {
		ArrayList<Loc> locs = TopologyFactory.makeTopology(TopologyFactory.TREE, 10);
		for(Loc loc: locs) {
			loc.print();
		}
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("absSIRscript.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Documents/backup/PSimPAL_fixedsampling/PALOMA models/Rumor")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//susceptible 
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String susceptible = st_s + loc.toString() + ":= ?("
								+ msg_contact + "," + p + ")@Pr{1}." + st_i + loc.toString();
//								+ msg_contact + "," + p + ")@Pr{1/100}." + st_i + comCod(i);
						for(int j=0; j<loc.getConnectedLocs().size(); j++) {
								susceptible += "+(" + act_move + "," + rand(0.5) + ")." 
										+ st_s + loc.getConnectedLocs().get(j).toString();  
							
						}
						susceptible += ";";
						pw.println(susceptible);
					}
					
					pw.println();
					
					//infected 
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String infected = st_i + loc.toString() + ":= !("
								+ msg_contact + "," + beta;
						infected += ")@IR{local}." + st_i + loc.toString();
						infected += "+(" + act_recover + "," + gamma + ")." + st_r + loc.toString(); 
						for(int j=0; j<loc.getConnectedLocs().size(); j++) {
							infected += "+(" + act_move + "," + rand(0.2) + ")." 
									+ st_i + loc.getConnectedLocs().get(j).toString();
						}
						infected += ";";
						pw.println(infected);
					}
					
					pw.println();
					
					//recovered 
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String recoverd = st_r + loc.toString() + ":= ";
						boolean isFirst = true;
						for(int j=0; j<loc.getConnectedLocs().size(); j++) {
							if(isFirst) {
								recoverd += "(" + act_move + "," + rand(0.5) + ")." 
										+ st_r + loc.getConnectedLocs().get(j).toString();
								isFirst = false;
							}else {
								recoverd += "+(" + act_move + "," + rand(0.5) + ")." 
										+ st_r + loc.getConnectedLocs().get(j).toString();  
							}
						}
						recoverd += "+(" + "reset" + "," + rep + ")." + st_s + loc.toString();
						recoverd += ";";
						pw.println(recoverd);
					}
					
					pw.println();
					
					
					
				}
				if(data.equalsIgnoreCase("#agents")){
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						//String Biker = "Biker" + comCod(i) + "[10]";
						String Agents = st_s + loc.toString() + "[" + randInt(100) + "] || ";
						Agents += st_i + loc.toString() + "[" + randInt(10) + "] || ";
						if(i==locs.size()-1) {
							Agents += st_r + loc.toString() + "[" + 0 + "]";
						}else {
							Agents += st_r + loc.toString() + "[" + 0 + "] || ";
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
	
	public static double rand(double max) {
		Random rnd = new Random();
		double ret = rnd.nextDouble() * max;
		ret = Double.parseDouble(df.format(ret));
		return ret;
	}
	
	public static int randInt(int max) {
		Random rnd = new Random();
		return rnd.nextInt(max);
	}

}
