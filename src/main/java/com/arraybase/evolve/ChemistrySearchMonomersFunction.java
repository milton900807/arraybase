package com.arraybase.evolve;

import com.arraybase.ABTable;
import com.arraybase.tm.GRow;

import java.util.HashMap;

public class ChemistrySearchMonomersFunction implements EvolveFunction {
    String field_name = null;
    String params = null;
    ABTable table = null;
    public ChemistrySearchMonomersFunction(String field_name, String params, ABTable t) {
        this.field_name = field_name;
        this.params = params;
        this.table = t;
    }
    public void eval(GRow gRow) {
        HashMap data = gRow.getData();
        String tmid = (String) data.get("TMID");
        String chemistry = (String) data.get(params);
        if ( chemistry != null && (chemistry.startsWith("RNA") || chemistry.startsWith("PEPTIDE") || chemistry.startsWith("CHEM"))){
            String v = chemistry.replace(".", " ");
            v = v.replace("{", " ");
            v = v.replace("|", " ");
            v = v.replace("(", " ");
            v = v.replace("[", " ");
            v = v.replace(")", " ");
            v = v.replace("]", " ");
            v = v.replace("}", " ");
            v = v.replace("$", " ");
            v = v.trim();
            table.set(tmid, this.field_name, v, false);
            System.out.println ("-0---- chemistry : " + chemistry );
        }
    }
}
