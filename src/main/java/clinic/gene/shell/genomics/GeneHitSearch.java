package clinic.gene.shell.genomics;

import com.arraybase.*;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.shell.cmds.genome.CoordinateSearch;
import com.arraybase.shell.cmds.search2;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneHitSearch implements GBPlugin {

    private int start_count = 0;
    private int end_count = Integer.MAX_VALUE;
    private int FLANK_SIZE = 3;

    // the reference path is an optional parameter
    // for example below the second parameter is the reference table
    ///human.genes(*TCTTGATTTGTCAGCA*, /annotations/hg38/genes)
    private String reference_path = null;
    private static String[] refColumns = {"chromosome", "feature", "gene", "feature_type", "strand"};


    public String exec(String command, String variable_key) throws UsageException {
        geneHits(command);
        return "Complete";
    }

    // command should look like this:
    //  /path_to_folder.flank(*ACTG*)
    public void geneHits(String command) {
        command = command.trim();
        if (!command.endsWith("]")) {
            command = command + "[start][end][sequence][seq_index][header]";
        }
        String method = Util.findMethod(command);
        int ind = command.indexOf(method) - 1;
        String path = command.substring(0, ind);
        String search_ = command;
        int ti = search_.indexOf('.');
        int t2 = search_.indexOf('(');
        int t3 = search_.lastIndexOf(')');
        int[] r = Util.pullRange(search_);
        this.start_count = r[0];
        this.end_count = r[1];
        String q = search_.substring(t2 + 1, t3);
        // check to see if there are any parameters to this query method
        int commaindex = q.indexOf(',');
        String refpath = q.substring(commaindex + 1);
        if (commaindex > 0) {
            q = q.substring(0, commaindex);
            q = q.trim();
            refpath = refpath.trim();
            if (refpath != null) {
                setReferencePath(refpath);
            }
        }
        q = "sequence:" + q;
        String fields = search_.substring(t3 + 1);
        ArrayList<String> columns = new ArrayList<String>();
        if (fields != null && fields.length() > 0)
            columns = getColumns(fields);
        else {
            try {
                columns = getAllColumns(path);
            } catch (ConnectException e) {
                e.printStackTrace();
                columns.add("TMID");
                GB.print(" Failed to access the table schema for : " + path);
            }
        }
        String[] cols = columns.toArray(new String[columns.size()]);
        ArrayList<String> list = new ArrayList<String>();
        List<String> tables = Util.getTables(path, list);
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
//                GB.print("\t" + start_index + " - " + end_index);
//                String seq_index = (String) data.get("seq_index");
//                String sequence = (String) data.get("sequence");
//                print_sequence(sequence, q, seq_index, start_index, t, cols);
            }
        }
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
        ArrayList<LSC> lsc = new ArrayList<LSC>();
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

                // start index of the string
                GB.print("\n" +
                        "\t HIT : " + (1 + start_index + index) + "\n" +
                        "\n");
                int genomic_index = start_index + index - 1;
                if (this.reference_path != null) {
                    ABTable ref = new ABTable(this.reference_path);
                    String gene_query = "chromosome:" + chr + " AND start:[* TO " + genomic_index + "] AND end:[" + (genomic_index+sequence.length()) + " TO *]";
                    ABaseResults gene_results = ref.search(gene_query, refColumns, 0, 100);
                    List<GRow> rows = gene_results.getValues();
                    for (GRow gener : rows) {
                        Map<String, Object> gene_data = gener.getData();
                        Set<String> keys = gene_data.keySet();
                        for (String k : keys) {
                            Object ob = gene_data.get(k);
                            GB.print("\t\t " + k + "\t" + ob);
                        }
                    }
                }
//


                GB.print(" ---------------------------------- ");
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
