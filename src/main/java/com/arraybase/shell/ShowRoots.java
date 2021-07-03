package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

/**
 * Created by jmilton on 3/14/2016.
 */
public class ShowRoots implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
        String[] roots = GB.getRoots();
        String t = "";
        for (String r : roots) {
            GB.print(r);
            t += r;
        }
        return "" + t;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
