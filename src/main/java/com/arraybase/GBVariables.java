package com.arraybase;


import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.arraybase.var.gbv.GBVTable;
import com.arraybase.io.parse.ColumnExtractor;
import com.arraybase.io.parse.ColumnNotFoundException;
import com.arraybase.io.parse.DelimNotFoundException;
import com.arraybase.lang.ABRef;
import com.arraybase.lang.FileRef;
import com.arraybase.modules.UsageException;

public class GBVariables {

    public static final String USAGE = "Available methods: 1) extractColumn 2) col\n";
    private HashMap<String, GBV> vars = new HashMap<String, GBV>();

    public void setVar(String va) {
        vars.put(new Date().getTime() + "", new GBV<String>(va));
    }

    public static void parse(String[] _args) {
        for (int i = 0; i < _args.length; i++)
            System.out.println(_args[i]);
    }

    public static void setVariable(String _vcommand) throws UsageException {
        GB.setVariable(_vcommand);
    }

    public Set<String> getSets() {
        return vars.keySet();
    }

    public GBV getVariable(String _cmd) {
        return vars.get(_cmd);
    }

    public Map<String, GBV> getVariables() {
        return vars;
    }

    /**
     * set the variables
     *
     * @param key
     * @param v
     */
    public void setVar(String key, GBV v) {
        vars.put(key, v);
    }

    /**
     * TODO: complete this framework..
     *
     * @param scommand
     * @throws CommandNotFoundException
     */
    public void setVar(String key, String scommand)
            throws CommandNotFoundException {
        if (scommand // this is a file loading method
                .matches("\\s*[A-Za-z]+\\s*\\(\\s*[A-Za-z0-9\\-\\._[\\s*,\\s*]]+\\s*\\)")) {
            int openi = scommand.indexOf("(");
            int closei = scommand.indexOf(")");
            String command = scommand.substring(0, openi);
            String value = scommand.substring(openi + 1, closei);
            command = command.trim();
            value = value.trim();
            try {
                if (value.contains(",")) {
                    String[] sp = value.split(",");
                    GBV var = extract(command, sp);
                    vars.put(key, var);
                } else {
                    String[] sp = new String[1];
                    sp[0] = value;
                    GBV var = extract(command, sp);
                    vars.put(key, var);

                }
            } catch (DelimNotFoundException e) {
                e.printStackTrace();
            } catch (ColumnNotFoundException e) {
                e.printStackTrace();
            }
        } else if (scommand.matches("[A-Za-z]+\\s*")) {
            vars.put(key, new GBV<String>(scommand));
        } else
            System.err.println(" failed to print ");
    }

    public void printVariable(String _key) {
        GBV g = vars.get(_key);
        if (g != null) {
            GB.print(g.toString());
        }
    }

    private GBV extract(String command, String[] sp)
            throws DelimNotFoundException, ColumnNotFoundException,
            CommandNotFoundException {
        if (command.equalsIgnoreCase("array")) {
            ColumnExtractor ext = new ColumnExtractor();
            String file = "";
            if (sp.length > 1) {
                for (int i = 1; i < sp.length; i++) {
                    String s = sp[i];
                    ext.configure(s);
                }
            }
            file = sp[0];
            return ext.extract(file);
        }
        throw new CommandNotFoundException("Could not find command : "
                + command);
    }

    private String parse(String s) {
        String[] sp = s.split("=");
        return sp[1].trim();
    }

    /**
     * @param file
     * @return
     */
    private GBV loadFile(String file) {
        File f = new File(file);

        return null;
    }

    public void printVariable(String[] _args) throws UsageException {
        if (_args.length != 2)
            throw new UsageException(
                    "Print command is not correct.  Please use:  print $variable_key");
        printVariable(_args[1]);
    }

    public void setVariable(String key, GBVTable table) {
        vars.put(key, table);
    }

    public static void set(String variable_name, String _method, String data) {
        if (_method.startsWith("file")) {
            FileRef fref = new FileRef(data);
            GB.setVariable(variable_name, fref);
        } else if (_method.startsWith("ab")) {
            ABRef abref = new ABRef(data);
            GB.setVariable(variable_name, abref);
        } else {
            ABRef abref = new ABRef(data);
            GB.setVariable(variable_name, abref);
        }
    }

    public static void printAll() {
        GB.print("All variables\n");
        GBVariables vars = GB.getVariables();
        Map<String, GBV> variables = vars.getVariables();
        Set<String> list = variables.keySet();
        for (String l : list) {
            GBV v = variables.get(l);
            GB.print(l + " " + v.get().toString());
        }
    }

    public static GBV get(String variable) {
        GBVariables vars = GB.getVariables();
        Map<String, GBV> variables = vars.getVariables();
        return variables.get(variable);
    }
}
