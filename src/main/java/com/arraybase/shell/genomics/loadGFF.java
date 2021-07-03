package com.arraybase.shell.genomics;

import com.arraybase.GBV;
import com.arraybase.bio.GFF3Loader;
import com.arraybase.modules.UsageException;

import java.util.ArrayList;

public class loadGFF implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
        //--load-gff http://s3-proxy:10000/vfvf-ngs-reference/genomes/canis-familiaris-88/resources/mrna.gff3 /test/gffcanfam
        String host = "";
        String path = "";
        String annotation = "";
        ArrayList<String> additional = new ArrayList<String>();
        String[] cl = command.split("\\s+");
        host = cl[cl.length-2];
        path = cl[cl.length-1];
        GFF3Loader.load(annotation, host, path);
//        --load-gff http://s3-proxy:10000/vfvf-ngs-reference/genomes/canis-familiaris-88/resources/mrna.gff3 /test/gffcanfam
        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
