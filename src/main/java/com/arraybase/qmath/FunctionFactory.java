package com.arraybase.qmath;


public class FunctionFactory {
	public static ABFunction create(String fun_name) {
		fun_name = fun_name.toLowerCase();
		if ( fun_name.equalsIgnoreCase(ABFunction.MEAN)){
			return new MeanFunction ( );
		}
		return null;
	}

}
