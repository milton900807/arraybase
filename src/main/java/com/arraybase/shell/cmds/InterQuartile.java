package com.arraybase.shell.cmds;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Double.MAX_EXPONENT;
import static java.lang.Double.MIN_EXPONENT;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Math.getExponent;

import java.util.ArrayList;
import java.util.HashMap;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBSearch;
import com.arraybase.GBV;
import com.arraybase.search.ABaseResults;
import com.arraybase.tm.GRow;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBLogger;

public class InterQuartile implements GBPlugin {

	private static GBLogger log = GBLogger.getLogger(InterQuartile.class);

	public String exec(String command, String variable_key) {
		if (command.contains(".interquartile") || command.contains(".iqr")
				|| command.contains(".iqrange"))
			return q(command, variable_key);
		return "Cannot find mean for this";
	}

	private String q(String command, String variable_key) {
		int table = command.indexOf(".interquartile");
		if (table <= 0)
			table = command.indexOf(".iqr");
		if (table <= 0)
			table = command.indexOf(".iqrange");

		String ttable = command.substring(0, table);
		int index = command.indexOf('(');
		int index2 = command.lastIndexOf(')');
		String params = command.substring(index + 1, index2);
		String temp2 = command.substring(0, index);
		temp2 = temp2.trim();
		ArrayList<String> fields = new ArrayList<String>();
		if (temp2.contains(",")) {
			String[] v = params.split(",");
			if (v == null || v.length <= 0) {
				v = new String[1];
				v[0] = params;
			}
			for (String s : v) {
				if (s != null && s.length() > 0)
					fields.add(s.trim());
			}
		} else
			fields.add(params.trim());
		String path = GB.pwd() + "/" + ttable.trim();
		TNode node = GB.getNodes().getNode(path);
		if (node == null) {
			GB.print("Node with name " + path + " is not available");
			return null;
		}
		double q25 = getQuartile(path,
				fields.toArray(new String[fields.size()]), 0.25);
		double q75 = getQuartile(path,
				fields.toArray(new String[fields.size()]), 0.75);

		String s = "Quartile: [" + q25 + " " + q75 + "]";
		GB.print(s);
		return s;
	}

	public double getQuartile(String path, String[] ff, double k) {
		double qValue = 0;
		try {
			ABaseResults res = GBSearch.select(path, ff, "*:*", 0, 1, "" + ff[0]
					+ " desc");
			int n = res.getTotalHits()+1;
			double first_index = (k * n);
			GB.print(" K  " + k + " n=" + n + " first_index: " + first_index);
			double remainder = first_index % 1;
			GB.print(" Remainder: " + remainder);
			int findex = (int) Math.floor(first_index)-1;
			GB.print(" Floored index: " + findex);
			if (remainder == 0) {
				ABaseResults firstq = GBSearch.select(path, ff, "*:*", findex, 1, ""
						+ ff[0] + " asc");

				ArrayList<GRow> row = firstq.getValues();
				GRow r = row.get(0);
				HashMap data = r.getData();
				Number d = (Number) data.get(ff[0]);
				qValue = d.doubleValue();
			} else {
				ABaseResults firstq = GBSearch.select(path, ff, "*:*", findex, 2,
						"" + ff[0] + " asc");
				ArrayList<GRow> row = firstq.getValues();
				GRow r = row.get(0);
				GRow r1 = row.get(1);
				HashMap data = r.getData();
				HashMap data1 = r1.getData();
				Number d = (Number) data.get(ff[0]);
				Number d1 = (Number) data.get(ff[0]);
				qValue = d.doubleValue()
						+ ((d1.doubleValue() - d.doubleValue()) * remainder);
			}

		} catch (Exception _e) {
			_e.printStackTrace();
		}
		return qValue;
	}

	public static void main(String[] _args) {
		InterQuartile tq = new InterQuartile();
		tq.exec("isis/search/micro_rna.interquartile(conservation)", "");
	}

	public GBV execGBVIn(String cmd, GBV input) {
		return null;
	}

	static final long SIGNIFICAND_MASK = 0x000fffffffffffffL;
	static final long EXPONENT_MASK = 0x7ff0000000000000L;
	static final long SIGN_MASK = 0x8000000000000000L;
	static final int SIGNIFICAND_BITS = 52;
	static final int EXPONENT_BIAS = 1023;
	static final long IMPLICIT_BIT = SIGNIFICAND_MASK + 1;

	public boolean isDoubleInt(double value) {
		return isFinite(value)
				&& (value == 0.0 || SIGNIFICAND_BITS
						- Long.numberOfTrailingZeros(getSignificand(value)) <= getExponent(value));
	}

	static boolean isFinite(double d) {
		return getExponent(d) <= MAX_EXPONENT;
	}

	static long getSignificand(double d) {
		checkArgument(isFinite(d), "not a normal value");
		int exponent = getExponent(d);
		long bits = doubleToRawLongBits(d);
		bits &= SIGNIFICAND_MASK;
		return (exponent == MIN_EXPONENT - 1) ? bits << 1 : bits | IMPLICIT_BIT;
	}
}
