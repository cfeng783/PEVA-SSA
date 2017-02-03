package directedrelationgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	
	private ArrayList<String> target;
	
	private Model model;
	private DRG drg;
	
	public ReductionProposer(Model model, ArrayList<String> target) {
		this.model = model;
		this.target = target;
		this.drg = new DRG(model.getInitAgentMap(), model.getTransArray());
		this.drg.makeGraph();
		System.out.println("DCG graph initialised!");
	}
	
	public void getOptimalProposal(double theta) {
//		ArrayList<Double> yOrigin = solveOdeModel(model.getInitAgentMap(), model.getTransArray());
		ArrayList<Double> yOrigin = null;
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
		for(String targetpop: target) {
			targetSet.add(targetpop);
		}
		
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
				
//				for(int i=0; i<trans.getProducts().size(); i++) {
//					skeletalSet.add(trans.getProducts().get(i).getName());
//				}
				for(String targetpop: target) {
					skeletalSet.add(targetpop);
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
		
		ArrayList<Trans> transformedTransArray = new ArrayList<Trans>();
		HashMap<String, Trans> transformTransMap = new HashMap<String, Trans>();
//		HashMap<String, Trans> comsumptionTransMap = new HashMap<String, Trans>();
//		HashMap<String, Trans> productionTransMap = new HashMap<String, Trans>();
		for(Trans trans: removableTransArray) {
			if(isBorderTrans(trans,skeletalAgentMap.keySet())) {
				ArrayList<RPItem> validReactants = new ArrayList<RPItem>();
				ArrayList<RPItem> validProducts = new ArrayList<RPItem>();
				ArrayList<RateItem> validRateItems = new ArrayList<RateItem>();
				for(int i=0; i<trans.getReactants().size(); i++) {
					String agent = trans.getReactants().get(i).getName();
					if(skeletalAgentMap.containsKey(agent)) {// this is a border transition
						validReactants.add(trans.getReactants().get(i));
					}
				}
				
				for(int i=0; i<trans.getProducts().size(); i++) {
					String agent = trans.getProducts().get(i).getName();
					if(skeletalAgentMap.containsKey(agent) ) { 
						validProducts.add(trans.getProducts().get(i));
					}
				}
				
				for(int i=0; i<trans.getRateFactors().size(); i++) {
					String agent = trans.getRateFactors().get(i).getName();
					if(skeletalAgentMap.containsKey(agent) ) { 
						validRateItems.add(trans.getRateFactors().get(i));
					}
				}
				
				if(validReactants.size() != trans.getReactants().size() || validRateItems.size() != trans.getRateFactors().size()) {
					ArrayList<RateItem> rateItems = new ArrayList<RateItem>();
					double factor = trans.getFireCount()*1.0/RealSimuator.finaltime/RealSimuator.samplingRuns;
					
					String key = "";
					for(int i=0; i<validReactants.size(); i++) {
						key += validReactants.get(i).getName() + "#" + validReactants.get(i).getCount() + "$";
					}
					for(int i=0; i<validProducts.size(); i++) {
						key += validProducts.get(i).getName() + "#" + validProducts.get(i).getCount() + "$";
					}
					if(transformTransMap.containsKey(key)) {
						transformTransMap.get(key).addConstRate(factor);
					}else {
						Trans transformedTrans = new Trans(validReactants, validProducts,
								factor, rateItems);
						transformTransMap.put(key, transformedTrans);
					}
					
					
				}else {
					Trans transformedTrans = new Trans(validReactants, validProducts,
							trans.getConstRateFactor(), validRateItems);
					transformedTransArray.add(transformedTrans);
				}
				
			}
			
		}
		
		
		
		
//		for(String key: comsumptionTransMap.keySet()) {
//			skeletalTransArray.add(comsumptionTransMap.get(key));
//		}
//		
		for(String key: transformTransMap.keySet()) {
			skeletalTransArray.add(transformTransMap.get(key));
		}
		
		for(Trans trans: transformedTransArray) {
			skeletalTransArray.add(trans);
		}
		
		
		HashMap<String, Trans> aggTransMap = new HashMap<String, Trans>();
		ArrayList<Trans> invalidTrans = new ArrayList<Trans>();
		for(Trans trans: skeletalTransArray) {
			
			ArrayList<RPItem> invalidProducts = new ArrayList<RPItem>();
			
			
			for(int i=0; i<trans.getProducts().size(); i++) {
				String agent = trans.getProducts().get(i).getName();
				if(!skeletalAgentMap.containsKey(agent) ) { 
					invalidProducts.add(trans.getProducts().get(i));
				}
			}
			
			
			
			
			for(RPItem item: invalidProducts) {
				trans.getProducts().remove(item);
			}
			
			
			if(trans.getRateFactors().size() == 0) {
				String key = "";
				for(int i=0; i<trans.getReactants().size(); i++) {
					key += trans.getReactants().get(i).getName() + "#" + trans.getReactants().get(i).getCount() + "$";
				}
				for(int i=0; i<trans.getProducts().size(); i++) {
					key += trans.getProducts().get(i).getName() + "#" + trans.getProducts().get(i).getCount() + "$";
				}
				if(aggTransMap.containsKey(key)) {
					aggTransMap.get(key).addConstRate(trans.getConstRateFactor()[0]);
				}else {
					aggTransMap.put(key, trans);
				}
				invalidTrans.add(trans);
				
			}
			
			
		}
		
		for(Trans trans: invalidTrans) {
			skeletalTransArray.remove(trans);
		}
		
		for(String key: aggTransMap.keySet()) {
			skeletalTransArray.add(aggTransMap.get(key));
		}
		
//		ArrayList<Double> trace = solveOdeModel(skeletalAgentMap, skeletalTransArray);
//		double error = calMaxError(yOrigin, trace);
//		System.out.println("theta: " + theta + " error: " + error);
		return new Proposal(0, skeletalTransArray, skeletalAgentMap);
	}
	
	private boolean isBorderTrans(Trans trans, Set<String> skeletalSet) {
		for(int i=0; i<trans.getReactants().size(); i++) {
			String agent = trans.getReactants().get(i).getName();
			if(skeletalSet.contains(agent) && trans.produced(agent) != trans.consumed(agent)) {
				return true;
			}
		}
		
		for(int i=0; i<trans.getProducts().size(); i++) {
			String agent = trans.getProducts().get(i).getName();
			if(skeletalSet.contains(agent) && trans.produced(agent) != trans.consumed(agent)) {
				return true;
			}
		}
		return false;
	}
	
	
	
//	private ArrayList<Double> solveOdeModel(HashMap<String, Integer> skeletalAgentMap, ArrayList<Trans> skeletalTransArray) {
//		MomentGenerator mg = new MomentGenerator(skeletalAgentMap, skeletalTransArray);
//		mg.setupIndex(2);
//		
//		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-6, 10.0, 1.0e-3, 1.0e-3);
//		
//		final int targetIndex = mg.getIndex(target);
//		final ArrayList<Double> ret = new ArrayList<Double>();
//		FixedStepHandler stepHandler = new FixedStepHandler() {
//		    public void init(double t0, double[] y0, double t) {
//		    }
//			@Override
//			public void handleStep(double t,
//		              double[] y,
//		              double[] yDot,
//		              boolean isLast) {
//				System.out.println(t+": " + y[targetIndex]);
//				ret.add(y[targetIndex]);
////				if(isLast) {
////					System.out.println(t+": " + y[targetIndex]);
////				}
//			}
//		};
//		StepNormalizer stepNorm = new StepNormalizer(RealSimuator.finaltime/50, stepHandler);
//		dp853.addStepHandler(stepNorm);
//		
//		FirstOrderDifferentialEquations ode = mg.getDiffEquations(model.getAlterPoints());
//		double[] y = mg.getInitValues(); // initial state
//
//		dp853.integrate(ode, 0.0, y, RealSimuator.finaltime, y); // now y contains final state at time t=16.0
//		
//		return ret;
//	}
	
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
		double CI = getCI(ret, mean, ret.length);
		System.out.println(mean + "+-" + CI);
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
		
		double interval = 1.96*sigma/Math.sqrt(size);
		return interval;
	}
	
}
