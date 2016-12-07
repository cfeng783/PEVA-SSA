package directedrelationgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import moment.MomentGenerator;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;

import simulator.Model;
import simulator.RealSimuator;
import trans.RPItem;
import trans.RateItem;
import trans.Trans;

public class ReductionProposer {
	
	private final double convergence = 0.005; 
	private final double sigma = 0.00000001; 
	
	private String target;
	
	private Model model;
	private DRG drg;
	
	public ReductionProposer(Model model, String target) {
		this.model = model;
		this.target = target;
		this.drg = new DRG(model.getInitAgentMap(), model.getTransArray());
		this.drg.makeGraph();
//		System.out.println("graph initialised!");
	}
	
	public void getOptimalProposal(double theta) {
		ArrayList<Double> yOrigin = solveOdeModel(model.getInitAgentMap(), model.getTransArray());
		
//		boolean isConverged = false;
//		double upbound = 1;
//		double lowbound = 0;
//		double okThreshold = lowbound;
//		do{
//			double mid = (lowbound + upbound)/2;
//			Proposal proposal = getProposal(mid, yOrigin);
//			if(proposal != null) {
//				double error = proposal.getError();
//				if(error<theta) {
//					if(theta-error > convergence) {
//						lowbound = mid;
//						okThreshold = mid;
//					}else {
//						System.out.println("converge!");
//						isConverged = true;
//						model.init(proposal.getSkeletalTransArray(), proposal.getSkeletalAgentMap());
//						break;
//					}
//				}
//				if(error>=theta) {
//					upbound = mid;
//				}
//			}else {
//				break;
//			}
//			
//		}while(lowbound<=upbound);
		
//		if(isConverged == false) {
//			if(okThreshold > 0) {
				Proposal proposal = getProposal(theta, yOrigin);
				model.init(proposal.getSkeletalTransArray(), proposal.getSkeletalAgentMap());
//			}else {
//				System.out.println("fail to reduce");
//			}
//		}
	}
	
	
	private Proposal getProposal(double theta, ArrayList<Double> yOrigin) {
		HashSet<String> targetSet = new HashSet<String>();
		targetSet.add(target);
		HashSet<String> skeletalSet = drg.makeSkeletalMechanism(targetSet, theta);
		if(skeletalSet == null) {
			return null;
		}
		ArrayList<Trans> skeletalTransArray = new ArrayList<Trans>();
		HashMap<String, Integer> skeletalAgentMap = new HashMap<String, Integer>();
		
		ArrayList<Trans> removableTransArray = new ArrayList<Trans>();
		
		for(Trans trans: model.getTransArray()) {
			if(skeletalSet.contains(trans.getID())) {
				skeletalTransArray.add(trans);
				for(int i=0; i<trans.getReactants().size(); i++) {
					skeletalSet.add(trans.getReactants().get(i).getName());
				}
				
				for(int i=0; i<trans.getRateFactors().size(); i++) {
					skeletalSet.add(trans.getRateFactors().get(i).getName());
				}
				
				for(int i=0; i<trans.getProducts().size(); i++) {
					skeletalSet.add(trans.getProducts().get(i).getName());
				}
				
			}else {
				removableTransArray.add(trans);
			}
		}
		
		for(String agent: model.getInitAgentMap().keySet()) {
			if(skeletalSet.contains(agent)) {
				skeletalAgentMap.put(agent, model.getInitAgentMap().get(agent));
			}
		}
		
		HashMap<String, Trans> comsumptionTransMap = new HashMap<String, Trans>();
		HashMap<String, Trans> productionTransMap = new HashMap<String, Trans>();
		for(Trans trans: removableTransArray) {
			for(int i=0; i<trans.getReactants().size(); i++) {
				String agent = trans.getReactants().get(i).getName();
				if(skeletalAgentMap.containsKey(agent)) {
					if(comsumptionTransMap.containsKey(agent)) {
						double factor = trans.getFireCount()*1.0/RealSimuator.finaltime/RealSimuator.samplingRuns;
						comsumptionTransMap.get(agent).addConstRate(factor);
					}else {
						double factor = trans.getFireCount()*1.0/RealSimuator.finaltime/RealSimuator.samplingRuns;
						ArrayList<RPItem> reactants = new ArrayList<RPItem>();
						ArrayList<RPItem> products = new ArrayList<RPItem>();
						ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
						reactants.add(new RPItem(agent,1));
						Trans trn = new Trans(reactants, products,factor, rateItems);
						comsumptionTransMap.put(agent, trn);
					}
				}
			}
			
			for(int i=0; i<trans.getProducts().size(); i++) {
				String agent = trans.getProducts().get(i).getName();
				if(skeletalAgentMap.containsKey(agent)) {
					if(productionTransMap.containsKey(agent)) {
						double factor = trans.getFireCount()*1.0/RealSimuator.finaltime/RealSimuator.samplingRuns;
						productionTransMap.get(agent).addConstRate(factor);
					}else {
						double factor = trans.getFireCount()*1.0/RealSimuator.finaltime/RealSimuator.samplingRuns;
						ArrayList<RPItem> products = new ArrayList<RPItem>();
						ArrayList<RPItem> reactants = new ArrayList<RPItem>();
						ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
						products.add(new RPItem(agent,1));
						Trans trn = new Trans(reactants, products,factor, rateItems);
						productionTransMap.put(agent, trn);
					}
				}
			}
			
			
		}
		
		for(String key: comsumptionTransMap.keySet()) {
			skeletalTransArray.add(comsumptionTransMap.get(key));
		}
		
		for(String key: productionTransMap.keySet()) {
			skeletalTransArray.add(productionTransMap.get(key));
		}
		
//		ArrayList<Double> trace = solveOdeModel(skeletalAgentMap, skeletalTransArray);
//		double error = calMaxError(yOrigin, trace);
//		System.out.println("theta: " + theta + " error: " + error);
		return new Proposal(0, skeletalTransArray, skeletalAgentMap);
	}
	
	
	
	private ArrayList<Double> solveOdeModel(HashMap<String, Integer> skeletalAgentMap, ArrayList<Trans> skeletalTransArray) {
		MomentGenerator mg = new MomentGenerator(skeletalAgentMap, skeletalTransArray);
		mg.setupIndex(1);
		
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-6, 10.0, 1.0e-3, 1.0e-3);
		
		final int targetIndex = mg.getIndex(target);
		final ArrayList<Double> ret = new ArrayList<Double>();
		FixedStepHandler stepHandler = new FixedStepHandler() {
		    public void init(double t0, double[] y0, double t) {
		    }
			@Override
			public void handleStep(double t,
		              double[] y,
		              double[] yDot,
		              boolean isLast) {
//				System.out.println(t+": " + y[targetIndex]);
				ret.add(y[targetIndex]);
//				if(isLast) {
//					System.out.println(t+": " + y[targetIndex]);
//				}
			}
		};
		StepNormalizer stepNorm = new StepNormalizer(RealSimuator.finaltime/50, stepHandler);
		dp853.addStepHandler(stepNorm);
		
		FirstOrderDifferentialEquations ode = mg.getDiffEquations(model.getAlterPoints());
		double[] y = mg.getInitValues(); // initial state

		dp853.integrate(ode, 0.0, y, RealSimuator.finaltime, y); // now y contains final state at time t=16.0
		
		return ret;
	}
	
	static double calMaxError(ArrayList<Double> data1, ArrayList<Double> data2) {
		double ret[] = new double[data1.size()];
		for(int i=0; i<data1.size(); i++) {
			double error = Math.abs(data1.get(i)-data2.get(i))/Math.abs(data1.get(i));
			if(error != error) {
				ret[i] = 0;
			}else {
				ret[i] = error;
			}
			
		}
		
		double mean = getMean(ret, ret.length);
//		double CI = getCI(ret, mean, ret.length);
		return mean;
	}
	
	static double getMean(double[] data, int size) {
		double avg = 0;
		for(int i=0; i<size; i++) {
			avg += data[i];
		}
		avg = avg/size;
		return avg;
	}
	
	static double getCI(double[] data, double avg, int size) {
		double sigma = 0;
		for(int i=0; i<size; i++) {
			sigma += Math.pow(data[i]-avg, 2);
		}
		sigma = Math.sqrt((sigma*1.0)/size);
		
		double interval = 1.64*sigma/Math.sqrt(size);
		return interval;
	}
	
}
