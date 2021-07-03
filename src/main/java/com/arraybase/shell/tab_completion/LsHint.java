package com.arraybase.shell.tab_completion;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.shell.CommandOption;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tables.GBTables;

public class LsHint extends CommandOption {

	String st = null;
	String nue_buff=null;

	public LsHint(String st) {
		this.st = st;
		if (this.st != null)
			this.st = this.st.trim();
	}

	public LsHint() {
	}
	public String getNewBufferCommand(){
		
		update ();
		if ( st != null && st.length() > 0 && nue_buff != null )
			nue_buff = st + " " + nue_buff;
		
		return nue_buff;
	}
	private void update ()
	{
		nue_buff = null;
		String buf = getCurrentBuffer();
		buf = buf.trim();
		String[] sp = buf.split ( " ");
		if ( sp.length == 2 )
		{
			buf = sp[1];// this implies that we have a command looking for arguments in the directory.
			// e.g. desc $argument
		}
		
		
		String start = buf.trim();

		String _p = GB.pwd();
		GBNodes nodes = GB.getNodes();
		String[] re = nodes.getNodes(_p);
		if ( re == null ){
			return;
		}
		ArrayList<String> ls = new ArrayList<String>();
		for (String p : re) {
			String test = p.trim().toLowerCase();
			if (test.startsWith(start.toLowerCase())) {
				ls.add(p);
			}
		}
		
		
		if (ls.size() == 1) {
			//res = ls.get(0);
			nue_buff = ls.get ( 0 );
		} else if (ls.size() > 1) {

			int index = 0;
			String first = getshortest ( ls );
			
			
			String cm = first.charAt(index++) + "";
			boolean allpass = true;
			while (allpass && index < first.length()) {
				for (String p : ls) {
					// res += p + "\n";
					if (!p.startsWith(cm)) {
						allpass = false;
					}
				}
				if ( allpass )
					cm += first.charAt(index++);
			}
			nue_buff=  cm;
		}

	}

	private String getshortest(ArrayList<String> ls) {
		String go = null;
		int maxlength = 0;
		for ( String l : ls ){
			if ( l.length()> maxlength){
				go = l;
				maxlength = l.length();
			}
		}
		return go;
	}

	public String toString() {
		String buf = getCurrentBuffer();
		String res = "";
		if (buf != null) {
			String start = buf.trim();
			if (start.startsWith("/")) {
				return GB.ls(start);
			} else {

				String _p = GB.pwd();
				GBNodes nodes = GB.getNodes();
				String[] re = nodes.getNodes(_p);
				if (re == null) {
					return "";
				}
				ArrayList<String> ls = new ArrayList<String>();
				for (String p : re) {
					String test = p.trim().toLowerCase();
					if (test.startsWith(start.toLowerCase())) {
						res += p + "\n";
					}
				}
			}
		}
		return res.trim();
	}
}
