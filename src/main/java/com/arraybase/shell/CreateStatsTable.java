package com.arraybase.shell;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

/**
 * Created by jmilton on 5/19/2015.
 */
public class CreateStatsTable implements GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {


        GB.createStats();
        return "done";

    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
