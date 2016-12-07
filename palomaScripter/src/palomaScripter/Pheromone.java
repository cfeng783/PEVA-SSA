package palomaScripter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import topology.Loc;
import topology.TopologyFactory;
import factory.ActionFactory;

public class Pheromone {

	
	final static String st_sensor = "Sensor";
	final static String st_sink = "Sink";
	
	final static int LOC_NUM = 25;
	
	final static int MAX = 5;
	
	public static void main(String[] args) {
		ArrayList<Loc> locs = TopologyFactory.makeTopology(TopologyFactory.GRID, LOC_NUM);
		for(Loc loc: locs) {
			loc.print();
		}
		
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("pheromone.txt")));  
			String data = null;
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.home") + "/Dropbox/PSimPal_2015epew/PALOMA models/pheromone")),true);  
			while((data = br.readLine())!=null)  
			{  	
				pw.println(data);
				if(data.equalsIgnoreCase("#states")){
					//sensor
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						
						String range = loc.toString();
						ArrayList<Loc> neighbours = loc.getNeighbours(1);
						for(int j=0; j<neighbours.size();j++) {
							range += "," + neighbours.get(j).toString();
						}
						
						for(int j=0; j<=MAX; j++) {
							String sensor = st_sensor + j + loc.toString() + ":= ";
							sensor += ActionFactory.genSpNoMsgAction("off", "offr", st_sensor + "_off" + loc.toString());
//							
							if(j>0) {
								int k = j-1;
								sensor += "+" +  ActionFactory.genSpNoMsgAction("evapor", "mu", st_sensor + k + loc.toString());
								sensor += "+" + ActionFactory.genSpBrAction("m"+j, "lambda", range, st_sensor + j + loc.toString());					
							}
							
							
							for(int k=j+1; k<=MAX; k++) {
								if(sensor.endsWith(":= ")) {
									sensor += "+" + ActionFactory.genIndBrAction("m"+k, "p", "1/(dist(loc_s,loc_r)+1)", st_sensor + k + loc.toString());
								}else {
									sensor += "+" +  ActionFactory.genIndBrAction("m"+k, "p", "1/(dist(loc_s,loc_r)+1)", st_sensor + k + loc.toString());
								}
							}
							
//							for(int k=0; k<loc.getConnectedLocs().size();k++) {
//								if(!loc.getKey().equals(loc.getConnectedLocs().get(k))) {
//									sensor += "+" + ActionFactory.genSpNoMsgAction("move", "mr", st_sensor + j + loc.getConnectedLocs().get(k).toString());
//								}
//							}
							
							sensor += ";";
							pw.println(sensor);
							pw.println();
						}
						
						String sensor_off = st_sensor + "_off" + loc.toString() + ":= ";
						sensor_off += ActionFactory.genSpNoMsgAction("on", "onr", st_sensor + 0 + loc.toString());
						pw.println(sensor_off + ";");
						pw.println();
					}
					
					pw.println();
					
					//sink 
					for(int i=0; i<locs.size(); i++) {
						if(i != (LOC_NUM-1)/2) {
							continue;
						}
//						if(i != 0) {
//							continue;
//						}
						Loc loc = locs.get(i);
						String sink = st_sink + loc.toString() + ":= ";
						String range = loc.toString();
						ArrayList<Loc> neighbours = loc.getNeighbours(2);
						for(int j=0; j<neighbours.size();j++) {
							range += "," + neighbours.get(j).toString();
						}
						sink += ActionFactory.genSpBrAction("m"+MAX, "lambda", "all", st_sink + loc.toString());
						pw.println(sink + ";");
						
					}
					
					pw.println();		
					
				}
				if(data.equalsIgnoreCase("#agents")){
					
					for(int i=0; i<locs.size(); i++) {
						Loc loc = locs.get(i);
						String Agents ="";
//						for(int j=0;j<=MAX;j++) {
//							Agents += st_sensor + j + loc.toString()+ "[" + 6+ "] || ";
//						}
						
						
						if(i==locs.size()-1) {
							Agents += st_sensor + "0" + loc.toString() + "[" + 1+ "] || ";
						}else {
							Agents += st_sensor + "0" + loc.toString() + "[" + 1 + "] || ";
						}
						pw.println(Agents);
					}
					
					int i = (LOC_NUM-1)/2;
					Loc loc = locs.get(i);
					pw.println(st_sink + loc.toString());
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
