package org.monomer.abplugs;


import com.arraybase.plugin.ABQFunction;
import com.arraybase.plugin.ABQParams;

//export=orderid, isisno, chemistry=fetch(http://oligodb:8080/oligos/{{isisno}}->[0].helm), conjugates=org.monomer.abplugs.HELMConjugateExtractor.eval([0].helm), datesynstarted, synthesis_status, datesyncompleted, in_plate, release_comments, release_lotno, timestamp, requesterid, projectid, whyordered, synscale, synscaleunit, requestedpurification, reqpercentpurity, desiredcompletiondate, delivertoid, plateno, wellno, status, mol_targetid, official_name, official_symbol
public class HELMConjugateExtractor implements ABQFunction {
        public String eval(ABQParams obj) {
            String helm = obj.getString("chemistry");

            if (helm == null || helm.length() <= 0) {
                helm = obj.getString("helm");
            }

            if (helm == null || helm.length() <= 0)
                return null;
            if (!helm.contains("$")) {
                return null;
            }
        // get the chain sequence:
        int end = helm.indexOf('$');
        String t = helm.substring(0, end);
        String[] chains = t.split("\\|");
        String chems = "";
        for (String c : chains) {
            if (c.startsWith("CHEM")) {
                String id = parsePolymerIDFromChain(c);
                int threeprime = determineThreePrimeIndex(id, helm);
//                System.out.println(" Tree prime: " + threeprime);
                int fiveprime = determineFivePrimeIndex(id, helm);
//                System.out.println(" Five prime: " + fiveprime);
                if (fiveprime > 0) {
                    chems += " FivePrime_" + solrify(c);
                }
                if (threeprime > 0) {
                    chems += " ThreePrime_" + solrify(c);
                }
            }
        }

        if (chems != null && chems.length() > 0) {
            chems = chems.trim();
        } else {
            return null;
        }

        return chems;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public float evalFloat(ABQParams obj) {
        return 0;
    }

    private String solrify(String c) {

        String t = c;
        int it = t.indexOf('{');
        int ft = t.indexOf('}');
        if (it > 0) {
            t = t.substring(it + 1, ft);
        }
        t = t.replaceAll("\\(", " ");
        t = t.replaceAll("\\)", " ");
        t = t.replaceAll("\\[", " ");
        t = t.replaceAll("\\]", " ");
        t = t.replaceAll("\\.", " ");
        return t.trim();
    }

    private int determineThreePrimeIndex(String unit, String helm) {
        //RNA1,CHEM1,30:R2-1:R1|CHEM1,CHEM2,1:R2-1:R1
        String connection_string = getConnectionString(helm);
        if (connection_string.contains("|")) {
            String[] con = connection_string.split("\\|");
            // we need to get the index given the chem-label above
            for (String c : con) {
                int val = calculateThreePrimeIndex(c, unit, helm);
                System.out.println(" unit " + unit);
                System.out.println(" helm " + helm);
                System.out.println(" val " + val);
                if (val != 0)
                    return val;
            }
        } else {
            return calculateThreePrimeIndex(connection_string, unit, helm);
        }
        return -1;
    }

    private int determineFivePrimeIndex(String unit, String helm) {
        //RNA1,CHEM1,30:R2-1:R1|CHEM1,CHEM2,1:R2-1:R1
        String connection_string = getConnectionString(helm);
        if (connection_string.contains("|")) {
            String[] con = connection_string.split("\\|");
            // we need to get the index given the chem-label above
            for (String c : con) {
                int val = calculateFivePrimeIndex(c, unit, helm);
                if (val != 0)
                    return val;
            }
        } else {
            return calculateFivePrimeIndex(connection_string, unit, helm);
        }
        return -1;
    }

    public int calculateThreePrimeIndex(String c, String unit, String helm) {
        String[] units = c.split(",");
        for (int t = 0; t < units.length; t++) {
            if (units[t].toUpperCase().equalsIgnoreCase(unit)) {
                for (int u = 0; u < units.length; u++) {
                    // {{ FIRST CHECK TO SEE IF THIS IS ATTACHED TO THE RNA STRAND }}
                    if (units[u].toUpperCase().startsWith("RNA")) {
                        int monomer_index = getIndex(u, units[2]);
                        if (monomer_index > 1) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (units[u].toUpperCase().startsWith("CHEM") && (!units[u].equals(unit))) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    public int calculateFivePrimeIndex(String c, String unit, String helm) {
        String[] units = c.split(",");
        for (int t = 0; t < units.length; t++) {
            if (units[t].toUpperCase().equalsIgnoreCase(unit)) {
                for (int u = 0; u < units.length; u++) {
                    // {{ FIRST CHECK TO SEE IF THIS IS ATTACHED TO THE RNA STRAND }}
                    if (units[u].toUpperCase().startsWith("RNA")) {
                        int monomer_index = getIndex(u, units[2]);
                        if (monomer_index == 1) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else if (units[u].toUpperCase().startsWith("CHEM") && (!units[u].equals(unit))) {
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

    private Integer getIndex(int i, String unit) {
        //RNA1,CHEM1,30:R2-1:R1|CHEM1,CHEM2,1:R2-1:R1
        String[] un = unit.split("-");
        String[] t = un[i].split(":");
        Integer gt = Integer.parseInt(t[0]);
        return gt;
    }

    public static boolean isHELM(String _string) {
        if (_string.contains("{") && _string.contains("}") && _string.contains("$")) {
            return true;
        } else {
            return false;
        }
    }


    public static String[] parseChains(String chemical_notation) {
        int chainindex = chemical_notation.indexOf('$');
        String sub = chemical_notation.substring(0, chainindex);
        if (sub.contains("|")) {
            return sub.split("\\|");
        } else {
            String[] s = new String[1];
            s[0] = sub.trim();
            return s;
        }
    }

    public static String getConnectionString(String helm) {
        int st = helm.indexOf('$');
        int en = helm.indexOf('$', st + 1);
        String connection_string = helm.substring(st + 1, en);
        return connection_string;
    }

    public static String parsePolymerIDFromChain(String c) {
        int i = c.indexOf('{');
        String t = c.trim();
        return t.substring(0, i);
    }

    public static String parseChainFromPolymer(String c) {
        int i = c.indexOf('{');
        int f = c.indexOf('}');
        String t = c.substring(i + 1, f);
        return t.trim();
    }

    public static String parseMonomer(String unit, int monomer_index, String helm) {
        String[] chains = parseChains(helm);
        for (String c : chains) {
            if (c.startsWith(unit)) {
                String monomerlist = parseChainFromPolymer(c);
                String monomerindex = getMonomerInChain(monomerlist, monomer_index);
                return monomerindex;

            }
        }
        return null;

    }

    private static String getMonomerInChain(String monomerlist, int monomer_index) {

        monomer_index = monomer_index - 1;

        monomerlist = monomerlist.replaceAll("\\(", ".");
        monomerlist = monomerlist.replaceAll("\\)", ".");
        String[] sp = monomerlist.split("\\.");
        if (monomer_index < sp.length)
            return sp[monomer_index];
        else
            return null;

    }

    public static void main(String[] test) {
        HELMConjugateExtractor h = new HELMConjugateExtractor();
        ABQParams abq = new ABQParams();
        abq.append("helm", "RNA1{p.[cet]([m5C])[sp].[cet]([m5C])[sp].[cet](G)[sp].d([m5C])[sp].d(T)[sp].d([m5C])[sp].d([m5C])[sp].d(T)[sp].d(G)[sp].d([m5C])[sp].d(A)[sp].d(A)[sp].d([m5C])[sp].[cet](T)[sp].[cet](G)[sp].[cet]([m5C])}|CHEM1{[3Alex594N]}$CHEM1,RNA1,1:R1-1:R1$$$");
        abq.append("helm", "RNA1{p.[cet]([m5C])[sp].[cet]([m5C])[sp].[cet](G)[sp].d([m5C])[sp].d(T)[sp].d([m5C])[sp].d([m5C])[sp].d(T)[sp].d(G)[sp].d([m5C])[sp].d(A)[sp].d(A)[sp].d([m5C])[sp].[cet](T)[sp].[cet](G)[sp].[cet]([m5C])}|CHEM1{[3Alex594N]}$CHEM1,RNA1,1:R1-1:R1$$$");
        h.eval(abq);
    }
}
