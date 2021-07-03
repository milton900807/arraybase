package com.arraybase.modules;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import clinic.gene.shell.genomics.Util;
import com.arraybase.*;
import com.arraybase.flare.DBProcessFailedException;
import com.arraybase.flare.GBJobListener;
import com.arraybase.flare.SQLToSolr;
import com.arraybase.io.ABQFile;
import com.arraybase.io.GBFileManager;
import com.arraybase.lac.LAC;
import com.arraybase.db.util.NameUtiles;
import com.arraybase.db.util.SourceType;
import com.arraybase.shell.cmds.NodePropertyType;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import org.json.JSONException;
import org.json.JSONObject;

import javax.script.*;

public class BuildTableFromABQFile implements GBModule {

    public void exec(List<String> l) throws UsageException {
        throw new UsageException(
                " This method is currently not implemented... please use exec(Map<String, Object> ");
    }

    /**
     * This will create a table from a set of parameters defined in the abq
     * file.
     */
    public void exec(final Map<String, Object> l) throws UsageException {
        final Map<String, String> config = new HashMap<String, String>();
        for (String keys : l.keySet()) {
            String value = (String) l.get(keys);
            config.put(keys, value);
        }
        String job_id = (String) l.get("job_id");
        final String user = (String) l.get(ABQFile.USER);
        final String query = (String) l.get(ABQFile.QUERY);
        final String export = (String) l.get(ABQFile.EXPORT);
        if (job_id == null) {
            job_id = query.toString() + new Date().toString();
        }

        // create the new core object
        final String path = config.get(ABQFile.NODE_PATH);

        final String core = NameUtiles.convertToValidCharName(path);
        final String link = LAC.getLink(core, "search", "*:*");
        // set this object as the loader in the config.
        config.put(NodePropertyType.MODULE.name(), this.getModName());

        String pattern = "^[a-zA-Z0-9_]*(s*)\\(.*\\)s*";
        if (Pattern.compile(pattern).matcher(query).find()) {
            System.out.println(" ---- ");

            int start_ind = query.indexOf('(');
            int end_index = query.indexOf(')');
            String st = query.substring(start_ind + 1, end_index);

//            http://ionprod:38383/lionrest/load

//            let js = { "user_id": user_id, "rule_name": rule_name };
            JSONObject jb = new JSONObject();
            try {
                jb.put("user_id", "user");
                jb.put("rule_name", "rule_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Util.readJsonFromPOSTUrl("http://ionprod:38383/lionrest/load/", jb);


            ScriptEngine js = new ScriptEngineManager().getEngineByName("javascript");
            Bindings bindings = js.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("stdout", System.out);


            String polyfills = "var global = this;\n" +
                    "var window = this;\n" +
                    "var process = {env:{}};\n" +
                    "\n" +
                    "var console = {};\n" +
                    "console.debug = print;\n" +
                    "console.log = print;\n" +
                    "console.warn = print;\n" +
                    "console.error = print; ";
            try {
                js.eval(polyfills + "\n" +
                        "console.log ( 'hello world ' );\n" +
                        "console.log((Math.PI));");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            // Prints "-1.0" to the standard output stream.


        } else if (query.startsWith("select ")) {
            try {
                new SQLToSolr()
                        .run(user, path, "" + query, config, null, query, job_id, new GBJobListener() {
                            public void jobComplete(String msg) {
                                GB.print(" Job complete");
                                String final_operation = (String) l.get("final-operation");
                                if (final_operation != null) {
                                    String[] pg = final_operation.split(":");
                                    if (pg.length > 1) {
                                        GB.setVariable(pg[0], new GBV(true));
                                    }
                                }
                            }
                        });

            } catch (DBProcessFailedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (query == null || query.length() <= 0) {
                    throw new UsageException(" Please provide an export in the abq file ");
                }
                new DocumentStoreToSolr().run(user, path, core, export, config, null, query, job_id, new GBJobListener() {
                    public void jobComplete(String msg) {
                    }
                });
            } catch (DBProcessFailedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getModName() {
        return this.getClass().getCanonicalName();
    }
}
