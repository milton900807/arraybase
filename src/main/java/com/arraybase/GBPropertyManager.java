package com.arraybase;

import java.util.ArrayList;
import java.util.Map;

import com.arraybase.modules.UsageException;
import com.arraybase.db.util.SourceType;
import com.arraybase.tm.tree.TNode;

public class GBPropertyManager {

	public static void set(String[] _args) {
		if (_args.length < 2)
			GB.printUsage("You need to specific a set type.----");
		String type = _args[1];
		try {

			GB.print ( " Getting the GB.SET module for type : " + type );

			GBModule ins = Mod.getModule(GB.SET, type, null);
			ArrayList<String> l = new ArrayList<String>();
			for (int i = 2; i < _args.length; i++) {
				l.add(_args[i]);
			}
			ins.exec(l);
		} catch (UsageException e) {
			GB.print(e.getLocalizedMessage());
		} catch (GBModuleNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void add(String[] _args) {
		if (_args.length < 3) {
			GB.printUsage("EXAMPLE: gb add donaldm fields=1 types=2 ignore=3 46239h2010.txt /gne/research/my_new_table");
			return;
		}
		String user_name = _args[1];
		String local_file = _args[_args.length - 2];
		String path = _args[_args.length - 1];
		Map<String, String> vals = GB.parseVals(_args);
		String delim = vals.get("delim");
		if (delim == null)
			delim = "\\t";

		String lac = GBTableLoader.mkTable(user_name, local_file, delim, path,
				vals);
		GB.print("Creating path " + path);
		TNode node = GBNodes.mkNode(user_name, path, lac, SourceType.DB);
		if (node != null)
			GB.print("[" + node.getNode_id() + "] Node created. ");
		else
			GB.print(" Node not created ");

	}
}
