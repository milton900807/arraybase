 package com.arraybase.qmath.flexigraph.converter;

public interface Converter{
    /**
     *  Converts to this object value
     */
    float convert(float _value);
    /**
     *  Converts from this object value
     */
    float revert(float _value);
}