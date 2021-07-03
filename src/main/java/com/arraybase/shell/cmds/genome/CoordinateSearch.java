package com.arraybase.shell.cmds.genome;

import com.arraybase.*;
import com.arraybase.lang.ItrVar;
import com.arraybase.modules.UsageException;
import com.arraybase.qmath.FloatVar;
import com.arraybase.shell.GBCommand;
import com.arraybase.shell.cmds.operator.FieldOperator;
import com.arraybase.tab.FieldFunctionException;
import com.arraybase.tm.GColumn;
import com.arraybase.tm.NodeNotFoundException;
import com.arraybase.util.GBRGX;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.net.ConnectException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jmilton on 5/18/2016.
 * <p>
 * We're rewritting this feature... 03/09/2018
 */
public class CoordinateSearch implements com.arraybase.GBPlugin {
    public static final Pattern field_function_pattern = Pattern
            .compile(GBRGX.FIELD_FUNCTION_REPLACE);
    private GBSearch gs = GB.getSearch();
    private int start_count = 0;
    private int end_count = Integer.MAX_VALUE;

    /**
     * given a sequence region search for all the hits and print coordinates
     * /human/chr1.sequence[8-99].coords(*ACGT*)
     * 1)  99292 99295 etc...
     */
    public String exec(String c, String variable____key) throws UsageException {
        start_count = 0;
//        /human/chr1.sequence[8-99].coords(*ACGT*)
        end_count = Integer.MAX_VALUE;
        String search_ = c;
//        int coords_method_index = c.indexOf("coords\\s*\\(");
        int coords_method_index = c.indexOf(".coords");
        String command = c.substring(0, coords_method_index);
        String _user_search_string = c.substring(coords_method_index + 7);
        int sts = _user_search_string.indexOf('(');
        int ests = _user_search_string.indexOf(')');
        _user_search_string = _user_search_string.substring(sts + 1, ests);
        _user_search_string = _user_search_string.trim();

        GB.print(" Search string " + _user_search_string);
        if (_user_search_string.startsWith("*"))
            _user_search_string = _user_search_string.substring(1, _user_search_string.length());
        if (_user_search_string.endsWith("*"))
            _user_search_string = _user_search_string.substring(0, _user_search_string.length() - 1);


        String path = "";
        int parens = command.indexOf('[');
        int parene = command.indexOf(']');

        path = command.substring(0, command.indexOf('.'));
        int start = -1;
        int end = -1;
        if (parens < 0) {
        } else {
            String range = command.substring(parens + 1, parene);
            int md = range.indexOf('-');
            String st = range.substring(0, md);
            String et = range.substring(md + 1);
            st = st.trim();
            et = et.trim();
            start = Integer.parseInt(st);
            end = Integer.parseInt(et);

        }
        try {
            ABTable abTable = new ABTable(path);
            if (!abTable.exists()) {
                throw new UsageException(" Path " + path + " is not a valid table.");
            } else {
                String search_string = "sequence:*" + _user_search_string + "*";
                if (start >= 0 && end > 0) {
                    search_string += " AND " +
                            "(start:[" + start + " TO *] AND " +
                            "end:[* TO " + end + "])";
                }
                String sortString = "start asc";
                GB.print("\t\t- - - - - - - - - - - " + search_string + " - - - - - - - - - -");
                String[] cols = {"sequence", "start", "end"};
                int start_count = 0;
                int end_count = 10000;
                SearchConfig config = null;
                Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                        .searchAndDeploy(path, search_string, sortString, cols,
                                start_count, end_count, config);
                ArrayList<LinkedHashMap<String, Object>> first = it.next();
                if (first == null || first.size() <= 0) {
                    GB.print("No results");
                    return "No results";
                }
                LinkedHashMap fmap = first.get(0);
                int count = 0;
//                if (fmap != null) {
//                    print(fmap.keySet());
//                }
                count = first.size();
//                print(first);
                print_sub_sequence(first, _user_search_string);
                while (it.hasNext()) {
                    ArrayList<LinkedHashMap<String, Object>> increment = it
                            .next();
                    print(increment);
                    print_sub_sequence(increment, _user_search_string);
                    count += increment.size();
                }
                GB.print("\t\tCount " + count + ". ");
                if (it instanceof GBSearchIterator) {
                    GBSearchIterator itg = (GBSearchIterator) it;
                    GB.print("\t\tSearch Total " + itg.getTotal());
                }
//                abTable.search(fieldname+":"+args);
            }
        } catch (NodeWrongTypeException e) {
            e.printStackTrace();
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            throw new UsageException("Node exception " + e.getMessage());
        } catch (NotASearchableTableException e) {
            e.printStackTrace();
            throw new UsageException("Exception in search algorithm" + e.getMessage());
        }

        return null;


//        return basicSearch(path, sortString, search_string, columns);
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

    private void pullRange(String c) {
        start_count = 0;
        end_count = Integer.MAX_VALUE;

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

                    this.start_count = start;
                    this.end_count = end;
                } catch (NumberFormatException nf) {
                    nf.printStackTrace();
                    GB.print(" Format of the search range values is incorrect.  Format should be {start-end}");

                }
            }
        }
    }

    private String parseSetField(String c) {

        return null;
    }

    private String operate(ArrayList<FieldOperator> operators, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getInputString(String[] sf) {
        String input = "";
        int index = 0;

        for (String t : sf) {
            t = t.trim();
            if (index > 0) {
                // if (t.contains("[") && t.contains("]")) {
                // if (t.matches("\\s*\\[\\s*"))
                // t = t.replaceAll("\\s*\\[\\s*", "$");
                // else if (t.matches("\\s*\\]")) {
                // t = t.replaceAll("\\s*\\]", "");
                // } else {
                // t = t.replace('[', '$');
                // t = t.replace("]", "");
                // }
                // }
                input += t + " ";
            }
            index++;
        }
        if (input != null)
            return input.trim();
        else
            return "";
    }

    private ArrayList<String> parseInputField(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    public String basicSearch(String path, String sortString,
                              String search_string, ArrayList<String> columns) {
        {
            int count = 0;
            String[] cols = columns.toArray(new String[columns.size()]);
            try {
                Map<String, String> node_props = GB.getNodeProps(path);
                SearchConfig config = new SearchConfig(SearchConfig.NODE_CONFIG);
                config.setConfigProperties(node_props);
                if (sortString == null)
                    GB.print("\t-Search : "
                            + search_string
                            + " sortstring : { default sorting... ->insert timestamp }");
                else
                    GB.print("\t-Search : " + search_string + " sortstring : "
                            + sortString);


                Iterator<ArrayList<LinkedHashMap<String, Object>>> it = GBSearch
                        .searchAndDeploy(path, search_string, sortString, cols,
                                start_count, end_count, config);
                ArrayList<LinkedHashMap<String, Object>> first = it.next();
                if (first == null || first.size() <= 0) {
                    GB.print("****************************\n" +
                            "No results\n" +
                            "*****************************");
                    return "No results";
                }


//                LinkedHashMap fmap = first.get(0);
//                if (fmap != null) {
//                    print(fmap.keySet());
//                }


                count = first.size();
                printCount(first, search_string);
                while (it.hasNext()) {
                    ArrayList<LinkedHashMap<String, Object>> increment = it
                            .next();

                    printCount(increment, search_string);
                    count += increment.size();
                }
                GB.print("\t\tCount " + count + ". ");
                if (it instanceof GBSearchIterator) {
                    GBSearchIterator itg = (GBSearchIterator) it;
                    GB.print("\t\tSearch Total " + itg.getTotal());
                }

            } catch (NotASearchableTableException e) {
                e.printStackTrace();
            } catch (NodeNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // GB.print("Search complete. ");
        return "";
    }

    /**
     * Remove the sort string
     *
     * @return
     */
    private String removeSortString(String _search) {
        int com = _search.indexOf(',');
        if (com > 0) {
            String st = _search.substring(0, com);
            return st.trim();
        }
        return _search;
    }

    /**
     * @param fields
     * @param columns
     * @return
     */
    public static String getColumns(String fields, ArrayList<String> columns) {
        String f = fields;
        if ((f == null) || !(f.contains("[") && f.contains("]"))) {
            return null;
        }
        if (f.contains(")")) {
            int ind = f.lastIndexOf(')');
            f = f.substring(ind + 1);
            f = f.trim();
        }

        int index = f.indexOf('[');
        int lindex = f.indexOf(']');
        String field = f.substring(index + 1, lindex);
        columns.add(field);
        String t = f.substring(lindex + 1);
        int i2 = t.indexOf('[');
        if (i2 < 0)
            return t;
        return getColumns(t, columns);
    }

    public static void main(String[] args) {

        String test = "http://thisisatest.com?f[i][anothersomething]eld[mylink][lfield]andth[field1]enanother&[field2]";
        CoordinateSearch s = new CoordinateSearch();
        ArrayList<String> cols = getColumns(test);
        for (String c : cols) {
            System.out.println(c);
        }

    }

    public static ArrayList<String> getColumns(String fields) {
        ArrayList<String> columns = new ArrayList<String>();
        getColumns(fields, columns);
        return columns;
    }

    public static String[] getColumnArray(String fields) {
        ArrayList<String> columns = new ArrayList<String>();
        getColumns(fields, columns);
        String[] c = columns.toArray(new String[columns.size()]);
        return c;
    }

    public static String parseSortString(String search_string) {
        String[] sp = search_string.split(",");
        search_string = sp[0];
        String sort_field = null;
        String direction = null;

        if (sp.length >= 2)
            sort_field = sp[1];
        if (sp.length >= 3)
            direction = sp[2];
        if (direction == null)
            direction = "desc";
        for (String s : sp) {
            if (s.contains("=")) {
                String[] var = s.split("=");
                String v = var[0].trim();
                if (v.equalsIgnoreCase("search"))
                    search_string = var[1];
                else if (v.equalsIgnoreCase("sort")) {
                    sort_field = var[1];
                } else if (v.equalsIgnoreCase("direction")) {
                    direction = var[1];
                }
            }
        }
        sort_field = sort_field.trim();

        if (sort_field.endsWith(" desc") || sort_field.endsWith(" asc"))
            return sort_field;

        String sortString = sort_field + " " + direction;
        return sortString;
    }

    private void print(ArrayList<LinkedHashMap<String, Object>> increment,
                       String post_string) {
        for (LinkedHashMap<String, Object> ls : increment) {
            Set<String> keys = ls.keySet();
            String ps = post_string;
            for (String key : keys) {
                ps = ps.replace("[" + key + "]", ls.get(key) + "\t\t");
            }
        }
    }


    private int print(ArrayList<LinkedHashMap<String, Object>> increment) {
        int count = 0;
        for (LinkedHashMap<String, Object> ls : increment) {
            Set<String> keys = ls.keySet();
            String ps = "";
            for (String key : keys) {
                ps += "[" + ls.get(key) + "]" + "\t\t";
            }
            GB.print(ps);
            count++;
        }
        return count;
    }

    private int printCount(ArrayList<LinkedHashMap<String, Object>> increment, String q) {
        int count = 0;
        int prev_index = 0;
        for (LinkedHashMap<String, Object> ls : increment) {
            //Set<String> keys = ls.keySet();
            String sequence = (String) ls.get("sequence");
            int start = (Integer) ls.get("start");
            while (prev_index >= 0) {
                int index = sequence.indexOf(q, prev_index + 1);
                GB.print(" _  " + 1 + (start + index));
                prev_index = index;
            }
            count++;
        }
        return count;
    }


    private ArrayList<String> process(String post_string) {
        return null;
    }

    private void printArgs() {
        GB.print("search=$string, sort=$field, sortd=[desc|asc]");
        GB.print("search=Comment:Jeff*, sort=row_index, direction=desc");

    }

    public GBV execGBVIn(String c, GBV input) {
        GB.print("This object does not handle input... i.e. command1 | searchobject.search(etc...)");
        return null;
    }

    public static void print_sub_sequence(ArrayList<LinkedHashMap<String, Object>> increment, String search_string) {
        if (search_string.contains("?") || search_string.contains("*")) {
            search_string = adjust_to_regex_from_lucene_syntax(search_string);
            Pattern pattern = Pattern.compile(search_string);
            int sti = 0;
            for (LinkedHashMap<String, Object> col : increment) {
                String seq = (String) col.get("sequence");
                int start_index = (Integer) col.get("start");
                Matcher matcher = pattern.matcher(seq);
                int index = 0;
                while (matcher.find(index)) {
                    index = matcher.start()+1;
                    ArrayList<Integer> hits = new ArrayList<Integer>();
                    GB.print("**************************\n" +
                            " HIT : " + (1 + start_index + index) + "\n" +
                            "*******************************\n");
                }
            }
        } else {
            int sti = 0;
            for (LinkedHashMap<String, Object> col : increment) {
                String seq = (String) col.get("sequence");
                int start_index = (Integer) col.get("start");
                int index = seq.indexOf(search_string);
                ArrayList<Integer> hits = new ArrayList<Integer>();
                while (index >= 0) {
                    GB.print("**************************\n" +
                            " HIT : " + (1 + start_index + index) + "\n" +
                            "*******************************\n");

                    index = seq.indexOf(search_string, index + 1);
                }
            }
        }

    }

    public static String adjust_to_regex_from_lucene_syntax(String search_string) {
        return search_string.replaceAll("\\?", ".");
    }

    private boolean contains(ArrayList<String> columns, String field) {
        for (String c : columns) {
            if (c.equalsIgnoreCase(field))
                return true;
        }
        return false;
    }

}
