package com.arraybase.shell.cmds.genome;

import com.arraybase.*;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.NodeNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by jmilton on 11/4/2016.
 */
public class LocusShow implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
//        AB-> /seq/human/ch1.sequence[1-10]
        int parens = command.indexOf('[');
        int parene = command.indexOf(']');

        String range = command.substring(parens + 1, parene);
        int md = range.indexOf('-');
        String st = range.substring(0, md);
        String et = range.substring(md + 1);
        st = st.trim();
        et = et.trim();
        int start = Integer.parseInt(st);
        int end = Integer.parseInt(et);
        int parn = command.indexOf('.');
        if (parn <= 0)
            throw new UsageException("Table could not be determined. ");
//        end = start+end;


        String path = command.substring(0, parn);
        ABTable abTable = new ABTable(path);
        try {
            if (!abTable.exists()) {
                throw new UsageException(" Path " + path + " is not a valid table.");
            } else {
                String search_string = "(start:[* TO " + start + "] AND " +
                        "end:[" + start + " TO *]) OR (" +
                        "start:[" + end + " TO *] AND end:[* TO " + end + "])";
                String sortString = "start asc";

                System.out.println(search_string);

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
                if (fmap != null) {
                    print(fmap.keySet());
                }
                count = first.size();
//                print(first);
                print_sub_sequence(first, start, end);
                while (it.hasNext()) {
                    ArrayList<LinkedHashMap<String, Object>> increment = it
                            .next();
                    print(increment);
                    print_sub_sequence(increment, start, end);
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
            throw new UsageException("Failed to validate table " + path);
        } catch (NodeNotFoundException e) {
            e.printStackTrace();
            throw new UsageException("Node exception " + e.getMessage());
        } catch (NotASearchableTableException e) {
            e.printStackTrace();
            throw new UsageException("Exception in search algorithm" + e.getMessage());
        }

        return null;
    }

    private void print_sub_sequence(ArrayList<LinkedHashMap<String, Object>> increment, int start, int end) {

        StringBuffer str = new StringBuffer();
        int sti = 0;
        for (LinkedHashMap<String, Object> col : increment) {
            String seq = (String) col.get("sequence");
            int start_index = (Integer) col.get("start");
            int seq_offset_start = start - start_index - 1;
            int distance_index = seq_offset_start + (end - start);
            if (distance_index <= 0) {
                String sub_seq = seq.substring(seq_offset_start, seq.length() - 30);
                str.append(sub_seq);
                // we need to get coordinates from the next values and append them
            } else {
                String sub_seq = "";
                if (distance_index > seq.length()) {
                    while (distance_index >= seq.length()) {
                        if ((sti + seq_offset_start) >= seq.length()) {
                            sub_seq += ";";
                        } else {
                            sub_seq += seq.substring((sti + seq_offset_start), seq.length() - 1);
                        }
                        distance_index -= seq.length() - 1;
                        // we need to get the sequence incrementally on a document basis
                        // need to get the next document here.
                    }
                } else {
                    sub_seq = seq.substring((sti + seq_offset_start), distance_index);
                }
                sti += sub_seq.length();
                str.append(sub_seq);
            }
            str.append('.');
        }

        System.out.println(str.toString());


    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }


    private void print(Set<String> keys) {
        String ps = "";
        for (String key : keys) {
            ps += "[" + key + "]" + "\t\t";
        }
        GB.print(ps);
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


}
