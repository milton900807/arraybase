package com.arraybase.bio;

import com.arraybase.ABTable;
import com.arraybase.net.Download;
import com.arraybase.net.FTPImporter;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class GFF3Loader {
//1       ensembl gene    48067612        48159754        .       +       .
// ID=gene:ENSCAFG00000000659;Name=SYTL3;biotype=protein_coding;description=synaptotagmin like 3 [Source:HGNC Symbol%3BAcc:HGNC:15587];gene_id=ENSCAFG00000000659;logic_name=ensembl;version=3
    public static void load(String annotation, String local, String gb_file) {
        System.out.println(" loading a fasta file:  " + local);
        File local_file = Download.downloadToTemp(local);
        if (local.endsWith(".gz")) {
            File local_file_compressed = new File(local);
            FTPImporter.gunzipIt(local_file_compressed);
            String nfilename = local.substring(0, local.length() - 3);
            local_file = new File(nfilename);
        }
        Map<String, String> schema = new LinkedHashMap<String, String>();
        schema.put("seqid", "string_ci");
        schema.put("source", "string_ci");
        schema.put("type", "string_ci");
        schema.put("start", "sint");
        schema.put("end", "sint");
        schema.put("score", "sfloat");
        schema.put("phase", "string_ci");
        schema.put("biotype", "string_ci");
        schema.put("description", "string_ci");
        schema.put("name", "string_ci");
        schema.put("id", "string_ci");
        schema.put("gene_id", "string_ci");
        try {
            // first thing we need to do is determine the size of the file by characters
            FileInputStream inputStream = new FileInputStream(local_file);
            Scanner sc = new Scanner(inputStream, "UTF-8");
//            ABTable table = new ABTable(gb_file);
//            if (!table.exists()) {
//                table.create(schema);
//            }
            String currentid = "";
            String current_sequence = new String("");
            int currentLine = 0;
            String _currentid = null;
            Integer start = 0;
            Integer end = 0;
            String header = "";
            String[] _header = null;
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(" line " + line);
            }

        } catch (Exception _e) {
            _e.printStackTrace();
        }


    }


}
