package com.arraybase.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import com.arraybase.*;
import com.arraybase.io.ABQFile;
import com.arraybase.modules.BuildCSVFromABQFile;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;

public class PrintToFile implements GBPlugin {

    public String exec(String cmd, String variable_key)
            throws UsageException {
        String[] sp = cmd.split("\\s+");
        String abq = null;
        for (String s : sp) {
            if (s.toLowerCase().endsWith(".abq")) {
                abq = s;
                break;
            }
        }
        String file_name = sp[sp.length - 1];
        if (file_name == null) {
            file_name = "ab_export.csv";
        }
// String u, String abq_file, String gb_file, String jobid, String final_operation)
        String user = "NA";
        try {
            PrintToFile.loadABQ(user, abq, file_name, "900807", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out
                .println("PRINT TO FILE COMMAND IS NOT COMPLETE AS A PREFIX command... however, you're welcome to pipe stuff into this.");
        return "Complete";
    }

    public GBV execGBVIn(String cmd, GBV input) {
        try {
            String[] sp = cmd.split("\\s+");
            if (sp.length != 2) {
                GB.print("Please provide a filename argument.");
                return null;
            }
            String file_name = sp[1];
            if (file_name == null) {
                file_name = "ab_export.csv";
            }

            String delim = "\t";
            Object object = input.get();
            File filename = new File(file_name.trim());
            if (filename.getName().endsWith(".csv")) {
                delim = ",";
            }
            if (object instanceof GBSearchIterator) {
                GB.print("Exporting to" + filename.getAbsolutePath());
                PrintStream pr = new PrintStream(filename);
                GBSearchIterator it = (GBSearchIterator) object;
                while (it.hasNext()) {
                    ArrayList<LinkedHashMap<String, Object>> values = it.next();
                    for (LinkedHashMap<String, Object> map : values) {
                        String line = "";
                        Set<String> keys = map.keySet();
                        for (String key : keys) {
                            Object value = map.get(key);
                            line += value.toString() + delim;
                        }
                        line = line.substring(0, line.length() - 1);
                        pr.println(line);
                    }
                }
                GB.print("Export complete:\t" + filename.getAbsolutePath());
                pr.close();
            } else if (object instanceof ABaseResults) {
                print((ABaseResults) object, filename, delim);
            }
            return null;
        } catch (IOException _e) {
            System.out.println("Problem printing to  " + cmd);
            _e.printStackTrace();
        }
        return null;
    }

    /**
     * Load a sql descriptor file.
     */
    public static void loadABQ(String u, String abq_file, String gb_file, String jobid, String final_operation)
            throws IOException, UsageException {
        File f = new File(abq_file);
        GB.print("Loading " + f.getAbsolutePath());
        Properties paf = ABQFile.load(f);
        Map<String, Object> af = convert(paf);
        if (gb_file == null || gb_file.length() <= 0) {
            String path = (String) af.get(ABQFile.NODE_PATH);
            if (path == null || path.length() <= 0)
                throw new UsageException("Arraybase node path was not found in the command line or the abq file.  Please provide this before loading.");
            gb_file = path;
        }
        String url = (String) af.get(ABQFile.URL);
        af.put("path", gb_file);
        af.put("gbuser", u);
        af.put("job_id", jobid);
        af.put("url", url);
        af.put("final-operation", final_operation);


        String query = paf.getProperty("query");
        if (query == null || query.length() <= 0) {
            GB.print(" Please provide a query syntax... ");
            return;
        }
        if (query.toLowerCase().startsWith("select ")) {
            GBModule mod = GBModuleBuildFactory.create(GBModule.ABQ_FOR_CSV, null);
            if (mod == null) {
                throw new UsageException("Please provide a select statement for the search ");
            } else {
                GB.print(" Mod loaded " + mod.getModName());
            }
            mod.exec(af);
        } else {

            System.out.println(" not implemented for this mode ");
        }
    }

    /**
     * Conver the properties object to a Map. utility function
     *
     * @param paf
     * @return
     */
    private static Map<String, Object> convert(Properties paf) {
        Map<String, Object> v = new HashMap<String, Object>();
        Set k = paf.keySet();
        for (Object s : k) {
            String ss = s.toString();
            String value = paf.getProperty(ss);
            v.put(ss, value);
        }
        return v;
    }


    private void print(ABaseResults object, File filename, String delim)
            throws FileNotFoundException {
        PrintStream pr = new PrintStream(filename);
        GB.print("Printing to  " + filename.getAbsolutePath());
        GBIO.printFacets(object, pr, delim);
        pr.close();
    }

}
