package com.arraybase.lac;

/**
 * Created by jmilton on 7/21/2015.
 */
public class VersionManager {


    public static String incrementCoreName(String corename) {

        int li = corename.lastIndexOf("__");
        if (li < 0) {
            return corename + "__1";
        }
        String digit = corename.substring(li + 2);
        if (digit == null || digit.trim().length() <= 0) {
            return corename + "__1";
        }else
        {
            try {
                Integer incr = Integer.parseInt(digit);
                incr++;

                String t = corename.substring(0, li);
                return t + "__" + incr;

            }catch (NumberFormatException ne )
            {
                ne.printStackTrace();
                return corename + "__1";
            }
        }


    }

}
