package com.arraybase.plugin;

import java.util.LinkedHashMap;

public class ABQParams {

    private LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object> ();

    public LinkedHashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(LinkedHashMap<String, Object> values) {
        this.values = values;
    }

    public void append(String next, Object value) {
        values.put ( next, value );
    }

    public float getFloat ( String key ) throws NumberFormatException {
        Object v = values.get(key);
        if (v instanceof Double) {
            Double d = (Double) v;
            return d.floatValue();
        } else if (v instanceof Float) {
            Float f = (Float) v;
            return f;
        } else if (v instanceof Integer) {
            Integer f = (Integer) v;
            return f.floatValue();
        }
        Float ftl = Float.parseFloat(v.toString());
        return ftl;
    }
    public String getString(String key) {
        Object v = values.get ( key );
        if ( v != null )
        {
            return v.toString();
        }
        else
        {
            return null;
        }
    }
}
