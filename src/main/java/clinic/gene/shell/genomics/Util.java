package clinic.gene.shell.genomics;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.tm.tree.TNode;
import com.arraybase.util.GBRGX;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static String findMethod(String c) {
        int first_index = c.indexOf('.');
        int first_paran = c.indexOf('(');
        String b = c.substring(first_index+1, first_paran);
        b = b.trim();
        return b;
    }

    public static List<String> getTables(String path, List<String> tables) {
        GBNodes nodeobject = GB.getNodes();
        TNode node = nodeobject.getNode(path);
        if (node.getNodeType().equalsIgnoreCase("table")) {
            tables.add(path);
        }
        if (GBNodes.hasChildren(node)) {
            List<TNode> nodes = GBNodes.getRefNodes(path);
            for (TNode no : nodes) {
//                System.out.println(" no " + no.getName());
                String npath = path + "/" + no.getName();
                ArrayList<String> local_list = new ArrayList<String>();
                List<String> t = getTables(npath, local_list);
                for (String s : t) {
                    tables.add(s);
                }
            }
        }
        return tables;
    }

    public static int[] pullRange(String c) {
        int start_count = 0;
        int end_count = Integer.MAX_VALUE;
        int[] v = new int[2];
        int bindex = c.lastIndexOf('{');
        if (bindex > 0) {
            String sub = c.substring(bindex);
            if (sub.matches(GBRGX.COUNT_RANGE + "$")) {
                // we have a range.
                int ob = sub.lastIndexOf('{');
                int cb = sub.lastIndexOf('}');
                String rng = sub.substring(ob + 1, cb);
                rng = rng.trim();
                int m = rng.indexOf('-');
                if (m <= 0) {
                    GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
                }
                String bg = rng.substring(0, m);
                if (bg == null) {
                    GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
                }
                bg = bg.trim();
                String eg = rng.substring(m + 1);
                if (eg == null) {
                    GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");
                }
                eg = eg.trim();

                try {
                    int start = Integer.parseInt(bg);
                    int end = Integer.parseInt(eg);

                    start_count = start;
                    end_count = end;
                } catch (NumberFormatException nf) {
                    nf.printStackTrace();
                    GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");

                }
            }
        }
        v[0] = start_count;
        v[1] = end_count;
        return v;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    public static Object readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            if (jsonText != null && jsonText.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonText);
                return jsonArray;
            } else if (jsonText != null && jsonText.startsWith("{")) {
                JSONObject json = new JSONObject(jsonText);
                return json;
            } else {
                return null;
            }
        } finally {
            is.close();
        }
    }


    public static void readJsonFromPOSTUrl(String s, JSONObject ob ) {
    }
}
