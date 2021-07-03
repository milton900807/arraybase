package com.arraybase.shell.cmds;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.db.NodeExistsException;
import com.arraybase.db.util.SourceType;
import org.apache.hadoop.fs.PathNotFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

public class CreateTableNodeCommand implements GBPlugin {
    // example
//	create table /comments/test --schema=orderid:int,comments:string
    public String exec(String command, String variable_kiiey) {
        String[] sp = command.split("\\s+");
        String table = sp[2];

        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        for (String sw : sp) {
            if (sw.startsWith("--")) {
                String[] split_option = sw.split("=");
                options.put(split_option[0], split_option[1]);
            }
        }

        String path = GB.pwd();
        if (table.startsWith("/")) {
            path = table;
        } else {
            path += "/" + table;
        }
        ABTable t = new ABTable(path);
        try {
            t.create(getSchema ( options ) );
        } catch (NodeExistsException e) {
            e.printStackTrace();
        }
        return "Complete";
    }

    public static LinkedHashMap<String, String> getSchema(LinkedHashMap<String, String> options) {
        LinkedHashMap<String, String> schema = new LinkedHashMap<String, String>();
        String value = options.get ( "--schema");
        if ( value != null && value.length() > 0 )
        {
//            v:int,a:string etc..
            String[] ps = value.split ( ",");
            for ( String p : ps ){

                String[] vp = p.split  ( ":");
                String var = vp[0];
                String typ = vp[1];
                schema.put ( var, typ );
            }
        }
        return schema;
    }


    public GBV execGBVIn(String cmd, GBV input) {
        // TODO Auto-generated method stub
        return null;
    }

}
