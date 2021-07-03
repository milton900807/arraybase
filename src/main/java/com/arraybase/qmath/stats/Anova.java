package com.arraybase.qmath.stats;


public class Anova {

	static double[] v = { 9, 10, 10, 10, 10, 10, 10, 10, 10, 10 };

	public static void main(String[] _ag) {

		
		Anova c = new Anova();
		
//		ABaseNode node = ABaseNode.getNode ( "/isis/test");
//		Map<String, String> fields = node.getFields ();
//		double sum = c.sum(node, "stdv");
		
		
		
	}

	public double mean(double value, double count) {
		return value / count;
	}

	public double standardDeviation(double _variance) {
		return Math.sqrt(_variance);
	}

	public double cv(double standardDeviation, double mean) {
		return standardDeviation / mean;
	}

	public double mean(double[] v) {
		double value = 0;
		for (double d : v) {
			value += d;
		}
		return value / v.length;
	}

	public double variance(double[] v, double mean) {
		double variance = 0;
		for (int i = 0; i < v.length; i++) {
			variance += v[i] * v[i];
		}
		variance = variance / v.length - mean * mean;
		return variance;
	}

}
