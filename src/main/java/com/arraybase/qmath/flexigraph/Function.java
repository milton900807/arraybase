package com.arraybase.qmath.flexigraph;


public abstract class Function {


    public int NUM_ARGUMENTS = 1;
    public double xmin;
    public double xmax;
    public double ymin;
    public double ymax;
    public double step;

    public String xLabel;
    public String yLabel;
    double a, b;

    public void setValue(double a){
       this.a = a;
    }
    public void setValue(double a, double b){
        this.a = a;
        this.b = b;
    }


    public abstract double apply(double a);

    public void setFunctionRange(Convert c){
        xmax = c.xmax;
        xmin = c.xmin;
        ymax = c.ymax;
        ymin = c.ymin;
    }


    public abstract double apply(double a, double b);

    public abstract String toString();

}