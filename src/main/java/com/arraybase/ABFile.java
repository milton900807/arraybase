package com.arraybase;

import com.arraybase.db.NodeExistsException;
import com.arraybase.flare.ABFileInputDocument;
import com.arraybase.flare.TMID;
import com.arraybase.flare.TMSolrServer;
import com.arraybase.modules.BuildTableFromABQFile;
import com.arraybase.modules.UsageException;
import com.arraybase.search.ABaseResults;
import com.arraybase.tab.ABFieldType;
import com.arraybase.tm.*;
import com.arraybase.tm.tables.GBTables;
import com.arraybase.tm.tree.NodeProperty;
import com.arraybase.tm.tree.TNode;
import com.google.gson.Gson;
import org.apache.hadoop.fs.PathNotFoundException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ABFile {

    private File file = null;
    private String url = null;
    private String path = null;
    private Map<String, String> schema = new LinkedHashMap<String, String>();
    private String delim = "\t";


    public ABFile(String path, Map<String, String> schema) {
        this.path = path;
        this.schema = schema;

    }

    public Map<String, String> getSchema() throws NodeNotFoundException {
        return this.schema;
    }

    public ABFile copy(String _path) throws NodeExistsException, NodeNotFoundException {
        ABFile nt = new ABFile(_path, this.schema);
        nt.create(getSchema());
        return nt;
    }

    public void create(Map<String, String> schema) throws NodeExistsException {
        this.schema = schema;
        this.file = new File ( this.path );
    }
    public void create ( LinkedHashMap<String, Map<String, String>> f ){
        System.out.println ( " looking for the schema " );
    }

    public void recreate(Map<String, String> schema) {
        delete();
        try {
            create(schema);
        } catch (NodeExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void addField(ABFieldType type, String _name) {
    }

    public ABaseResults search(String q, String[] cols, int start, int total) {
        return null;
    }

    public void insert(String search_string, String field, final Object value) {
    }


    public void set(String tmid, String field, final Object value, boolean b) {
    }

    public void update(String search_string, final Map<String, Object> values) {
    }

    public boolean hasField(String field) {
        if (this.schema.get(field) != null && this.schema.get(field).toString().length() > 0) {
            return true;
        }
        return false;
    }

    public void append(Map<String, Object> constants, Map<String, Object> distinct, boolean _commit) {
    }


    public void append(Map<String, Object> map, boolean _commit) {
    }

    public boolean exists() throws NodeWrongTypeException {
        throw new NodeWrongTypeException("Now was found but is not a table. ");
    }


    public void append(ArrayList<LinkedHashMap<String, Object>> maplist) {
    }

    public void append(Iterator<ArrayList<LinkedHashMap<String, Object>>> itt) {
        while (itt.hasNext()) {
            ArrayList<LinkedHashMap<String, Object>> rows = itt.next();
            append(rows);
        }
    }

    public String getUrl() {
        return url;
    }

    public void append(LinkedHashMap<String, Object> list) {
        ArrayList<LinkedHashMap<String, Object>> alist = new ArrayList<LinkedHashMap<String, Object>>();
        alist.add(list);
        append(alist);
    }


    public void delete(String q) {
    }

    public void delete() {
    }

    public void replace(String search_string, String sortString, String field,
                        String from, String to) {

    }

    public void add(ArrayList<ABFileInputDocument> docs) {
        if ( this.file == null  ){
            this.file = new File ( this.path );
        }


        try {
            FileWriter pr = new FileWriter( this.file, true );
            if ( this.file.length() == 0)
            {
                // write the header
                Set<String> fs = this.schema.keySet();
                for( String a : fs )
                {
                    pr.write ( a );
                    pr.write ( delim);
                }
            }
            pr.write("\n");

            for ( ABFileInputDocument doc : docs )
            {

                Set<String> fields = this.schema.keySet();
                for ( String f : fields )
                {
                    Object val = doc.get ( f );
                    if ( val == null ){
                        val = "";
                    }
                    pr.write( val.toString() );
                    pr.write(delim);
                }
                pr.write('\n');
            }
            pr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setSchema(Map<String, String> file_schema) {
        this.schema = file_schema;
    }
}
