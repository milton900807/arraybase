package clinic.gene.shell.genomics;

import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.shell.cmds.genome.CoordinateSearch;
import com.arraybase.shell.cmds.search2;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceSearch implements GBPlugin {

    private int start_count = 0;
    private int end_count = Integer.MAX_VALUE;
    private int FLANK_SIZE = 3;
    public static String[] cols = {"start", "end", "sequence", "seq_index", "header", "TMID"};
    // the reference path is an optional parameter
    // for example below the second parameter is the reference table
    ///human.genes(*TCTTGATTTGTCAGCA*, /annotations/hg38/genes)
    private String reference_path = null;
    private static String[] refColumns = {"chromosome", "feature", "gene", "feature_type", "strand"};
    private String sequence_table = "";
    private String sequence = "";
    private String outputfile ="mode-j.csv";


    private LinkedHashMap<String, LinkedHashMap<String, String>> results = new LinkedHashMap<>();


    //	seq --mode=h  --sequence=/human --annotation=/annotations/GRCh38/a GCTATTAGGAGTCTTT
    public String exec(String command, String variable_key) throws UsageException {
        geneHits(command);
        return "Complete";
    }

    // command should look like this:
    //  /path_to_folder.flank(*ACTG*)
    public void geneHits(String command) {
        String[] co = command.split(" ");
        String sequence_table = parse("sequence", co);
        String annotation_table = parse("annotation", co);
        String file = parse("file", co);
        if ( file != null  && file.length() > 0 )
        {
            this.outputfile = file;
        }


        String mode = parse("mode", co);
        String qsequence = co[co.length - 1];
        command = command.trim();
        String q = "sequence:*" + qsequence + "*";
        this.start_count = 0;
//        this.end_count = 1000000;
        this.reference_path = annotation_table.trim();
        this.sequence_table = sequence_table;
        this.sequence = qsequence;


        this.results = new LinkedHashMap<>();

        if (mode.equalsIgnoreCase("j")) {
            goJ();
        } else {
            ArrayList<String> list = new ArrayList<String>();
            List<String> tables = Util.getTables(sequence_table, list);
            for (String tpath : tables) {
                ABTable t = new ABTable(tpath);


                ABaseResults results = t.search(q, cols, start_count, end_count);
                int total = results.getTotalHits();
                List<GRow> hits = results.getValues();
                GB.print(" " + tpath + " \t" + total);
                for (GRow row : hits) {
                    Map data = row.getData();
                    int start_index = (int) data.get("start");
                    String sequence = (String) data.get("sequence");
                    int end_index = (int) data.get("end");
                    String header = (String) data.get("header");
                    int st = header.indexOf(' ');
                    String chr = header.substring(1, st);
                    chr = chr.trim();
                    printAnnotations(sequence, start_index, end_index, q, chr);
                }
            }
        }
    }

    private void goJ() {
        for (int i = 0; i < this.sequence.length(); i++) {
            char[] ch = sequence.toCharArray();
            ch[i] = '?';
            String qsequence = new String(ch);

            GB.print(" --------------------------------------");
            GB.print(" \n\n\n\n\n " + qsequence + " \n\n\n\n ");


            ArrayList<String> list = new ArrayList<String>();
            List<String> tables = Util.getTables(this.sequence_table, list);
            for (String tpath : tables) {
                ABTable t = new ABTable(tpath);
                ABaseResults results = t.search("sequence:*" + qsequence + "*", cols, start_count, end_count);
                int total = results.getTotalHits();
                List<GRow> hits = results.getValues();
                GB.print(" " + tpath + " \t" + total);
                for (GRow row : hits) {
                    Map data = row.getData();
                    int start_index = (int) data.get("start");
                    String sequence = (String) data.get("sequence");
                    int end_index = (int) data.get("end");
                    String header = (String) data.get("header");
                    int st = header.indexOf(' ');
                    String chr = header.substring(1, st);
                    chr = chr.trim();
                    printAnnotations(sequence, start_index, end_index, qsequence, chr);
                }
            }
        }


        GB.print(" -----------printing the results----------------------- ");
        File f = new File(outputfile);
        try {
            PrintStream pr = new PrintStream(f);
            Set<String> resset = this.results.keySet();
            for (String r : resset) {
                LinkedHashMap<String, String> gr = this.results.get(r);
                Set<String> keys = gr.keySet();
                for ( String key : keys )
                {
                    String gene = gr.get(key);
                    pr.println ( r + "," + key + "," + gene);
                }
            }

            pr.flush();
            pr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }

    private String parse(String key, String[] co) {
        for (String st : co) {
            if (st.startsWith("--" + key)) {
                String[] sp = st.split("=");
                return sp[1];
            }
        }
        return null;
    }

    private void setReferencePath(String refpath) {
        this.reference_path = refpath;

    }


    public static ArrayList<String> getColumns(String fields) {
        ArrayList<String> columns = new ArrayList<String>();
        search2.getColumns(fields, columns);
        return columns;
    }


    private ArrayList<String> getAllColumns(String path)
            throws ConnectException {
        ArrayList<GColumn> column = GB.getAllColumns(path);
        ArrayList<String> cols = new ArrayList<String>();
        for (GColumn cc : column) {
            cols.add(cc.getName());
        }
        return cols;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }

    private void printAnnotations(String sequence, int start_index, int end_index, String search_string, String chr) {
        if (search_string.startsWith("sequence:")) {
            search_string = search_string.substring(10).trim();
        }
        if (search_string.contains("?") || search_string.contains("*")) {
            // local string search
            search_string = CoordinateSearch.adjust_to_regex_from_lucene_syntax(search_string);
            String query_sequence = search_string.trim();
            if (query_sequence.endsWith("*")) {
                query_sequence = query_sequence.substring(0, query_sequence.length() - 1);
            }
            if (query_sequence.startsWith("*")) {
                query_sequence = query_sequence.substring(1).trim();
            }
            Pattern pattern = Pattern.compile(search_string);
            Matcher matcher = pattern.matcher(sequence);
            int index = 0;
            while (matcher.find(index)) {
                index = matcher.start() + 1;
                int genomic_index = start_index + index;
                // start index of the string
                GB.print("\n" +
                        "\t HIT : " + genomic_index + "\n" +
                        "\n");
                if (this.reference_path != null) {
                    ABTable ref = new ABTable(this.reference_path);
                    String gene_query = "chromosome:" + chr + " AND start:[* TO " + genomic_index + "] AND end:[" + genomic_index + " TO *]";
                    GB.print( "-+-+\t" + gene_query);
                    ABaseResults gene_results = ref.search(gene_query, refColumns, 0, 100);
                    List<GRow> rows = gene_results.getValues();
                    for (GRow gener : rows) {
                        Map<String, Object> gene_data = gener.getData();
                        Set<String> keys = gene_data.keySet();
                        for (String k : keys) {
                            Object ob = gene_data.get(k);
                            GB.print("\t\t " + k + "\t" + ob);
                        }
                        if (gene_data.containsKey("gene") && gene_data.containsKey("feature_type")) {
                            String gene = (String) gene_data.get("gene");
                            String feature_type = (String) gene_data.get("feature_type");
                            String feature = (String) gene_data.get("feature");

                            LinkedHashMap<String, String> glist = this.results.get(query_sequence);
                            if (glist == null)
                                glist = new LinkedHashMap<>();
                            glist.put(gene, feature_type + ";" + feature);
                            this.results.put ( query_sequence, glist );
                        }
                    }
                }
//


            }
        }
    }

//
//    private void print_sequence(String sequence, String search_string, String seq_index, int start_index, ABTable table, String[] cols) {
//        if (search_string.startsWith("sequence:")) {
//            search_string = search_string.substring(10).trim();
//        }
//        if (search_string.contains("?") || search_string.contains("*")) {
//            search_string = CoordinateSearch.adjust_to_regex_from_lucene_syntax(search_string);
//            Pattern pattern = Pattern.compile(search_string);
//            Matcher matcher = pattern.matcher(sequence);
//            int index = 0;
//            while (matcher.find(index)) {
//                index = matcher.start() + 1;
////                String left_flank = getleftSequence(sequence, index, seq_index, table, cols);
//                GB.print("\t\t\t\t**************************\n" +
//                        " \t\t\t\t\t\t HIT : " + (1 + start_index + index) + "\n" +
//                        " \t\t\t\t***************************\n");
//            }
//        }
//    }


}
