package pevaSSA;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TraceCompare {
	static String theta = "0.1";
	
	public static void main(String[] args) throws IOException  {
		
//		for(int e = 1; e<11; e++) {
			int num = 50;
			int moment = 1;
			int totalR[] = {15,31,40,16,21,32,13,9,33,28,34,14,50,22,41,49,34,2,4,46,40,3,8,43,46,48,29,2,43,43};
			
			double errorSamples[] = new double[195*num];
			
			for( int stationNum = 0; stationNum < num ; stationNum++ ) {
				String agent = "Bike(" + stationNum + ")";
//				double[] sampleArray = getErrorRatios(agent, moment);//mean error
//				double[] sampleArray = getVarianceError(agent);//variance error
				double[] sampleArray = getStatisticalError(agent);//variance error
//				double[] sampleArray = getBhahError(agent,totalR[stationNum]);
				for(int i=0; i<195; i++) {
//					System.out.println(sampleArray[i]);
					if(sampleArray[i] < 10000)
					errorSamples[stationNum*195 + i] = sampleArray[i];
				}
			}
			
			printCI(errorSamples ,"");
//		}
		
		
		
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
	
	static double[] getVarianceError(String agent) throws IOException {
		double m1_full[] = readTrace(agent, 1, 0);
		double m2_full[] = readTrace(agent, 2, 0);
		
		double m1_reduced[] = readTrace(agent, 1, 1);
		double m2_reduced[] = readTrace(agent, 2, 1);
		
		double variance_full[] = new double[195];
		double variance_reduced[] = new double[195];
		for(int i=0; i<m1_full.length; i++) {
			
			variance_full[i] = Math.sqrt(m2_full[i] - Math.pow(m1_full[i], 2));
			variance_reduced[i] = Math.sqrt(m2_reduced[i] - Math.pow(m1_reduced[i], 2));
			System.out.println(variance_full[i] + " " + variance_reduced[i]);
			
		}
		
		
		double errorTrace[] = new double[195];
		
		for(int i=0; i<m1_full.length; i++) {
//			System.out.println(trace1[i]);
			errorTrace[i] = Math.abs(variance_full[i]-variance_reduced[i])/Math.max(variance_full[i], variance_reduced[i]);
		}
		return errorTrace;
		
	}
	
	static double[] getStatisticalError(String agent) throws IOException {
		double m1_full[] = readTrace(agent, 1, 0);
		double m2_full[] = readTrace(agent, 2, 0);
		
		double m1_reduced[] = readTrace(agent, 1, 1);
		double m2_reduced[] = readTrace(agent, 2, 1);
		
		double variance_full[] = new double[195];
		double variance_reduced[] = new double[195];
		for(int i=0; i<m1_full.length; i++) {
			
			variance_full[i] = m2_full[i] - Math.pow(m1_full[i], 2);
			variance_reduced[i] = m2_reduced[i] - Math.pow(m1_reduced[i], 2);
//			System.out.println(variance_full[i] + " " + variance_reduced[i]);
			
		}
		
		
		double errorTrace[] = new double[195];
		
		for(int i=0; i<m1_full.length; i++) {
//			System.out.println(trace1[i]);
//			errorTrace[i] = Math.abs(variance_full[i]-variance_reduced[i])/Math.max(variance_full[i], variance_reduced[i]);
			errorTrace[i] = bhattacharyyaDistance(m1_full[i], m1_reduced[i], variance_full[i], variance_reduced[i]);
			
		}
		return errorTrace;
		
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
	
	public static double bhattacharyyaDistance(double mean1, double mean2, double variance1, double variance2) {
		double ret = 0.25*Math.log(0.25*(variance1/variance2+variance2/variance1+2))
			                                +0.25*(Math.pow(mean1-mean2, 2)/(variance1+variance2));
//			              System.out.println("mean1:" + mean1 + " mean2:"+mean2 + "	var1:"+variance1 + " var2:"+variance2);
//			              System.out.println("dist:" +  ret);
        if(ret != ret) {
                return 0;
        }
        return ret;
	}
	

}
