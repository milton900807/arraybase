package com.arraybase;

import java.util.ArrayList;

import com.arraybase.modules.JoinModule;

public class GBJoinMnager {

	public static void join(String[] _args) {
		// java -jar gb.jar join /gne/totable/t5 as t5 /gne/totable/t6 as t6
		// on t5.wdir=t6.wdir
		// A way to join tables together.
		ArrayList<String> arguments = new ArrayList<String>();
		for (int i = 1; i < _args.length; i++) {
			arguments.add(_args[i]);
		}
		JoinModule j = new JoinModule();
		j.exec(arguments);

	}

}
