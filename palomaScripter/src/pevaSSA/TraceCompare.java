package pevaSSA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TraceCompare {

	public static void main(String[] args) throws IOException  {
		
		int[] validSts = {0,1,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0};
		
		int num = 50;
		int moment = 1;
		
		double errorSamples[] = new double[180*num];
		
		for( int stationNum = 0; stationNum < num ; stationNum++ ) {
			String agent = "Bike(" + 0 + ")";
			double[] sampleArray = getErrorRatios(agent, moment);
			for(int i=0; i<180; i++) {
				System.out.println(sampleArray[i]);
				errorSamples[stationNum*180 + i] = sampleArray[i];
			}
		}
		
		printCI(errorSamples, validSts ,"");
		
	}
	
	static void printCI(double array[], int[] validSts, String statistics) {
		
		int total = 0;
		
		double avg = 0;
		for(int i=0; i<array.length; i++) {
			if(validSts[i/180] == 1) {
				avg += array[i];
				total += 1;
			}
			
		}
		
		avg = avg*1.0/total;
		
		double sigma = 0;
		for(int i=0; i<array.length; i++) {
			if(validSts[i/180] == 1) {
				sigma += Math.pow(array[i]-avg, 2);
			}
		}
		sigma = Math.sqrt(sigma*1.0/total);
		double interval = 1.96*sigma/(Math.sqrt(total));
		
		System.out.println(statistics + " " + "avg: " + avg);
		System.out.println(statistics + " " + "interval: " + interval);
	}
	
	static double[] getErrorRatios(String agent, int moment) throws IOException {
		double trace1[] = readTrace(agent, moment, 0);
		double trace2[] = readTrace(agent, moment, 1);
		
		double errorTrace[] = new double[180];
		
		for(int i=0; i<trace1.length; i++) {
//			System.out.println(trace1[i]);
			errorTrace[i] = Math.abs(trace1[i]-trace2[i])/(trace1[i]);
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
			inputfile = "../data/" + agent + suffix;
		}
		
		double trace[] = new double[180];
		
		String data = null;
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile)));
			int current = 0;
			while((data=br.readLine())!=null) {
				if(current >= 20) {
//					System.out.println(data);
					trace[current-20] = Double.parseDouble(data);
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
