package clinic.gene.shell.genomics;

import com.arraybase.*;
import com.arraybase.db.util.SourceType;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.shell.cmds.genome.CoordinateSearch;
import com.arraybase.shell.cmds.search2;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.GRow;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;
import org.apache.lucene.store.GrowableByteArrayDataOutput;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecursiveCoreSearch implements com.arraybase.GBPlugin {


    private int start_count = 0;
    private int end_count = Integer.MAX_VALUE;


    public String exec(String command, String variable_key) throws UsageException {
        command = command.trim();
        if (!command.endsWith("]"))
        {
            command = "[start][stop][sequence]";
        }
        int ind = command.indexOf(".rcoords");
        String path = command.substring(0, ind);




        String search_ = command;
        int ti = search_.indexOf('.');
        int t2 = search_.indexOf('(');
        int t3 = search_.lastIndexOf(')');
        int[] r = Util.pullRange(search_);
        this.start_count=r[0];
        this.end_count = r[1];
        String q = search_.substring(t2 + 1, t3);
        String fields = search_.substring(t3 + 1);
        ArrayList<String> columns = new ArrayList<String>();
        if (fields != null && fields.length() > 0)
            columns = getColumns(fields);
        else {
            try {
                columns = getAllColumns(path);
            } catch (ConnectException e) {
                // TODO Auto-generated catch block
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
                int start_index = (int) row.getData().get("start");
                GB.print("\t" + row.getData().get("start"));
                String sequence = (String) row.getData().get("sequence");
                print_sequence(sequence, q, start_index);
            }

        }


        return null;
    }

    private void print_sequence(String sequence, String search_string, int start_index) {
        if (search_string.startsWith("sequence:")) {
            search_string = search_string.substring(10).trim();
        }
        if (search_string.contains("?") || search_string.contains("*")) {
            search_string = CoordinateSearch.adjust_to_regex_from_lucene_syntax(search_string);
            Pattern pattern = Pattern.compile(search_string);
            Matcher matcher = pattern.matcher(sequence);
            int index = 0;
            while (matcher.find(index)) {
                index = matcher.start() + 1;
                GB.print("\t\t\t\t**************************\n" +
                        " \t\t\t\t\t\t HIT : " + (1 + start_index + index) + "\n" +
                        " \t\t\t\t***************************\n");
            }
        }
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
        GB.print ( " \t hello world " );

        return null;
    }
}
