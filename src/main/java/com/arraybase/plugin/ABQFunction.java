package com.arraybase.plugin;

public interface ABQFunction {
    public static String FLOAT = "FLOAT";
    public static String STRING = "STRING";

    public String eval (ABQParams obj );
    public String getType ( );
    public float evalFloat ( ABQParams obj ) throws NumberFormatException;
}