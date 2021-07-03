package com.arraybase.qmath.flexigraph;


/****************************************************
 * ConvertLogX: class to provide the convertion
 * from world space to screen coordinates and
 * and from screen coordinates to world space.
 * --> this is done to provide a log x axis
 *@@author D. Jeff Milton
 ***************************************************/
public class ConvertLogX extends Convert{

public void rescaleX(double newxmin, double newxmax){
  this.xmin = 0.43429448*Math.log(newxmin);
  this.xmax = 0.43429448*Math.log(newxmax);
  double xAxisLength = Math.abs(xmax - xmin);
  xscale = ((width - (2 * inset))/(xAxisLength));
  xshift = -xmin;
}
protected double Xscreen_world(int x){
  double xAxisValue;
  xAxisValue = (((x-inset)/xscale)-xshift);
  return Math.exp(xAxisValue*Math.log(10));
}
protected int X(double xAxisValue){
  double x;  
  x = (0.43429448*Math.log(xAxisValue) + xshift) * xscale + inset;
    return(int)(Math.round(x));
}  
public int getXMaxSc(){
  double x = (0.43429448*Math.log(xmax) + xshift) * xscale + inset;
  return(int)(Math.round(x));  
}
public int getXMinSc(){
  double x = (0.43429448*Math.log(xmin) + xshift) * xscale + inset;
  return(int)(Math.round(x));
}
}

