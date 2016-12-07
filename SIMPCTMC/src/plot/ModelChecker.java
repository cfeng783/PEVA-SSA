package plot;

import java.util.ArrayList;

public class ModelChecker {
	public final static int LESS = 101;
	public final static int GREATER = 102;
	public final static int LESS_EQUAL = 103;
	public final static int GREATER_EQUAL = 104;
	
	final static double SIGMA = 0.02;
	private double t_begin;
	private double t_end;
	private int operator;
	private double comparator;
	private String agent;
	
	private int succ = 0;
	private int total = 0;
	
	public ModelChecker(String agent, double t_begin, double t_end, int operator, double comparator) {
		this.setAgent(agent);
		this.setT_begin(t_begin);
		this.setT_end(t_end);
		this.setOperator(operator);
		this.comparator = comparator;
	}

	public double getT_begin() {
		return t_begin;
	}

	public void setT_begin(double t_begin) {
		this.t_begin = t_begin;
	}

	public double getT_end() {
		return t_end;
	}

	public void setT_end(double t_end) {
		this.t_end = t_end;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public boolean check(ArrayList<Double> trace, double finaltime) {
		total++;
		int spos = new Double( t_begin/(finaltime/trace.size())).intValue();
		int epos = new Double( t_end/(finaltime/trace.size())).intValue();
		
//		System.out.println("spos: " + spos);
//		System.out.println("epos: " + epos);
		
		for(int i=spos; i<=epos; i++) {
			if(this.operator == LESS) {
				if(trace.get(i) >= comparator) {
					return false;
				}
			}else if(this.operator == GREATER) {
				if(trace.get(i) <= comparator) {
					return false;
				}
			}
			
//			System.out.print(trace.get(i) + " ");
			
		}
//		System.out.println("true");
		succ++;
		return true;
	}
	
	public boolean converge() {
		if(total<100) {
			return false;
		}
		double ratio = succ*1.0/total;
		double interval = 2*1.96*Math.sqrt(ratio*(1-ratio)/total);
		if(total% 100 == 0) {
			System.out.println("interval: "+ interval);
		} 
		if(interval<SIGMA) {
			return true;
		}else {
			return false;
		}
	}

	public double getRatio() {
		return succ*1.0/total;
	}
	
}
