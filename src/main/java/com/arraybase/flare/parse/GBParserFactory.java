package com.arraybase.flare.parse;

import com.arraybase.ABTable;
import com.arraybase.db.NodeExistsException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GBParserFactory {

    public static GBParser makeParser(String type) {

        if (type.equalsIgnoreCase("application/pdf")) {
            return new PDFGBParser();
        } else if (type
                .equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return new WordDocumentGBParser();
        } else if (type.equalsIgnoreCase("application/vnd.ms-powerpoint")) {
            return new PowerPointGBParser();
        } else if (type.equals("text/csv")) {
            return new CSVGBParse();
        } else if (type.equals("application/vnd.ms-excel")) {
            return new XLSGBParser();
        }
        return new DefaultGBParser();
    }


    public static void main(String[] args) {
        HashMap<String, String> stats_schema = new HashMap<String, String>();
        stats_schema.put("location", "string_ci");
        stats_schema.put("doc", "string_ci");
        stats_schema.put("title", "string_ci");

        File directory = new File("\\\\isis.local\\ogroups\\Antisense Drug Discovery\\Literature");///isis.local/ogroups/Antisense Drug Discovery/Literature");
        File[] l = directory.listFiles();
        for (File f : l) {
            try {
                if (f.isFile()) {
                    String example = parseExample(f);

                    System.out.println(" parse " + example);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TikaException e) {
                e.printStackTrace();
            }
        }
//        ABTable library = new ABTable("/library/literature");
//        try {
//            library.create(stats_schema);
//        } catch (NodeExistsException e) {
//            e.printStackTrace();
//        }
    }

    public static String parseExample(File f) throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        try (InputStream stream = new FileInputStream(f)) {
            parser.parse(stream, handler, metadata);

           String[] names = metadata.names();

           for ( String nam : names )
           {
               if ( nam.equalsIgnoreCase("title")){
                   String title = metadata.get(nam);
                   System.out.println ( " title " + title );
               }


               System.out.println( " name " + nam );
           }
            return handler.toString();
        }
    }
}
