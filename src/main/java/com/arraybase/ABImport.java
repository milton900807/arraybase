package com.arraybase;

/**
 * Created by jmilton on 5/18/2015.
 */
public interface ABImport {
    public void setPath ( String _path );
    public void setUser ( String user );
    public void runImport ( String[] _args, ABImportProgress progress);
}
