package com.arraybase.qmath.flexigraph;


public class Convert {

  int specMax;

  int width;
  int height;


  protected int inset = 20;
  double yLength;

  double xscale = 1;
  double yscale = 1;
  double xshift = 0;
  double yshift = 0;


  public double xmin = 0;
  public double ymin = 0;

  public double xmax = 1;
  public double ymax = 1;

public Convert(){
  
  yLength = ymax-ymin;
}
   /************************************************************************
   *  For converting Screen coordinates to World coordinates              *
   ***********************************************************************/
  protected double Xscreen_world(int x){
    int xAxisValue;
    xAxisValue = (int)(((x-inset)/xscale)-xshift);
    return xAxisValue;
  }
  protected double  Yscreen_world(int screenval){
    int yAxisValue;
    yAxisValue = (int)(yLength -  ((screenval - inset)/yscale));
    return yAxisValue;
  }
  /************************************************************************
   *  For converting World coordinates to Screen coordinates              *
   ***********************************************************************/


  protected int X(double xAxisValue){
    double x;

    x = (xAxisValue + xshift) * xscale + inset;
    return(int)(Math.round(x));
  }

  protected int Y(double yAxisValue){
    double y;

    y = (yLength - (yAxisValue + yshift)) * yscale + inset;
    return(int)(Math.round(y));
  }

  /************************************************************************
   *   rescaling methods: for determining the values of the xscale        *
   *   and the yscale                                                     *
   ***********************************************************************/

  public void rescale(double xAxisLength, double yAxisLength){
    if (yAxisLength < 0)
      yAxisLength *= -1;

    yLength = yAxisLength;

    xscale = (width - (2 * inset))/ (xAxisLength);
    yscale = (height - (2 * inset))/ (yAxisLength);
    xshift = -xmin;
    yshift = -ymin;

  }
  public double  rescaleY(double ymin, double ymax){
    this.ymin = ymin;
    this.ymax = ymax;
    yLength = ymax-ymin;
    yscale = (height - (2 * inset))/ (yLength);
    yshift = -ymin;
    return yscale;
  }
  public void rescaleX(double xAxisLength){
    xscale = (width - (2 * inset))/(xAxisLength);
    xshift = -xmin;
  }
  public void rescaleX(){
    rescaleX(Math.abs(xmax - xmin));
  }

public double getXMin(){
    return xmin;
}

public double getXMax(){
    return xmax;
}

public double getYMin(){
    return ymin;
}

public double getYMax(){
    return ymax;
}

public int getInset(){
    return inset;
}

public void setInset(int newset){
    this.inset = newset;
}
public void setDimension(Dimension d){
  this.width = d.width;
  this.height = d.height;
}
public void setYMax(double newymax){
    this.ymax = newymax;
}
public void setYMin(double newymin){
    this.ymin = newymin;
}
public void setHeight(int height){
    this.height = height;
}

public int getYMaxSc(){
    int y =  Y(ymax);
    return y;
}

public int getYMinSc(){
    int y = Y(ymin);
    return y;
}

public int getXMaxSc(){
    int x = X(xmax);
    return x;
}
public int getXMinSc(){
    int x = X(xmin);
    return x;
}




}




