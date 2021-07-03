package clinic.gene.shell.genomics;

import com.arraybase.*;
import com.arraybase.bio.FASTALoader;
import com.arraybase.bio.FASTATableAppend;
import com.arraybase.db.NodeExistsException;
import com.arraybase.modules.UsageException;
import com.arraybase.net.Download;
import com.arraybase.net.FTPImporter;
import com.arraybase.util.FileUnzipper;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class LoadGTF implements GBPlugin {


    static String[] STANDARD_FIELDS = {"seqname", "source", "feature", "start", "end", "score", "strand", "frame", "attribute",
            "gene_name", "gene_biotype", "exon_number", "transcript_id", "gene_id"};


    public void load(File f, String path) {
        // get the frst line:

        if ( f.getName().endsWith( ".gz")){
            File directory = new File (f.getParent() );
            FileUnzipper.unzip(f.getAbsolutePath(),directory.getAbsolutePath());
            String rfile = f.getAbsolutePath().substring(0, f.getAbsolutePath().length()-3);
            File unzipedFile = new File (rfile);
            System.out.println (" File : " + f.getAbsolutePath()  );
            f = unzipedFile;
        }




        try {
            ABTable table = new ABTable(path);
            Map<String, String> schema = new LinkedHashMap<String, String>();
            schema.put("seqname", "string_ci");
            schema.put("source", "string_ci");
            schema.put("feature", "string_ci");
            schema.put("start", "sint");
            schema.put("end", "sint");
            schema.put("strand", "string_ci");
            schema.put("gene", "string_ci");
            schema.put("feature_type", "string_ci");
            schema.put("chromosome", "string_ci");
            schema.put("exon", "string_ci");
            schema.put("transcript_id", "string_ci");
            schema.put("gene_id", "string_ci");
            if (!table.exists()) {
                table.create(schema);
            }
            FileInputStream inputStream = new FileInputStream(f);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            String line = sc.nextLine();
            ArrayList<LinkedHashMap<String, Object>> av = new ArrayList<LinkedHashMap<String, Object>>();
            int line_index = 0;
            while (sc.hasNextLine()) {
                LinkedHashMap<String, Object> values = new LinkedHashMap<>();
                if (line.startsWith("#")) {
                } else {
                    String[] l = line.split("\t");
                    String[] a = l[l.length - 1].split(";");
                    values.put("seqname", l[0]);
                    values.put("chromosome", l[0]);
                    values.put("source", l[1]);
                    values.put("feature", l[2]);
                    values.put("start", l[3]);
                    values.put("end", l[4]);
                    values.put("strand", l[6]);

                    for (String attribute : a) {
                        attribute = attribute.trim();
                        int in = attribute.indexOf(' ');
                        String key = attribute.substring(0, in);
                        key = key.trim();
                        String value = attribute.substring(in + 1).trim();
                        if (value.startsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (key.equalsIgnoreCase("gene_name")) {
                            values.put("gene", value);
                        } else if (key.equalsIgnoreCase("gene_biotype")) {
                            values.put("feature_type", value);
                        } else if (key.equalsIgnoreCase("gene_id")) {
                            values.put("gene_id", value);
                        } else if (key.equalsIgnoreCase("exon_id")) {
                            values.put("exon", value);
                        }
                    }


//                   System.out.println( " line " + line );

                }
                av.add(values);


                if (av.size() % 100000 == 0) {
                    table.append(av);
                    av = new ArrayList<>();
                }


                GB.print("l\t" + line_index++);
                line = sc.nextLine();
            }

            if (av.size() > 0) {
                table.append(av);
                av = new ArrayList<>();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NodeWrongTypeException e1) {
            e1.printStackTrace();
        } catch (NodeExistsException e1) {
            e1.printStackTrace();
        }
    }
    public static void main(String[] args) {
        File f = new File("./annotations.gtf");
        LoadGTF l = new LoadGTF();
        l.load(f, "/annotations/grch38/features");
    }
    public String exec(String command, String variable_key) throws UsageException {
        String[] parms = command.split(" ");
        String gtffile = parms[1];
        String abpath = parms[2];
        if (gtffile.startsWith("ftp") || gtffile.startsWith("http") || gtffile.startsWith("https")) {
            File f = Download.downloadToTemp(gtffile);
            if ( f.getName().endsWith(".gz")){
                f = Download.gunzipIt(f);
            }
            load(f, abpath);
        } else {
            GB.print(" loading GTF " + gtffile);
            File f = new File(gtffile);
            load(f, abpath);
        }
        return "Complete";
    }
    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
