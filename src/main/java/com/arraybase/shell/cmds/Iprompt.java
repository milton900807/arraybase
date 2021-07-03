package com.arraybase.shell.cmds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.GBCommand;
import com.arraybase.shell.iterminal.c.ConsoleReader;
import com.arraybase.shell.iterminal.c.autocmplt.Completer;
import com.arraybase.tm.GColumn;

public class Iprompt implements GBPlugin {
	public String exec(String c, String variable_key) throws UsageException {

		// this is a search and replace.
		String search_ = c;
		int ti = search_.indexOf('.');

		int t2 = search_.indexOf('(');
		int t3 = search_.lastIndexOf(')');
		String target = "";
		if (ti > 0) {
			if (t2 > 0 && t3 > 0)
				target = search_.substring(0, ti);
			else {
				String sub = search_.substring(0, ti);
				if (sub.startsWith("/"))
					target = sub;
				else
					target = GB.pwd() + "/" + sub;
			}
		}
		if (target.endsWith("/")) {
			target = target.substring(0, target.length() - 1);
		}

		try {
			ArrayList<GColumn> cols = GB.describeTable(target);
			// get the taarget
			final ConsoleReader reader = new ConsoleReader(
					new BufferedInputStream(System.in),
					new BufferedOutputStream(System.out));
			// final ConsoleReader reader = new ConsoleReader( );
			reader.setPrompt("ABi > ");
			reader.addCompleter(new Completer() {
				public int complete(String buffer, int cursor,
						List<CharSequence> candidates) {
					GBCommand _cc_ = GB.getCommands();
					_cc_.printHint(buffer, reader);
					try {
						reader.redrawLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return 1;
				}
			});
			int i = 0;
			PrintWriter out = new PrintWriter(System.out);
			String line = "";
			boolean INTERACTIVE = true;
			LinkedHashMap<String, Object> list = new LinkedHashMap<String, Object>();
			while (line != null && INTERACTIVE) {
				out.flush();
				Character mask = null;
				String trigger = null;
				// If we input the special word then we will mask
				// the next line.
				if (line.equalsIgnoreCase("quit")
						|| line.equalsIgnoreCase("exit")) {
					break;
				}
				GB.print ( ""
						+ "To exit iprompt without commiting at any time type *exit*"
						+ "\n ");
				if ( cols == null || cols.size() <= 0 )
				{
					GB.print("The table does not have a schema.  Please create this first." );
					return null;
				}
				

				for (GColumn column : cols) {

					GB.print("---------------(" + column.getType() + ")  "
							+ column.getName() + "---------------");
					line = reader.readLine();
					if (line == null) {
						line = "";
					}
					if ( line.equals("*exit*") )
					{
						INTERACTIVE = false;
						break;
					}
					
					list.put(column.getName(), line.trim());
				}
				GB.print("[C] to commit. [Enter] to add another. [x] to end interaction without committing.");
				line = reader.readLine();
				if (line == null || line.length() <= 0) {
				} else {
					if (line.equalsIgnoreCase("c")) {
						commit(target, list);
					} else if (line.equalsIgnoreCase("x")) {
						INTERACTIVE = false;
					}
				}

			}
		} catch (ConnectException _e) {
			_e.printStackTrace();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return null;
	}

	private void commit(String path, LinkedHashMap<String, Object> list) {
		ABTable table = new ABTable(path);
		table.append(list);
	}

	public GBV execGBVIn(String c, GBV input) throws UsageException {
		// this is a search and replace.
		String search_ = c;
		int ti = search_.indexOf('.');
		int t2 = search_.indexOf('(');
		int t3 = search_.lastIndexOf(')');
		String target = "";
		if (ti > 0) {
			target = search_.substring(0, ti);
		}
		// target is defined at this point
		String path = GB.pwd() + "/" + target;

		return null;
	}

}
