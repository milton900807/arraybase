package com.arraybase;

import com.arraybase.lang.BasicVariableSetConditional;
import com.arraybase.lang.conditionals.TrueConditional;
import com.arraybase.modules.UsageException;
import com.arraybase.shell.GBCommand;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class MethodTrigEngine implements Runnable {
    private Thread t = null;
    private LinkedHashMap<MethodCondition, List<String>> mc = new LinkedHashMap<MethodCondition, List<String>>();
    private boolean engine = true;

    public MethodTrigEngine(List<String> _cmds) {
        buildMC(_cmds);
    }

    /**
     * This parses the command so we can have conditionals at each operation
     *
     * @param cmds
     */
    private void buildMC(List<String> cmds) {
        MethodCondition m = null;
        for (int i = 0; i < cmds.size(); i++) {
            String c = cmds.get(i);
            if (i == 0 || c.startsWith("&&")) {
                ArrayList<String> mcommands = new ArrayList<String>();
                if (i == 0 && (!c.startsWith("&&"))) {
                    m = new TrueConditional();
                    mcommands.add(c);
                } else {
                    m = buildCondition(c);
                }
                i++;
                c = cmds.get(i);
                while (i < cmds.size() && (!c.startsWith("&&"))) {
                    mcommands.add(c);
                    if (i + 1 < cmds.size()) {
                        c = cmds.get(++i);
                    } else {
                        i++;
                    }
                    if (c.startsWith("&&"))
                        i--;
                }

                mc.put(m, mcommands);
                m = null;
            }
        }
    }

    private MethodCondition buildCondition(String c) {
        String conditional = c.substring(2).trim();
        if (conditional.matches("[A-Za-z0-9_]*")) {
            MethodCondition basic = new BasicVariableSetConditional(c);
            return basic;
        }
        return new TrueConditional();
    }

    public void run() {
        while (engine) {
            Set<MethodCondition> mset = mc.keySet();
            ArrayList<MethodCondition> executed = new ArrayList<MethodCondition>();
            for (MethodCondition m : mset) {
                if (m.evalCondition()) {
                    List<String> cmds = mc.get(m);
                    GB.print(" Executing operation " + cmds);
                    try {

                        String command = stitchCommand(cmds);
                        GBCommand gbCommand = new GBCommand();
                        gbCommand.exec(command, null);


//                        GB.gogb(cmds.toArray(new String[cmds.size()]));
                        executed.add(m);
                    } catch (UsageException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (MethodCondition m : executed) {
                this.mc.remove(m);
            }
            if (this.mc.size() == 0) {
                GB.print(" Method triggers complete ");
                return;
            }
            try {
                mset = mc.keySet();
                for (MethodCondition nm : mset) {
                    GB.print(" Waiting on " + mc.get(nm));
                }
                t.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String stitchCommand(List<String> cmds) {
        String t = "";
        for (String b : cmds) {
            t += b + ' ';
        }
        return t.trim();
    }

    public void start() {
        this.t = new Thread(this);
        t.start();
    }
}
