package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.iterminal.c.ConsoleReader;
import com.arraybase.shell.iterminal.c.history.History;

import java.io.IOException;

/**
 * Created by jmilton on 3/16/2016.
 */
public class PreviousCommand implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

        History his = GB.getConsoleHistory();
        if ( his != null )
        {
            his.removeLast();
            ConsoleReader console = GB.getConsole();
            if ( console != null )
            {
                console.appendOnRedraw (""+his.get(his.size()-1));
            }


//            System.out.append((String) his.removeLast());
        }else
        {
            GB.print ( "No history available.");
        }

        // print the previous command
        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
