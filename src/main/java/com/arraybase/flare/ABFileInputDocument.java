package com.arraybase.flare;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ABFileInputDocument {
    private Map<String, Object> objects = new HashMap<String, Object>();

    public void addField(String k, Object v) {
        objects.put ( k, v );
    }

    public Set<String> getFieldNames() {
        return objects.keySet();
    }

    public Object get(String na) {
        return objects.get(na);
    }
}
