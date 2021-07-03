package com.arraybase.flare;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jmilton on 5/11/2015.
 */
public class CurrentTimeForSolr {
    private static SimpleDateFormat sf = new SimpleDateFormat( "YYYY-MM-dd'T'hh:mm:ss'Z'");
    public static Date time()
    {
        Date d = new Date();
        return d;
    }
    public static String timeStr()
    {
        Date d = new Date();
        return sf.format(d);
    }
}
