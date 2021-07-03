package com.arraybase.qmath.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.GBSearchIterator;

public class SimpleLinearRegression {

	double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
	double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
	double rss = 0.0; // residual sum of squares
	double ssr = 0.0; // regression sum of squares

	int n = 0;
	public void calculate(GBSearchIterator gbit, String xfield, String yfield) {
		while (gbit.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> search_set = gbit.next();
			for (LinkedHashMap<String, Object> list : search_set) {
				Object obx = list.get(xfield);
				Object oby = list.get(yfield);
				if (obx instanceof Number && oby instanceof Number) {
					double x = ((Number) obx).doubleValue();
					double y = ((Number) oby).doubleValue();
					sumx += x;
					sumx2 += x * x;
					sumy += y;
					n++;
				}
			}
		}
		double xbar = sumx / n;
		double ybar = sumy / n;
		gbit.reset();
		while (gbit.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> search_set = gbit.next();
			for (LinkedHashMap<String, Object> list : search_set) {
				Object obx = list.get(xfield);
				Object oby = list.get(yfield);
				if (obx instanceof Number && oby instanceof Number) {
					double x = ((Number) obx).doubleValue();
					double y = ((Number) oby).doubleValue();
					xxbar += (x - xbar) * (x - xbar);
					yybar += (y - ybar) * (y - ybar);
					xybar += (x - xbar) * (y - ybar);
				}
			}
		}
		
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
		System.out.println("y   = " + beta1 + " * x + " + beta0);
		int df = n - 2;
		
		gbit.reset();
		while (gbit.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> search_set = gbit.next();
			for (LinkedHashMap<String, Object> list : search_set) {
				Object obx = list.get(xfield);
				Object oby = list.get(yfield);
				if (obx instanceof Number && oby instanceof Number) {
					double x = ((Number) obx).doubleValue();
					double y = ((Number) oby).doubleValue();
					double fit = beta1 * x + beta0;
					rss += (fit - y) * (fit - y);
					ssr += (fit - ybar) * (fit - ybar);
				}
			}
		}
		double R2 = ssr / yybar;
		double svar = rss / df;
		double svar1 = svar / xxbar;
		double svar0 = svar / n + xbar * xbar * svar1;
		System.out.println("R^2                 = " + R2);
		System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
		System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
		svar0 = svar * sumx2 / (n * xxbar);
		System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

		System.out.println("SSTO = " + yybar);
		System.out.println("SSE  = " + rss);
		System.out.println("SSR  = " + ssr);
	}

	public void update(Map<Double, Double> data) {
		int MAXN = 1000;
		int n = 0;
		double[] x = new double[MAXN];
		double[] y = new double[MAXN];

		// first pass: read in data, compute xbar and ybar
		Set<Double> s = data.keySet();
		for (Double xv : s) {
			x[n] = xv;
			y[n] = data.get(xv);
			sumx += x[n];
			sumx2 += x[n] * x[n];
			sumy += y[n];
			n++;
		}
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;

		// print results
		System.out.println("y   = " + beta1 + " * x + " + beta0);

		// analyze results
		int df = n - 2;
		double rss = 0.0; // residual sum of squares
		double ssr = 0.0; // regression sum of squares
		for (int i = 0; i < n; i++) {
			double fit = beta1 * x[i] + beta0;
			rss += (fit - y[i]) * (fit - y[i]);
			ssr += (fit - ybar) * (fit - ybar);
		}
		double R2 = ssr / yybar;
		double svar = rss / df;
		double svar1 = svar / xxbar;
		double svar0 = svar / n + xbar * xbar * svar1;
		System.out.println("R^2                 = " + R2);
		System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
		System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
		svar0 = svar * sumx2 / (n * xxbar);
		System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

		System.out.println("SSTO = " + yybar);
		System.out.println("SSE  = " + rss);
		System.out.println("SSR  = " + ssr);
	}
}