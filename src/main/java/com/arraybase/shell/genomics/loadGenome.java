package com.arraybase.shell.genomics;

import com.arraybase.GBTableLoader;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.net.Download;

import java.io.*;
import java.util.ArrayList;

//--load-genome --overlap=25 --i=1...33,MT,X ftp://ftp.ensembl.org/pub/release-92/fasta/canis_familiaris/dna/Canis_familiaris.CanFam3.1.dna.chromosome.{{i}}.fa.gz /canis
//--load-genome --overlap=25 --i=1...33,MT,X ftp://ftp.ensembl.org/pub/release-92/fasta/canis_familiaris/dna/ftp://ftp.ensembl.org/pub/release-92/fasta/canis_familiaris/dna/Canis_familiaris.CanFam3.1.dna.chromosome.{{i}}.fa.gz /canis
public class loadGenome implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
        Integer olap = 25;
        String host = "";
        String path = "";
        Integer loops = 0;
        Integer loope = 1;
        ArrayList<String> additional = new ArrayList<String>();
        String[] cl = command.split("\\s+");
        for (String c : cl) {
            if (c.toLowerCase().startsWith("--overlap=")) {
                String[] tvalue = c.split("=");
                Integer overlapint = Integer.parseInt(tvalue[1]);
                olap = overlapint;
            } else if (c.toLowerCase().startsWith("--i=")) {
                String[] tvalue = c.split("=");
                String loopv = tvalue[1];
                int ind = loopv.indexOf("...");
                int comma_index = loopv.indexOf(',');
                if (comma_index > 0) {
                    String t = loopv.substring(comma_index + 1);
                    String[] tsp = t.split(",");
                    for (String ts : tsp)
                        additional.add(ts);
                    String lstart = loopv.substring(0, ind);
                    String lend = loopv.substring(ind + 3, comma_index);
                    loops = Integer.parseInt(lstart.trim());
                    loope = Integer.parseInt(lend.trim());
                    System.out.println(" loops " + loops + " - " + loope);
                } else {
                    String lstart = loopv.substring(0, ind);
                    String lend = loopv.substring(ind + 3);
                    loops = Integer.parseInt(lstart.trim());
                    loope = Integer.parseInt(lend.trim());
                    System.out.println(" loops " + loops + " - " + loope);
                }


            }
        }
        host = cl[cl.length - 2];
        path = cl[cl.length - 1];
        for (int i = loops; i < loope; i++) {
            String uri = host.replace("{{i}}", i + "");
            String path_uri = path.replace("{{i}}", i + "");
            System.out.println(" " + uri);
            File f = Download.downloadToTemp(uri);
            try {
                String annotation = "";
                if (annotation == null)
                    annotation = "";
//                import --user=jeff --overlap=25 ./fasta/human-grch38-chr1.fa /human/chr1
                GBTableLoader.loadFASTA(annotation, f.getAbsolutePath(), path_uri, olap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (String ad : additional) {
            String uri = host.replace("{{i}}", ad + "");
            String path_uri = path.replace("{{i}}", ad + "");
            System.out.println(" " + uri);
            File f = Download.downloadToTemp(uri);
            try {
                String annotation = "";
                if (annotation == null)
                    annotation = "";
//                import --user=jeff --overlap=25 ./fasta/human-grch38-chr1.fa /human/chr1
                GBTableLoader.loadFASTA(annotation, f.getAbsolutePath(), path_uri, olap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
