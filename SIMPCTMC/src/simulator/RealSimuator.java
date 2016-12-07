package simulator;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import moment.MomentGenerator;
import couplingcoefficient.GraphBuilder;
import ctmc.transientAnalyzer;
import directedrelationgraph.ReductionProposer;
import framework.ParserLondonBike;
import plot.Counter;
import plot.ModelChecker;
import plot.PlotVarDefiner;
import plot.ploter;
import utality.Cache;
import utality.Utality;


public class RealSimuator extends Simulator{
	static Model model = new Model();
	public static int finaltime=60*1;
	public static int runs=10000;
	public static int samplingRuns = 10;
	static Counter counter = new Counter();
	static GraphBuilder gb = new GraphBuilder();
	static PlotVarDefiner pvd = new PlotVarDefiner();
	
	static int order = 1; 
	
	static double theta = 0.01;
	
	public static boolean converged = false;
	
	public static void main(String[] args) {
		Utality.init(Cache.WRITE);
		
		int stationNum = 15;
			System.out.println("current: " + stationNum);
			
			String plot = "Bike(" + stationNum + ")";
			int keyStations[] = new int[1];
			String strNum = plot.substring(plot.indexOf("(")+1, plot.indexOf(")"));
			keyStations[0] = Integer.parseInt(strNum);
			
			
			ParserLondonBike parser = new ParserLondonBike(keyStations);
			parser.parse(model);
			model.print2File();
			
			System.out.println("Prev Trans num: " + model.getTransArray().size());
//			System.out.println("Prev Agent num: " + model.getInitAgentMap().keySet().size());
			
			pvd.insertPlotVar(plot);

			counter.setUp(samplingRuns, finaltime, model.getInitAgentMap(), new ArrayList<String>());
			new RealSimuator().start(samplingRuns);
			
			gb.init(model.getInitAgentMap(), model.getTransArray());
			
			ReductionProposer rp = new ReductionProposer(model, plot);
			rp.getOptimalProposal(theta);
			
			System.out.println("Trans num: " + model.getTransArray().size());
			System.out.println("Agent num: " + model.getInitAgentMap().keySet().size());
			
			MomentGenerator mg = new MomentGenerator(model.getInitAgentMap(), model.getTransArray());
			mg.setupIndex(order);
			
//			FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-6, 10.0, 1.0e-3, 1.0e-3);
//			
//			StepHandler stepHandler = new StepHandler() {
//			    public void init(double t0, double[] y0, double t) {
//			    }
//			            
//			    public void handleStep(StepInterpolator interpolator, boolean isLast) {
//			        double   t = interpolator.getCurrentTime();
////				        double[] y = interpolator.getInterpolatedState();
////				        System.out.println(t);
//			    }
//			};
//			dp853.addStepHandler(stepHandler);
//			
//			FirstOrderDifferentialEquations ode = mg.getDiffEquations(model.getAlterPoints());
//			double[] y = mg.getInitValues(); // initial state
//			long startTime = System.currentTimeMillis();
//			
//			dp853.integrate(ode, 0.0, y, finaltime, y); // now y contains final state at time t=16.0
//			System.out.println("final: " + finaltime);
//			
//			long endTime = System.currentTimeMillis();
//			long cost = endTime - startTime;
//			System.out.println("solving ODE cost: " + cost);
//			costArray[stationNum] = cost;
//			
//			parser.printMsg();
//			
//			if(order == 1) {
//				int index = mg.getIndex(plot);
//				writeToFile(y[index], parser.getTrueNum(), parser.getCapacity(), parser.getInitNum());
//			}else if(order == 2) {
//				int index = mg.getIndex(plot);
//				int secondindex = mg.getSecondIndex(plot);
//				writeToFile(y[index], y[secondindex], parser.getTrueNum(), parser.getCapacity(), parser.getInitNum());
//			}else if(order == 3) {
//				int index = mg.getIndex(plot);
//				int secondindex = mg.getSecondIndex(plot);
//				int thirdIndex = mg.getThirdIndex(plot);
//				writeToFile(y[index], y[secondindex], y[thirdIndex], parser.getTrueNum(), parser.getCapacity(), parser.getInitNum());
//			}
			
			counter.clear();
			ModelChecker mc = new ModelChecker(plot, 0, finaltime-5, ModelChecker.LESS, 35);
			counter.setUp(runs, finaltime, model.getInitAgentMap(), mc);
			new RealSimuator().start(runs);
			
			
		
	}
	
	
	void start(int runs) {
        events = new ListQueue();
        this.final_time = counter.getFinalTime();
        
        int j = 0;
        while(j<runs && converged == false) {
//        	System.out.println("Round "+j);
        	events.clear();
        	model.reset(j);
        	
        	this.time = 0;
        	counter.preRun(j, model.getAgentMap());
        	
        	model.scheduleNextEvent(this);
        	
            doAllEvents();
            counter.afterRun(j);
            j++;
        }
       
        ploter p = new ploter();
    	p.plot(pvd.getPlots());
//    	p.export(pvd.getPlots(), System.getProperty("user.home") + "/Dropbox/matlab/data/");
//    	p.plotVariance(pvd.getPlots());
//    	p.exportVariance(pvd.getPlots(), System.getProperty("user.home") + "/Dropbox/matlab/data/");
    	//p.plotCoRR();
    }
	
	static void writeToFile(double mean, int trueNum, int capacity, int initNum){
		//PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("~/Dropbox/Spatial PEPA/fluidflow.m")),true);  
		try {
			String filename = System.getProperty("user.home")+"/Desktop/cycle data/prediction/raw" + ParserLondonBike.snapshot + "_" + ParserLondonBike.maxDpThreshold + ".txt";
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		   
		    out.println("true: " + trueNum + "  capacity: " + capacity + " init: " + initNum);
		    out.println("guess1: " + mean);
		   
		    out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void writeToFile(double mean, double variance, int trueNum, int capacity, int initNum){
		//PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("~/Dropbox/Spatial PEPA/fluidflow.m")),true);  
		try {
			String filename = System.getProperty("user.home")+"/Desktop/cycle data/prediction/raw" + ParserLondonBike.snapshot + "_" + ParserLondonBike.maxDpThreshold + ".txt";
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		   
		    out.println("true: " + trueNum + "  capacity: " + capacity + " init: " + initNum);
		    out.println("guess1: " + mean + ", " + variance);
		   
		    out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void writeToFile(double mean, double variance, double thirdMoment, int trueNum, int capacity, int initNum){
		//PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("~/Dropbox/Spatial PEPA/fluidflow.m")),true);  
		try {
			String filename = System.getProperty("user.home")+"/Desktop/cycle data/prediction/raw" + ParserLondonBike.snapshot + "_" + ParserLondonBike.maxDpThreshold + ".txt";
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		   
		    out.println("true: " + trueNum + "  capacity: " + capacity + " init: " + initNum);
		    out.println("guess1: " + mean + ", " + variance + ", " + thirdMoment);
		   
		    out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static void writeToFile(double mean, double probs[]){
		//PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("~/Dropbox/Spatial PEPA/fluidflow.m")),true);  
		try {
			String filename = System.getProperty("user.home")+"/Desktop/cycle data/prediction/raw" + ParserLondonBike.snapshot + "_" + ParserLondonBike.maxDpThreshold + ".txt";
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		    
		    out.println("guess2: " + mean);
		    String str = "guess2->";
			for(int i=0; i<probs.length; i++) {
				str+= i + ":" +probs[i] +";";
			}
			out.println(str);
		    out.println("");
		    
		    out.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	static void writeToFile(double probs[]){
//		//PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("~/Dropbox/Spatial PEPA/fluidflow.m")),true);  
//		try {
//			String filename = System.getProperty("user.home")+"/Desktop/cycle data/prediction/raw" + ParserLondonBike.snapshot + "_" + ParserLondonBike.maxDpThreshold + ".txt";
//			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
//		    
//		    double total = 0;
//			String str = "guess1->";
//			for(int i=0; i<probs.length; i++) {
//				str+= i + ":" +probs[i] +";";
//				total += probs[i];
//			}
//			out.println(str);
//		    out.println("total:" + total);
//		    out.close();
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
//	public static double[] gradientDescent(double mean, double variance, double thirdMoment, int range, int trueNum) {
//		if(mean < 0) {
//			double ret[] = new double[range+1];
//			ret[0] = 1;
//			for(int i=1; i<=range; i++) {
//				ret[i] = 0;
//			}
//			return ret;
//		}else if(mean > range) {
//			double ret[] = new double[range+1];
//			ret[range] = 1;
//			for(int i=0; i<range; i++) {
//				ret[i] = 0;
//			}
//			return ret;
//		}
//		
//		double mu[] = new double[3];
//		mu[0] = mean;
//		mu[1] = variance;
//		mu[2] = thirdMoment;
//		
//		DescentFunction df = new DescentFunction(range, mu);
//		boolean verbose = true;
//		LbfgsMinimizer minimizer = new LbfgsMinimizer(verbose);
//		double[] lambda = minimizer.minimize(df);
//		
//		double[] prob = df.generate(range, lambda);
//		return prob;
//	}
	
	public static Counter getCounter() {
		return counter;
	}
	
	public static GraphBuilder getGraphBuilder() {
		return gb;
	}
	
	public static Model getModel() {
		return model;
	}

}