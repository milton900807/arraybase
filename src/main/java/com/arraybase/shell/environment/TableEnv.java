package com.arraybase.shell.environment;

import java.util.LinkedHashMap;

import com.arraybase.ABaseNode;
import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBPlugin;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.GBCommand;
import com.arraybase.shell.environment.table.cmds.ListFields;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;

public final class TableEnv extends PromptEnv {
	public TNode table = null;
	private LinkedHashMap<String, GBPlugin> cmds = new LinkedHashMap<String, GBPlugin> ();


	public static final String FIELD_PATH = "(" + GBRGX.PATH
			+ ")*\\s*[A-Za-z0-9_]*(\\.search)" + "\\s*\\(.*\\)\\s*"
			+ "((\\.set\\s*\\(.*\\)\\s*)|" + "(\\[" + GBRGX.field + "\\])+\\s*"
			+ ")\\s*(\\{([0-9_]*)\\-([0-9_]*)\\})*";
	
	public static final String field_name = "[A-Za-z0-9_]*";
	public static final String method_name = "[A-Za-z0-9_]*";

	public static final String field_method = field_name + "\\." + method_name + "\\s*\\(.*\\)";
	public static final String append_x = "add\\s*field\\s*" + "" + method_name + "\\s*="+GBRGX.SEARCH_STRING;


	public TableEnv ( TNode table ){
		this.table = table;
		init();
	}
	private void init()
	{
		cmds.put ( "listfields|list\\s*fields|fields|columns|desc", new ListFields(table) );
        cmds.put ( append_x, new AppendX(table));


//		fpkm.set(/isis/allspecies/fpkm.search(CELL_LINE:$CELL_LINE)[fpkm])
		cmds.put ( field_method, new FieldMethod ( table ));
	}
	public String toString ()
	{
		return "Table env";
	}
	public void updateCommandCenter ()
	{
		GB.getCommands().updateCommands(cmds);	
	}


    public static void main(String[] args )
    {
        NodeManager man = new NodeManager ();
        TNode node = man.getNode("/test/secproteins");
        TableEnv tv = new TableEnv (node);
		tv.updateCommandCenter();
		GBCommand command = new GBCommand();
		try {
			command.exec("add field field_name_test<-/ionis/expression/rpkm1.search($gene)", null);
		} catch (UsageException e) {
			e.printStackTrace();
		}
	}

	
}
