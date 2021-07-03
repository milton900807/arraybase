package com.arraybase.evolve;

import com.arraybase.ABTable;
import com.arraybase.tm.GRow;
import org.helm.notation2.exception.ParserException;
import org.helm.notation2.parser.notation.HELM2Notation;
import org.helm.notation2.parser.notation.connection.ConnectionNotation;
import org.helm.notation2.parser.notation.polymer.PolymerEntity;
import org.helm.notation2.parser.notation.polymer.PolymerNotation;
import org.helm.notation2.tools.HELM2NotationUtils;
import org.jdom2.JDOMException;

import java.util.HashMap;
import java.util.List;

//import static org.openscience.cdk.smiles.smarts.parser.SMARTSParserConstants.v;

public class FivePrimeChemistry implements EvolveFunction {
    String field_name = null;
    String params = null;
    ABTable table = null;

    public FivePrimeChemistry(String field_name, String params, ABTable t) {
        this.field_name = field_name;
        this.params = params;
        this.table = t;
    }

    public void eval(GRow gRow) {
        HashMap data = gRow.getData();
        String tmid = (String) data.get("TMID");
        String chemistry = (String) data.get(params);
        try {
            HELM2Notation helm2notation = HELM2NotationUtils.readNotation(chemistry);
            String threePrimeChain = pullFivePrime(helm2notation);
            if (threePrimeChain != null && threePrimeChain.length() > 0) {
                String v = threePrimeChain.replace(".", " ");
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
            }
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
//        System.out.println("-0---- chemistry : " + chemistry);
    }


    private static String pullFivePrime(HELM2Notation notation) {
        List<PolymerNotation> polymers = notation.getListOfPolymers();
        List<ConnectionNotation> polymer_connections = notation.getListOfConnections();
        for (PolymerNotation pol : polymers) {
            PolymerEntity poi = pol.getPolymerID();
            int numMonomers = pol.getListMonomers().size();
            if (poi.getId().equalsIgnoreCase("RNA1")) {
                for (ConnectionNotation cn : polymer_connections) {
                    String target = cn.getTargetId().getId();
                    String source = cn.getSourceId().getId();
                    String targetunit = cn.getTargetUnit();
                    String sourceunit = cn.getSourceUnit();
                    if (poi.getId().equalsIgnoreCase(target)) {
                        if (targetunit != null && targetunit.length() > 0) {
                            Integer sui = Integer.parseInt(targetunit.trim());
                            if ((sui - 2) <= 1 && (sui + 2) >= 1) {
                                return getChain(source, polymers);
                            }
                        }
                    } else if (poi.getId().equalsIgnoreCase(source)) {
                        if (sourceunit != null && sourceunit.length() > 0) {
                            Integer sui = Integer.parseInt(sourceunit.trim());
                            if ((sui - 2) <= 1 && (sui + 2) >= 1) {
                                return getChain(target, polymers);
                            }
                        }

                    }
                }
            }

        }
        return null;
    }

    private static String getChain(String target, List<PolymerNotation> polymers) {
        for (PolymerNotation pol : polymers) {
            if (pol.getPolymerID().getId().equalsIgnoreCase(target)) {
                return pol.toHELM2();
            }
        }
        return null;
    }
}
