package pevaSSA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TraceCompare {
	final static String theta = "0.005";
	
	public static void main(String[] args) throws IOException  {
		
		
		int num = 10;
		int moment = 3;
		
		
		
		double errorSamples[] = new double[195*num];
		
		for( int stationNum = 0; stationNum < num ; stationNum++ ) {
			String agent = "Bike(" + stationNum + ")";
			double[] sampleArray = getErrorRatios(agent, moment);
			for(int i=0; i<195; i++) {
				System.out.println(sampleArray[i]);
				errorSamples[stationNum*195 + i] = sampleArray[i];
			}
		}
		
		printCI(errorSamples ,"");
		
	}
	
	static void printCI(double array[],  String statistics) {
		
		int total = 0;
		
		double avg = 0;
		for(int i=0; i<array.length; i++) {
			
			avg += array[i];
			total += 1;
			
			
		}
		
		avg = avg*1.0/total;
		
		double sigma = 0;
		for(int i=0; i<array.length; i++) {
			
			sigma += Math.pow(array[i]-avg, 2);
			
		}
		sigma = Math.sqrt(sigma*1.0/total);
		double interval = 1.96*sigma/(Math.sqrt(total));
		
		System.out.println(statistics + " " + "avg: " + avg);
		System.out.println(statistics + " " + "interval: " + interval);
	}
	
	static double[] getErrorRatios(String agent, int moment) throws IOException {
		double trace1[] = readTrace(agent, moment, 0);
		double trace2[] = readTrace(agent, moment, 1);
		
		double errorTrace[] = new double[195];
		
		for(int i=0; i<trace1.length; i++) {
//			System.out.println(trace1[i]);
			errorTrace[i] = Math.abs(trace1[i]-trace2[i])/Math.max(trace1[i], trace2[i]);
		}
		return errorTrace;
		
	}
	
	static double[] readTrace(String agent, int moment, int mode){
		String suffix = "_";
		if(moment == 1) {
			suffix += "mean";
		}else if(moment == 2) {
			suffix += "variance";
		}else {
			suffix += "skewness";
		}
		
		String inputfile = "";
		
		if(mode == 0) {
			inputfile = "../ori-data/" + agent + suffix;
		}else {
			inputfile = "../data/" +theta + "/"+ agent + suffix;
		}
		
		double trace[] = new double[195];
		
		String data = null;
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile)));
			int current = 0;
			while((data=br.readLine())!=null) {
				if(current >= 5) {
//					System.out.println(data);
					trace[current-5] = Double.parseDouble(data);
				}
				current++;
			}
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		
		
		return trace;
	}

}
