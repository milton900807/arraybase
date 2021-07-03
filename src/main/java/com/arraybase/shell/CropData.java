package com.arraybase.shell;

import com.arraybase.GBIO;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.cmds.RMWhere;

/**
 * Created by jmilton on 2/27/2016.
 */
public class CropData implements GBPlugin {


    public String exec(String command, String variable_key) throws UsageException {

        String[] sp = GBIO.parseParams(command);
        String path = GBIO.parsePath(command);
        for ( int i = 0; i < sp.length; i++)
        {
            sp[i]=sp[i].trim();
        }
        RMWhere rm = new RMWhere();
        String field = sp[0];
        try {
            double min = Double.parseDouble(sp[1]);
            double max = Double.parseDouble(sp[2]);
            String minw = field+":[* TO "+min+"]";
            String maxw = field+":["+max+" TO *]";


            rm.exec(path+".delete.("+minw+")", variable_key);
            rm.exec(path+".delete.("+maxw+")", variable_key);

            //System.out.println (" range : " + minw + "\t" + maxw );


        }catch ( NumberFormatException ne ){

        }





        return "don.";

    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
