package com.arraybase.qmath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.arraybase.GB;
import com.arraybase.GBIO;
import com.arraybase.GBUtil;
import com.arraybase.util.GBRGX;

public class EvalOperation extends ABOperation {

	
	public EvalOperation(ABOperation input, String params, ArrayList<String> fields, int[] index) {
		super(input, params, fields, index);
	}

	public Iterator<ArrayList<LinkedHashMap<String, Object>>> exec() {
		// default operation does not take in input
		// default operation does not push to output
		// search2 search = new search2 ();
		// search.exec(target.toString()+".search("+ param + ")", null);
		ABOperation in = getItarget();
		String out = in.getOtarget();
		String param = getParam();
		String searchString = param;
		
		ArrayList<String> operators = parsOperators ( param );
		
		Iterator<ArrayList<LinkedHashMap<String, Object>>> values = in.exec();
		ArrayList<String> fields = GBIO.parseFieldNames(param);
		double sum = 0d;
		double index = 0;
		int count = 0;
		while (values.hasNext()) {
			ArrayList<LinkedHashMap<String, Object>> v = values.next();
			for (Map<String, Object> vmap : v) {
				String string_value = "";
				
				int field_count = 0;
				for (String f : fields) {
					Object ob = vmap.get(f);
					Double d = GBUtil.toDouble(ob);
					if (d != null) {
						string_value += ( d );
						index++;
					}else
					{
						GB.print ( " WARNING: " + f + " is not a valid number at index : " + count);
					}
					if (field_count <operators.size())
						string_value+= operators.get(field_count++);
				}
				
				ScriptEngineManager manager = new ScriptEngineManager();
			    ScriptEngine engine = manager.getEngineByName("js");        
			    try {
					Object result = engine.eval(string_value);
					System.out.println ( " [" + result + "]" );
			    
			    } catch (ScriptException e) {
					e.printStackTrace();
				}
				
				
			}
		}
		Double mean = sum / index;
		
		
		return null;
	}

	private ArrayList<String> parsOperators(String param) {
		String[] sp = param.split(GBRGX.TABLE_FIELD);
		ArrayList<String> list = new ArrayList<String> ();
		for ( String s : sp ){
			if ( s != null && s.length()>0 )
				list.add ( s );
		}
		return list;
	}

}
