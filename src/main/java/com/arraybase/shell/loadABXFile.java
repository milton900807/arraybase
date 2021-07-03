package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class loadABXFile implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
        // 05.09. this is where I left off.. we need to load the add field file
        String[] line =command.split ( "\\s+");
        if ( line.length == 3 )
        {
            String table = line[2];
            System.out.println( " table " + table );
        }



        return null;
    }
    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
