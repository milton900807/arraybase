package com.arraybase.flare;

import java.util.LinkedHashMap;
import java.util.Map;

public class FunctionFeatureValues {
    Map<String, Object> v = new LinkedHashMap<String, Object>();

    public FunctionFeatureValues(Map<String, Object> in) {
        this.v = in;
    }
    public Object get(String p) {
        return v.get(p);
    }
}
