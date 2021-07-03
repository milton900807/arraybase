package com.arraybase.shell.interactive;

import java.io.*;
import java.util.List;

import com.arraybase.GB;
import com.arraybase.GBNodes;
import com.arraybase.GBVariables;
import com.arraybase.shell.FieldNodeCommands;
import com.arraybase.shell.GBCommand;
import com.arraybase.shell.iterminal.c.ConsoleReader;
import com.arraybase.shell.iterminal.c.autocmplt.Completer;
import com.arraybase.shell.iterminal.c.history.FileHistory;
import com.arraybase.shell.iterminal.c.interal.Configuration;
import com.arraybase.tm.FieldNode;
import com.arraybase.tm.NodeManager;
import com.arraybase.tm.tree.TNode;

public class StandardRunner implements Runnable {
    private GBCommandCenterListener cclistener = null;

    public StandardRunner(GBCommandCenterListener _command_center) {
        cclistener = _command_center;
    }


    public void run() {
        try {
            final ConsoleReader reader = new ConsoleReader(
                    new BufferedInputStream(System.in),
                    new BufferedOutputStream(System.out));
            // final ConsoleReader reader = new ConsoleReader( );
            GB.registerConsoleReader ( reader );
            String historyFileName = ".ab.history";
            reader.setHistory(new FileHistory(new File(".",
                    String.format(".jline-%s.%s.history", reader, historyFileName))));

            reader.setPrompt("AB-> ");
            reader.addCompleter(new Completer() {
                public int complete(String buffer, int cursor,
                                    List<CharSequence> candidates) {
                    GBCommand cc = GB.getCommands();
                    cc.printHint(buffer, reader);
//                    try {
//                        reader.redrawLine();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return 1;
                }
            });


            String line = "";
            PrintWriter out = new PrintWriter(System.out);
            GBCommand command_center = GB.getCommands();
            String current_path = null;
            while ((line = reader.readLine()) != null && GB.INTERACTIVE) {
                out.flush();
                if (line.endsWith("\t") || line.endsWith("*")) {
                    reader.complete();
                } else {
                    Character mask = null;
                    String trigger = null;
                    // If we input the special word then we will mask
                    // the next line.
                    if ((trigger != null) && (line.compareTo(trigger) == 0)) {
                        line = reader.readLine("password> ", mask);
                    }
                    if (line.equalsIgnoreCase("quit")
                            || line.equalsIgnoreCase("exit")) {

                        reader.flush();
                        System.exit(1);
                        break;
                    }
                    if (line.trim().equals("?")) {
                        // output.output(String.format(HINT_FORMAT,
                        // appName),
                        // outputConverter);
                    } else if (line
                            .matches("[A-Za-z]+\\s*=\\s*[A-Za-z]+\\s*")) {
                        GBVariables.setVariable(line);
                    } else if (command_center.matches(line)) {
                        // System.out.println ( line );
                        command_center.exec(line, null);
                    } else {
                        String[] re = GBNodes.getNodeNames(GB.pwd());
                        boolean found_file = false;
                        if ( re != null && re.length > 0 ) {
                            for (String f : re) {
                                if (line.toLowerCase().startsWith(f.toLowerCase())) {
//                                    reader.getCursorBuffer().write(f);
                                    found_file = true;
                                }
                            }
                        }
                        if ( !found_file )
                        {
                            GBCommand gbCommand = new GBCommand();
                            gbCommand.exec(line, null);
                        }


//                        if (!found_file) {
//                            GB.gogb(GB.parseLine(line));
//                        }
                    }


                    // {{ IF WE HAVE CHANGED PATH AS A RESULT OF THE COMMAND }}
                    // {{ SEE IF THE COMMAND STRUCTURE SHOULD CHANGE. }}
                    String path = GB.pwd();
                    if (current_path == null || (!path.equals(current_path))) {
                        // GET THE COMMAND SET BASED ON THE CONTEXT
                        command_center = getCommands(path);
                        current_path = path;
                    }


                }


            }


        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }

    private NodeManager node_manager = new NodeManager();

    private GBCommand getCommands(String current_path) {
        TNode node = node_manager.getNode(current_path);
        if (node instanceof FieldNode) {
            System.out.println(" field noe command center ");
            GBCommand field_node_commands = new FieldNodeCommands();
            updateCommand(field_node_commands);
            return field_node_commands;
        } else
            return GB.getCommands();
    }


    private void updateCommand(GBCommand commands_new) {
        cclistener.updateCommandCenter(commands_new);

    }
}