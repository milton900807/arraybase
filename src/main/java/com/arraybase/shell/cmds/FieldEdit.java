package com.arraybase.shell.cmds;


import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tab.field.FieldAction;
import com.arraybase.tab.FieldActionFactory;
import com.arraybase.tab.field.FieldNotFoundException;
import com.arraybase.tab.field.NoFieldActionFound;
import com.arraybase.tab.field.TypeNotCorrect;

public class FieldEdit implements GBPlugin {

	public String exec(String command, String variable_key)
			throws UsageException {

		// something_table.myfield.tolowercase()
		String[] params = null;
		int first = command.indexOf('.');
		int second = command.indexOf('.', first+1);
		int end = command.indexOf('(');
		int ende = command.lastIndexOf(')');
		if (end > 0) {
			String p = command.substring(end + 1, ende);
			if (p != null) {
				p = p.trim();
				String[] splt = p.split(",");
				for (int in = 0; in < splt.length; in++) {
					splt[in] = splt[in].trim();
				}
				params = splt;
			}
		}
		if (end < 0)
			end = command.length();

		String target_table = command.substring(0, first);
		String target_field = command.substring(first + 1, second);
		String target_action = command.substring(second+1);

		if (target_table != null && target_field != null
				&& target_action != null) {
			target_table = target_table.trim();
			target_field = target_field.trim();
			target_action = target_action.trim();

			try {
				FieldAction fieldActions = FieldActionFactory.create(
						target_table, target_field, target_action);
				fieldActions.start();
			} catch (GBPathNotFoundException ep) {
				ep.printStackTrace();
				GB.print ( ep.getMessage() );
			} catch (NoFieldActionFound _nf) {
				_nf.printStackTrace();
				GB.print ( _nf.getMessage() );
			} catch (FieldNotFoundException e) {
				e.printStackTrace();
				GB.print ( e.getMessage() );
			} catch (TypeNotCorrect e) {
				GB.print ( e.getMessage() );
			}

		}

		return null;
	}

	public GBV execGBVIn(String cmd, GBV input) {

		return null;
	}

}
