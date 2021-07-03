package com.arraybase.qmath.stats;

import org.apache.commons.math3.special.Beta;

public class TTest {

	// return the mean of the array x[]
	public static double mean(double[] x) {
		int N = x.length;
		double sum = 0.0;
		for (int i = 0; i < N; i++) {
			sum += x[i];
		}
		return sum / N;
	}

	// return the sample variance of the array x[]
	public static double variance(double[] x) {
		int N = x.length;
		double mu = mean(x);
		double sum = 0.0;
		for (int i = 0; i < N; i++) {
			double delta = x[i] - mu;
			sum += delta * delta;
		}
		return sum / (N - 1);
	}

	public static double tstat(double[] x, double[] y) {
		int n1 = x.length;
		int n2 = y.length;
		if (n1 != n2)
			throw new RuntimeException("array sizes must be equal");
		int N = n1;

		// compute means
		double mu1 = mean(x);
		double mu2 = mean(y);

		// compute variances
		double var1 = variance(x);
		double var2 = variance(y);

		// compute t-statistic
		return (mu1 - mu2) / Math.sqrt(var1 / N + var2 / N);
	}

	private double degreesOfFreedom = 0;

	public double pairedT(double[] sample1, double[] sample2)
			throws IllegalArgumentException, MathException, StatsException {
		if ((sample1 == null)
				|| (sample2 == null || Math.min(sample1.length, sample2.length) < 2)) {
			throw new IllegalArgumentException(
					"insufficient data for t statistic");
		}
		double meanDifference = meanDifference(sample1, sample2);
		return t(meanDifference, 0,
				varianceDifference(sample1, sample2, meanDifference),
				(double) sample1.length);
	}

//	public double pairedTTest(double[] sample1, double[] sample2)
//			throws IllegalArgumentException, MathException, StatsException {
//
//		int s1l = sample1.length;
//		int s2l = sample2.length;
//
//		double meanDifference = meanDifference(sample1, sample2);
		// double test_value = tTest(meanDifference, 0,
		// varianceDifference(sample1, sample2, meanDifference),
		// (double) sample1.length);
//		TTest ttest = new TTest();
//		double paired_t = ttest.pairedTTest(sample1, sample2);
//		// double test_value2 = ttest.t(sample1, sample2);
//		return paired_t;
//	}

	/**
	 * as of 08.20.2014 this is not used because we're just going to adjust the
	 * N for each replicate. --makes better experimental sense. This is a
	 * welche's t test used where n1!=n2
	 * 
	 * @param sample1
	 * @param sample2
	 * @return
	 * @throws IllegalArgumentException
	 * @throws MathException
	 * @throws StatsException
	 */
	public double welchsTTest(double[] sample1, double[] sample2)
			throws IllegalArgumentException, MathException, StatsException {
		double s1 = variance(sample1);
		double s2 = variance(sample2);
		int n1 = sample1.length;
		int n2 = sample2.length;
		double x1 = mean(sample1);
		double x2 = mean(sample2);

		// (N1 - 1) + (N2 - 1) --> df

		// P Value = [ 1/ ( (√df) Β(1/2,df/2) ) ] lt->-t to t ∫ ( 1+
		// x²/df)(-(v+1)/2) .dx
		return (x1 - x2) / Math.sqrt((s1 / n1) + (s2 / n2));
		// double meanDifference = meanDifference(sample1, sample2);
		// return tTest(meanDifference, 0,
		// varianceDifference(sample1, sample2, meanDifference),
		// (double) sample1.length);
	}

	protected double tTest(double m1, double m2, double v1, double v2,
			double n1, double n2) throws StatsException, MathException {
		double t = Math.abs(t(m1, m2, v1, v2, n1, n2));
		double degreesOfFreedom = 0;
		degreesOfFreedom = df(v1, v2, n1, n2);
		this.degreesOfFreedom = degreesOfFreedom;
		return 1.0 - cumulativeProbability(-t, t);
	}

	protected double tTest(double m, double mu, double v, double n)
			throws MathException {
		double t = Math.abs(t(m, mu, v, n));
		degreesOfFreedom = (n - 1);
		return 1.0 - cumulativeProbability(-t, t);
	}

	public double cumulativeProbability(double x0, double x1)
			throws MathException {
		if (x0 > x1) {
			throw new IllegalArgumentException(
					"lower endpoint must be less than or equal to upper endpoint");
		}
		return cumulativeProbability(x1) - cumulativeProbability(x0);
	}

	private double cumulativeProbability(double x) throws MathException {
		double ret;
		if (x == 0.0) {
			ret = 0.5;
		} else {
			double t = Beta.regularizedBeta(getDegreesOfFreedom()
					/ (getDegreesOfFreedom() + (x * x)),
					0.5 * getDegreesOfFreedom(), 0.5);
			if (x < 0.0) {
				ret = 0.5 * t;
			} else {
				ret = 1.0 - 0.5 * t;
			}
		}
		return ret;
	}

	private double getDegreesOfFreedom() {
		return degreesOfFreedom;
	}

	protected double t(double m1, double m2, double v1, double v2, double n1,
			double n2) {
		return (m1 - m2) / Math.sqrt((v1 / n1) + (v2 / n2));
	}

	protected double t(double m, double mu, double v, double n) {
		return (m - mu) / Math.sqrt(v / n);
	}

	protected double df(double v1, double v2, double n1, double n2) {
		return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2)))
				/ ((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2)
						/ (n2 * n2 * (n2 - 1d)));
	}

	/**
	 * Calculate the variance
	 * 
	 * @param sample1
	 * @param sample2
	 * @param meanDifference
	 * @return
	 * @throws StatsException
	 */
	public static double varianceDifference(final double[] sample1,
			final double[] sample2, double meanDifference)
			throws StatsException {
		double sum1 = 0d;
		double sum2 = 0d;
		double diff = 0d;
		int n = sample1.length;
		if (n != sample2.length) {
			throw new StatsException(
					"sample1 length is not equal to the sample2 length",
					sample2.length, 1);
		}
		if (n < 2) {
			new StatsException(
					"sample1 length is not equal to the sample2 length",
					sample2.length, 1);
		}
		for (int i = 0; i < n; i++) {
			diff = sample1[i] - sample2[i];
			sum1 += (diff - meanDifference) * (diff - meanDifference);
			sum2 += diff - meanDifference;
		}
		return (sum1 - (sum2 * sum2 / n)) / (n - 1);
	}

	public static double meanDifference(final double[] sample1,
			final double[] sample2) throws IllegalArgumentException,
			StatsException {
		return sumDifference(sample1, sample2) / sample1.length;
	}

	public static double sumDifference(final double[] sample1,
			final double[] sample2) throws StatsException {
		int n = sample1.length;
		if (n != sample2.length) {
			throw new StatsException(
					"sample1 length is not equal to the sample2 length",
					sample2.length, 1);
		}
		if (n < 1) {
			throw new StatsException("sample1 length  is less than 1",
					sample2.length, 1);
		}
		double result = 0;
		for (int i = 0; i < n; i++) {
			result += sample1[i] - sample2[i];
		}
		return result;
	}

	public static void main(String[] args) {
		// read data from standard input
		// int N = StdIn.readInt();
		// double[] x = new double[N];
		// double[] y = new double[N];
		// for (int i = 0; i < N; i++) {
		// x[i] = StdIn.readDouble();
		// y[i] = StdIn.readDouble();
		// }
		//
		// // compute t-statistic
		// double t = tstat(x, y);
		// StdOut.println(t);
	}
}
