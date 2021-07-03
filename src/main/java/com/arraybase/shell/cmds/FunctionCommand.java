package com.arraybase.shell.cmds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.GBVariables;
import com.arraybase.var.gbv.GBVPointer;
import com.arraybase.modules.UsageException;
import com.arraybase.qmath.ABFunction;
import com.arraybase.qmath.CalculationFailedException;
import com.arraybase.qmath.CalculationNotAvailableException;
import com.arraybase.qmath.FloatVar;
import com.arraybase.qmath.FunctionFactory;
import com.arraybase.shell.cmds.math.PairedTTest;
import com.arraybase.util.GBRGX;

public class FunctionCommand implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {

		if (command.matches(GBRGX.PAIRED_TTEST)) {
			PairedTTest pt = new PairedTTest();
			return pt.exec(command, variable_key);
		} else {
			// mean($variable_name);
			int s = command.indexOf('(');
			int f = command.lastIndexOf(')');
			String sub = command.substring(s + 1, f);
			sub = sub.trim();
			String fun_name = command.substring(0, s);
			fun_name = fun_name.trim();
			GBVariables gbv = GB.getVariables();
			Set<String> keys = gbv.getSets();
			for (String key : keys) {
				if (sub.equalsIgnoreCase("$" + key)) {
					GBV var = gbv.getVariable(key);
					if (var instanceof GBVPointer) {
						GBVPointer point = (GBVPointer) var;
						String commandstr = point.toString();
						fun_name += "|" + commandstr;
					}
				}
			}
			GB.exec(fun_name, null);
		}

		return "Command";
	}

	public GBV execGBVIn(String command, GBV input) {

		int s = command.indexOf('(');
		int f = command.lastIndexOf(')');
		String sub = command.substring(s + 1, f);
		sub = sub.trim();
		String fun_name = command.substring(0, s);
		fun_name = fun_name.trim();

		// -- this is where we left off.
		// NOTES: were running the following command:
		// fpkm_ct.search(gene:kras AND isis_cell_line:A549 AND
		// vis:true).set(ct,mean(primer_probes.search(CELL_LINE:$isis_cell_line
		// AND TARGET:$mtid)[CT]{0-100}))
		// command comes in with the following
		// mean(primer_probes.search(CELL_LINE:A549 AND TARGET:696)[CT]{0-100})
		// and fun_name == mean
		ABFunction function = FunctionFactory.create(fun_name);
		search2 search = new search2();
		String search_string = command.substring(s + 1, f);
		search_string = search_string.trim();
		GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> results = search
				.execGBVIn(search_string, null);
		if (results == null || empty(results)) {
			return null;
		}
		function.setSearch(results);
		try {
			float mean_value = function.calculateValue();
			FloatVar mv = new FloatVar(mean_value);
			return mv;
		} catch (CalculationNotAvailableException _e) {
			GB.print("  failed to produce a working function for "
					+ search_string);
		} catch (CalculationFailedException e) {
			GB.print("  Error in the meanvalue calculation " + command);
		}

		return null;
	}

	private boolean empty(
			GBV<Iterator<ArrayList<LinkedHashMap<String, Object>>>> results) {
		Iterator<ArrayList<LinkedHashMap<String, Object>>> iincr = results
				.get();
		ArrayList<LinkedHashMap<String, Object>> incr = iincr.next();
		results.reset();
		return incr.size() <= 0;
	}

}
