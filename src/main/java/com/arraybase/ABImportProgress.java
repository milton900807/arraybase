package com.arraybase;

/**
 * Created by jmilton on 5/18/2015.
 */
public class ABImportProgress {

    public final static int START = 0;
    public final static int COMPLETE = 1;
    public final static int NOT_STARTED = -1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status = NOT_STARTED;
    public String current = null;
    public void update ( String current )
    {
        this.current = current;
    }
    public String getCurrentProgress ()
    {
        return this.current;
    }


}
