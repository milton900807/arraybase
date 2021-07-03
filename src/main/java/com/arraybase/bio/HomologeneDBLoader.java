package com.arraybase.bio;

import com.arraybase.ABTable;
import com.arraybase.NodeWrongTypeException;
import com.arraybase.db.NodeExistsException;
import gov.nih.nlm.ncbi.*;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class HomologeneDBLoader {


    public static void main(String[] args) {

        // load the
        File file = new File("C:/Users/jmilton/dev/homologs/homologene.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(HGEntrySet.class);


            XMLInputFactory xif = XMLInputFactory.newFactory();
//            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader xsr = xif.createXMLStreamReader(new StreamSource(file));

            Unmarshaller un = jaxbContext.createUnmarshaller();
            HGEntrySet hg = (HGEntrySet) un.unmarshal(xsr);

            HGEntrySet.HGEntrySetEntries st = hg.getHGEntrySetEntries();
            List<HGEntry> l = st.getHGEntry();

            ABTable table = new ABTable("/test/homolog8");

            HashMap<String, String> schema = new HashMap<String, String>();
            schema.put("hg_id", "sint");
            schema.put("alias", "text");
            schema.put("symbol", "text");
            schema.put("locus", "text");
            schema.put("gene_id", "text");
            try {
                if (!table.exists())
                    table.create(schema);
            } catch (NodeExistsException e1) {
                e1.printStackTrace();
            } catch (NodeWrongTypeException e1) {
                e1.printStackTrace();
            }


            ArrayList<LinkedHashMap<String, Object>> alist = new ArrayList<LinkedHashMap<String, Object>>();
            for (HGEntry e : l) {
                LinkedHashMap<String, Object> hgentry = new LinkedHashMap<>();
                System.out.println(" e " + e.getHGEntryHgId());
                HGEntry.HGEntryGenes genes = e.getHGEntryGenes();
                List<HGGene> hgg = genes.getHGGene();
                String alias = "";
                String symbol = "";
                String geneids = "";
                String locus_str = "";

                HashMap<String, String> symbolList = new HashMap<String, String> ();


                for (HGGene gen : hgg) {
                    HGGene.HGGeneAliases aliases = gen.getHGGeneAliases();
                    if (aliases != null) {
                        List<String> li = aliases.getHGGeneAliasesE();
                        String alias_list = toStringList(li);
                        alias += alias_list + " ";
                    }
                    symbolList.put ( gen.getHGGeneSymbol(), "" );
                    String locus = gen.getHGGeneTaxid() + "_" + gen.getHGGeneChr() + "_" + gen.getHGGeneLocusTag();
                    int tax_id = gen.getHGGeneTaxid().intValue();
                    SeqLoc location = gen.getHGGeneLocation().getSeqLoc();
                    String gene_location = "";
                    if ( location != null )
                    {
                        gene_location = location.toString();
                        SeqLoc.SeqLocInt locInt = location.getSeqLocInt();
                        SeqInterval interval = locInt.getSeqInterval();
                        if ( interval != null )
                        {
                            BigInteger fromInterval = interval.getSeqIntervalFrom();
                            BigInteger toInterval = interval.getSeqIntervalTo();
                            String strand = interval.getSeqIntervalStrand().getNaStrand().getValue();
                            if ( fromInterval != null && toInterval != null )
                            {
                               int f = fromInterval.intValue();
                               int t = toInterval.intValue();
                               locus_str += " " + tax_id + "_" + f + "_"+t+"_"+strand;
                            }
                        }
                    }
                    int geneid = gen.getHGGeneGeneid().intValue();
                    geneids += " _" + geneid + "_ ";
                }

                Set<String> symbolSet = symbolList.keySet();
                for ( String s : symbolSet ){
                    symbol += s + " ";
                }


                hgentry.put("hg_id", e.getHGEntryHgId().intValue());
                hgentry.put("alias", alias);
                hgentry.put("symbol", symbol);
                hgentry.put("locus", locus_str);
                hgentry.put("gene_id", geneids);

                alist.add(hgentry);
                if (alist.size() % 1000 == 0) {
                    table.append(alist);
                    alist = new ArrayList<>();
                }
            }
            table.append(alist);
            table.commit();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private static String toStringList(List<String> li) {
        String list_string = "";
        for (String l : li) {
            list_string += " " + l;
        }
        return list_string;
    }

}
