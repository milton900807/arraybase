package com.arraybase.qmath.flexigraph.converter;


public final class Log_base extends Object implements Converter
{
    float base;

    public Log_base(float _base)
    {
        this.base = _base;
    }
    public float convert(float _value)
    {
        if (_value <= 0.0)
            return 0.0f;
            
        if (base != 0.0 && _value !=0.0)
           return (float) (((float)Math.log(_value))/(Math.log(base)));
        else
           return 0.0f;
    }
    public float revert(float _logvalue)
    {
        if (base != 0.0 && _logvalue != 0.0)
            return (float) Math.exp((float)(_logvalue*Math.log(base)));
        else
            return 0.0f;
    }

}