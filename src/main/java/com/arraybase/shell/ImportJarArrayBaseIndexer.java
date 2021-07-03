package com.arraybase.shell;

import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class ImportJarArrayBaseIndexer implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
        System.out.println ( " command " + command );

        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
