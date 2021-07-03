package com.arraybase.bio;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by jmilton on 5/12/2016.
 */
public class FASTALoader {


//    hs_ref_GRCh38.p2_chr1.fa
    public static String loadSequence ( File _fastafile ) throws IOException {
        FileInputStream inputStream = new FileInputStream(_fastafile);
        Scanner sc = new Scanner(inputStream, "UTF-8");
        if (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith(">")) {
            }
        }
        int character_count = 0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            character_count += line.length();
        }

        Charset encoding = Charset.defaultCharset();
        Reader reader = new InputStreamReader(new FileInputStream(_fastafile), encoding);
        char[] cs = new char[character_count+1];
        int r = -1;
        int i = 0;
        boolean start_count = false;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            if (start_count && ch != '\n' && ch != '\r' ) {
                if ( i >= cs.length )
                {
                    System.out.println ( " out of bounds " + i);
                    i++;
                }else {
                    cs[i] = ch;
                    i++;
                }
            }
            else if (ch == '\n') {
                start_count = true;
            }
        }
        String sequence = new String(cs);
        System.out.println ( "string length : " + sequence.length());
        String firstset = sequence.substring(0, 10);
        String lastset = sequence.substring(sequence.length()-10);
        System.out.println( " first : " + firstset );
        System.out.println( " last : " + lastset );
        System.out.println ( " ------ " );
        return sequence;
    }

}
