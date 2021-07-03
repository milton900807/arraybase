package com.arraybase.shell.environment;

import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.tree.TNode;

import java.util.ArrayList;

/**
 * Created by jmilton on 3/14/2016.
 */
public class AppendX implements GBPlugin {


    private TNode table = null;
    private ArrayList<GColumn> cols = null;

    public AppendX(TNode table) {




    }


    public String exec(String command, String variable_key) throws UsageException {
        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
