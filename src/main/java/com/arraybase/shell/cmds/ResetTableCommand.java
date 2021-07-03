package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.TableManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by jmilton on 5/19/2015.
 */
public class ResetTableCommand implements GBPlugin {


    public String exec(String command, String variable_key) throws UsageException {


        String[] table_t = command.split("\\.");

        String table_name = table_t[0];
        String path = table_name;
        if ( !table_name.startsWith("/"))
            path = GB.pwd() + "/" + table_name;
        GB.print ( "Are you sure you want to remove all the data from this table?\n\t\t");
        Scanner sc = new Scanner( System.in);
        String resp = sc.next();
        resp = resp.toLowerCase();

        if ( resp.equalsIgnoreCase("Yes") || resp.equalsIgnoreCase("Y")){
            GB.print("Resetting.... ");
            return clear(path);
            } else
        {
            GB.print ( "Cancel reset.");
        }
        return null;
    }

    private String clear(String path) {


        NodeManager nm = new NodeManager();
        TNode n = nm.getNode(path);
        // create a new node
        String link = n.getLink();

        if (link == null) {
            GB.print(" No data associated with this target so can't reset this.");
            return " Nothing changed. ";
        }

        if (link != null) {
            String target = LAC.getTarget(n.getLink());
            GB.print("\tTable: " + target);
            try {
                ArrayList<GColumn> cols = GB.getGBTables().describeCore(target);
                // we need to sort this based on the table properties.
                if (cols == null || cols.size() <= 0) {
                    GB.print(" Schema is undefined for this target... nothing will be changed. ");
                    return "No schema defined. ";
                }


                int rows_count = GB.getGBTables().count(target);
                GB.print("Rows: " + rows_count);
                GB.print("\n");
                TableManager.delete(target);
                //TableManager.createSchema(cols, "test_user", path);
//                TableManager.reset(target);


            } catch (ConnectException e) {
                e.printStackTrace();
            }
            // create a new core with the same schema parameters


            //Map<String, String> np = nm.getNodePropertyMap(n.getNode_id());






        }
        return "Table reset.";
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
