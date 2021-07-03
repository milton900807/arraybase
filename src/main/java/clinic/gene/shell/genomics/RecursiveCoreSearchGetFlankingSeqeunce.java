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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveCoreSearchGetFlankingSeqeunce implements GBPlugin {

    private static int FLANK_SIZE = 500;


    public String exec(String command, String variable_key) throws UsageException {
        List<LSC> ls = getFlankSequences(command);
        return "Complete";
    }

    // command should look like this:
    //  /path_to_folder.flank(*ACTG*)
    public static List<LSC> getFlankSequences(String command) {

        ArrayList<LSC> lsc_list = new ArrayList<LSC> ();

        int start_count = 0;
        int end_count = Integer.MAX_VALUE;
        command = command.trim();
        if (!command.endsWith("]")) {
            command = command + "[start][end][sequence][seq_index]";
        }

        String method = Util.findMethod(command);
        int ind = command.indexOf(method);

        String path = command.substring(0, ind-1);
        if ( path.endsWith("."))
            path = path.substring(0, path.length()-1);
        String search_ = command;
        int ti = search_.indexOf('.');
        int t2 = search_.indexOf('(');
        int t3 = search_.lastIndexOf(')');
        int[] r = Util.pullRange(search_);
        start_count = r[0];
        end_count = r[1];

        String q = search_.substring(t2 + 1, t3);
        // check to see if there are any parameters to this query method
        int commaindex = q.indexOf(',');
        if (commaindex > 0) {
            q = q.substring(0, commaindex);
            q = q.trim();
            String refpath = q.substring(commaindex + 1);
            refpath = refpath.trim();
//            if ( refpath != null ){
//                setReferencePath ( refpath );
//            }
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
                int end_index = (int) data.get("end");
                GB.print("\t" + data.get("start"));
                String seq_index = (String) data.get("seq_index");
                String sequence = (String) data.get("sequence");
                List<LSC> flankSequences = getFlankSequences(sequence, seq_index, start_index, end_index, q, cols, t);

                for (LSC l : flankSequences) {

                    lsc_list.add ( l );
                }


//                print_sequence(sequence, q, seq_index, start_index, t, cols);
            }
        }
        return lsc_list;
    }


    public static List<LSC> getFlankSequences(String sequence, String seq_index, int start_index, int end_index, String search_string, String[] cols, ABTable t) {
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
                GB.print("\t\t\t\t**************************\n" +
                        " \t\t\t\t\t\t HIT : " + (1 + start_index + index) + "\n" +
                        " \t\t\t\t***************************\n");

                int genomic_index = start_index + index - 1;
                String left_flank = getleftSequence(sequence, start_index, end_index, genomic_index, seq_index, t, cols);
                // the genomic start sequence must include the length of the query sequence:
                int adjusted_genomic_start = genomic_index + query_sequence.length();
                String right_flank = getRightSequence(sequence, start_index, end_index, adjusted_genomic_start, seq_index, t, cols);
                GB.print("\t " + left_flank + " " + query_sequence + " " + right_flank);

                LSC lc = new LSC();
                lc.setCore(t.getDataLink());
                lc.setCenter_start(start_index);
                lc.setC(query_sequence);
                lc.setL(left_flank);
                lc.setR(right_flank);
                lsc.add(lc);


//                GB.print ( " left flank " + left_flank );
                GB.print(" ---------------------------------- ");
            }
        }

        return lsc;


    }

    private static String mergeWithPreviousSequence(ABTable table, String[] cols, String docIndex, int diff, String seq) {
        int ind = docIndex.indexOf("_index");
        String tind = docIndex.substring(ind + 6);
        Integer tind_val = Integer.parseInt(tind.trim());
        String previous_doc = "_index" + (tind_val - 1);
        ABaseResults results = table.search("seq_index:" + previous_doc, cols, 0, 1);
        String prev_sequence = (String) results.getValues().get(0).getData().get("sequence");
        int end_index = (Integer) results.getValues().get(0).getData().get("end");
        int start_index = (Integer) results.getValues().get(0).getData().get("start");
        int start_position = end_index - diff - start_index;
        String temp = prev_sequence.substring(start_position);
        String f = temp + seq;
        return f;
    }

    private static String mergeWithNextDoc(ABTable table, String[] cols, String docIndex, int diffcount, String seq) {
        int ind = docIndex.indexOf("_index");
        String tind = docIndex.substring(ind + 6);
        Integer tind_val = Integer.parseInt(tind.trim());
        String next_doc = "_index" + (tind_val + 1);
        ABaseResults results = table.search("seq_index:" + next_doc, cols, 0, 1);


        String prev_sequence = (String) results.getValues().get(0).getData().get("sequence");
        int start_index = (Integer) results.getValues().get(0).getData().get("start");
        String temp = prev_sequence.substring(0, diffcount);
        String f = seq + temp;
        return f;
    }

    private static String getleftSequence(String sequence, int start_index, int end_index, int gindex, String doc_index, ABTable table, String[] cols) {

        // strip the doc to the entire left hand sequence
        int lindex = gindex - FLANK_SIZE;
        // if this left hand sequenc is less than 500 then we need to fetch the previous doc
        if (lindex < start_index) {

            int index = gindex - start_index;
            String st = sequence.substring(0, index);
            int diff = FLANK_SIZE - st.length();

            String merged_with_previous_doc = mergeWithPreviousSequence(table, cols, doc_index, diff, st);
            return merged_with_previous_doc;
        } else {

            int doc_start = lindex - start_index;
            int doc_end = gindex - start_index;
            String st = sequence.substring(doc_start, doc_end);
            return st;
        }
    }

    private static String getRightSequence(String sequence, int start_index, int end_index, int gindex, String doc_index, ABTable table, String[] cols) {
        int lindex = gindex + FLANK_SIZE;
        // if this left hand sequenc is less than 500 then we need to fetch the previous doc
        if (lindex > end_index) {
            int index = gindex - start_index;
            String st = sequence.substring(index);
            int diffcount = FLANK_SIZE - st.length();
            String right_seq = mergeWithNextDoc(table, cols, doc_index, diffcount, st);
            return right_seq;
        } else {

            int doc_end = lindex - start_index;
            int doc_start = gindex - start_index;
            String st = sequence.substring(doc_start, doc_end);
            return st;
        }
    }


    public static ArrayList<String> getColumns(String fields) {
        ArrayList<String> columns = new ArrayList<String>();
        search2.getColumns(fields, columns);
        return columns;
    }


    private static ArrayList<String> getAllColumns(String path)
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
