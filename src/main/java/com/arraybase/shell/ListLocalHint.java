package com.arraybase.shell;


import com.arraybase.GB;
import com.arraybase.GBNodes;

public class ListLocalHint extends CommandOption {

	public String toString() {
		String buf = getCurrentBuffer();
		if (buf != null) {
			
			String path = GB.pwd();
			String[] va = GBNodes.listPath(path);
			String st = "";
			for ( String p : va ){
				st += p + "\n";
			}
			return st + "\n";
		}
		return "";
	}
}
