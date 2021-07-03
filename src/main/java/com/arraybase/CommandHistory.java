package com.arraybase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class CommandHistory {

    private PrintStream printstream = null;

    public void init ()
    {
        String home = System.getProperty("user.home");
        File f = new File(home, ".ab.history");
        if ( f.exists() )
        {
            try {
                this.printstream = new PrintStream( new FileOutputStream( f ));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void append ( String command ) {
        if ( this.printstream != null  ){
            this.printstream.println ( command );
        }
    }

    public void close ()
    {
        if ( this.printstream != null )
        this.printstream.close();

    }



}
