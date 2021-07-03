package com.arraybase.bio;

import com.arraybase.ABTable;

import java.util.HashMap;

/**
 * Created by jmilton on 4/28/2016.
 */
public class FASTATableAppend {
    public final static int MAX_CHUNK_SET = 32766;

    private String[] seqs = null;
    private int[] startindex = null;
    private int[] endindex = null;
    private int sequence_array_length = 0;


    public void append(ABTable table, String currentid, String header, String annotation, String _current_sequence, Integer start, Integer end, Integer overlap) {
        if (overlap == null) {
            overlap = 0;
        }
        if (_current_sequence.length() > 6000) {
            chunk(_current_sequence, overlap);
            for (int i = 0; i < sequence_array_length; i++) {
                HashMap<String, Object> values = new HashMap<String, Object>();
                values.put("seq_index", currentid + "_index" + i);
                values.put("header", header);
                values.put("sequence", seqs[i]);
                values.put("annotation", annotation);
                if (start != null && end != null) {
                    values.put("start", start + startindex[i]);
                    values.put("end", start + endindex[i]);
                }
                table.append(values, true);
            }

        } else {
            HashMap<String, Object> values = new HashMap<String, Object>();
            values.put("seq_index", currentid);
            values.put("annotation", annotation);
            values.put("sequence", _current_sequence);
            if (start != null && end != null) {
                values.put("start", start);
                values.put("end", end);
            }
            table.append(values, true);
        }

    }

    private String[] chunk(String current_sequence, Integer overlap) {
        int size = current_sequence.length();
        int number_of_new_sequences = 1 + (size / MAX_CHUNK_SET);
        System.out.println ( " nw seq size : "+ number_of_new_sequences );
        long overhang_length = Math.abs(size * overlap);
        System.out.println ( " Overhand Length: : "+ overhang_length + " num chars: "  + size + " overlap value " + overlap );
        long overhang_sequence_block_count = (overhang_length / MAX_CHUNK_SET);
        number_of_new_sequences += overhang_sequence_block_count;
        System.out.println ( " nw seq size : "+ number_of_new_sequences );
        seqs = new String[number_of_new_sequences];
        startindex = new int[number_of_new_sequences];
        endindex = new int[number_of_new_sequences];
        int prev_end = 0;
        for (int i = 0; i < number_of_new_sequences; i++) {
            int start = (prev_end - overlap);
            if (start < 0) {
                start = 0;
            }
            int end = start + MAX_CHUNK_SET;
            if (end > current_sequence.length())
                end = current_sequence.length();
            seqs[i] = current_sequence.substring(start, end);
            startindex[i] = start;
            endindex[i] = end;
            sequence_array_length = i+1;

            if (prev_end == end) {
                System.out.println(" break" + i + "  " + start + "-" + end + " " + seqs[i]);
                break;
            }else {
                prev_end = end;
            }
            if (i > (number_of_new_sequences - 100)) {
                System.out.println(i + "  " + start + "-" + end + " " + seqs[i]);
            }
        }

        System.out.println("Array size: " + seqs.length);

        return seqs;
    }
}
