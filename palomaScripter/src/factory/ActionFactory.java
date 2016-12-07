package factory;

public class ActionFactory {
	public static String genSpUniAction(String name, String rate, String range, String nextSt) {
		return "!!(" + name + ", " + rate + ")@IR{" + range + "}." + nextSt;
	}
	
	public static String genIndUniAction(String name, String prob, String wt, String nextSt) {
		return "??(" + name + ", " + prob + ")@Wt{" + wt + "}." + nextSt;
	}
	
	public static String genSpNoMsgAction(String name, String rate, String nextSt) {
		return "(" + name + ", " + rate + ")." + nextSt;
	}
	
	public static String genSpBrAction(String name, String rate, String range, String nextSt) {
		return "!(" + name + ", " + rate + ")@IR{" + range + "}." + nextSt;
	}
	
	public static String genIndBrAction(String name, String prob, String wt, String nextSt) {
		return "?(" + name + ", " + prob + ")@Pr{" + wt + "}." + nextSt;
	}
}
