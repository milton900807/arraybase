package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

/**
 * Created by jmilton on 3/14/2016.
 */
public class ListPath implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

        String[] sp = command.split(" ");
        if ( sp.length == 2 )
        {
            String[] re = GBNodes.listPath(sp[1].trim());
            GB.print ( "   ");
//            for ( String r : re )
//            {
//                GB.print ( r );
//            }
        }else
        {
            String[] re = GBNodes.listPath(GB.pwd());
            GB.print ( "   ");
//            for ( String r : re )
//            {
//                GB.print ( r );
//            }

        }


        return "";
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
