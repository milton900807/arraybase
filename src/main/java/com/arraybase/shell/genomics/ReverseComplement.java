package com.arraybase.shell.genomics;

import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

public class ReverseComplement implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {

        String[] st = command.split("\\s+");
        if (st.length > 1) {
            String sequence = st[1];
            sequence.toUpperCase();
            char[] s = sequence.toCharArray();
            String rc = "";
            for (char c : s) {
                if (c == 'A') {
                    rc += 'T';
                } else if (c == 'T') {
                    rc += 'A';
                } else if (c == 'C') {
                    rc += 'G';
                } else if (c == 'G') {
                    rc += 'C';
                }
            }
            char[] cc = rc.toCharArray();
            String rrc = "";
            for (int i = cc.length-1; i >= 0; i--) {
                rrc += cc[i];
            }


            System.out.println(rrc);

        }
        return "--";

    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
