package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import factory.ActionFactory;
import topology.Loc;
import topology.TopologyFactory;


public class HouseholdSIS {
	final static String msg_contact = "contact";
	final static String act_recover = "recover";
	final static String act_move = "move";
	
	final static String st_s = "S";
	final static String st_i = "I";
	
	final static int LOC_NUM = 50;
	
	public static void main(String[] args) {
		ArrayList<Loc> locs = TopologyFactory.makeTopology(TopologyFactory.RING, LOC_NUM);
		for(Loc loc: locs) {
			loc.print();
		}
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("absSIRscript.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPal_2015epew/PALOMA models/HouseholdSIS")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//susceptible 
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String susceptible = st_s + loc.toString() + ":= ";
						susceptible += ActionFactory.genIndUniAction(msg_contact, "p", "1", st_i + loc.toString());
						
						String range = loc.toString();
						ArrayList<Loc> neighbours = loc.getNeighbours(1);
						for(int j=0; j<neighbours.size();j++) {
							susceptible += "+" + ActionFactory.genSpNoMsgAction(act_move, "r/"+neighbours.size(), st_s + neighbours.get(j).toString());
						}
						
						susceptible += ";";
						pw.println(susceptible);
					}
					
					pw.println();
					
					//infected 
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String infected = st_i + loc.toString() + ":= ";
						infected += ActionFactory.genSpUniAction(msg_contact, "r", "local", st_i + loc.toString());
						
						String range = loc.toString();
						ArrayList<Loc> neighbours = loc.getNeighbours(1);
						for(int j=0; j<neighbours.size();j++) {
							infected += "+" + ActionFactory.genSpNoMsgAction(act_move, "r/"+neighbours.size(), st_i + neighbours.get(j).toString());
						}
						
//						for(int j=0; j<neighbours.size();j++) {
//							infected += neighbours.get(j);
//						}
						
						//infected += "+" + ActionFactory.genSpUniAction(msg_contact, "r-alpha*r", range, st_i + loc.toString());
						infected += "+" + ActionFactory.genSpNoMsgAction(act_recover, "mu", st_s + loc.toString());
						infected += "+" + ActionFactory.genIndUniAction(msg_contact, "p", "1", st_i + loc.toString());
						infected += ";";
						pw.println(infected);
					}
					
					pw.println();		
					
				}
				if(data.equalsIgnoreCase("#agents")){
					
					int source[] = new int[5];
					for(int i=0;i<5;i++) {
						source[i] = randInt(LOC_NUM);
					}
					
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						//String Biker = "Biker" + comCod(i) + "[10]";
						boolean isSource = false;
						for(int j=0;j<5;j++) {
							if(source[j] == i) {
								isSource = true;
							}
						}
						int sNum = 0;
						int iNum = 0;
						if(isSource) {
							sNum = 50-5;
							iNum = 5;
						}else {
							sNum = 50;
							iNum = 0;
						}
						
						String Agents = st_s + loc.toString() + "[" + sNum + "] || ";
						
						if(i==locs.size()-1) {
							Agents += st_i + loc.toString() + "[" + iNum + "]";
						}else {
							Agents += st_i + loc.toString() + "[" + iNum + "] || ";
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
	
	public static int randInt(int max) {
		Random rnd = new Random();
		return rnd.nextInt(max);
	}

	

}
