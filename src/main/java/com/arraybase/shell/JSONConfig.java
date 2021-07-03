package com.arraybase.shell;

import clinic.gene.shell.genomics.LoadGTF;
import com.arraybase.GB;
import com.arraybase.GBV;
import com.arraybase.modules.UsageException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JSONConfig implements com.arraybase.GBPlugin {
    public String exec(String command, String variable_key) throws UsageException {
//        {
//            "mode":"loadgtf",
//                "max.threads":1,
//                "batch.size":5000,
//                "solr.host":"http://arraybase:8080/",
//                "gtf-file":""
//        }
        if (command!= null && command.length() > 0) {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(command)) {
                JSONObject obj = null;
                try {
                    obj = (JSONObject) jsonParser.parse(reader);
                    Set<String> keys = obj.keySet();
                    for (String key : keys) {

                        if ( key.equalsIgnoreCase("mode"))
                        {
                            String mode = (String) obj.get("mode");
                            if ( mode.equalsIgnoreCase( "loadgtf")){
                               String gtf = (String) obj.get ("file");
                               String corpath = (String) obj.get ("path");
                                LoadGTF lg = new LoadGTF();
                                lg.exec("loadgtf " + gtf + " " + corpath, null);
                            }
                        }
                        System.setProperty(key, obj.get(key).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        System.out.println ( " loading the file : " + command );



        return null;
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}
