package com.arraybase.qmath.flexigraph;


import com.arraybase.qmath.flexigraph.converter.Converter;
import com.arraybase.qmath.flexigraph.converter.Log_base;
/**
 *     App: FlexiGraph 1.0
 *    File: LogGrid.java
 *   Class: LogGrid
 *  Author: Jeff Milton, Dave Yaron
 *    Date: 09.19.97
 *	  Mod: 03.12.2000
 */

/* *
 * Addition of the xinset and yinsets...
 */

public class YScaledGrid extends Grid{

    private Converter converter = new Log_base(10);
    private float max=1;
    private float min =0;

  public YScaledGrid()
  {
  }
  /**
   *  Constructs a Grid with screen width and height.
   */
  public YScaledGrid(int _width, int _height)
  {
    super(_width, _height);
  }
   /**
    *  Constructs the Grid with the current shape
    */
  public YScaledGrid(int _xi, int _yi, int _width, int _height)
  {
    this(_width,  _height);
    this.xi = _xi;
    this.yi = _yi;
  }
  public YScaledGrid ( Grid _grid )
  {
    super ( _grid );
  }
  /**
   *  Constructs a Grid with the world coordinate range:
   *  <br>xmin, ymin, xmax, ymax
   *  <P>
   *  With Screen dimensions of width and height.
   */
  public YScaledGrid(float _xmin, float _ymin, float _xmax, float _ymax,
                                                int _width, int _height)
  {
    this(_width, _height);
    setxmax(_xmax);
    setymax(_ymax);
    setxmin(_xmin);
    setymin(_ymin);
  }
  /**
   *  Converts the 'y' screen coordinate to the corresponding log('y') world coordinate
   */
  public float  Ywc(int _ysc){
    float ywc = super.Ywc(_ysc);//get the world linear value
    return converter.revert(ywc);
  }

  /**
   *  Converts the log'y' world coordinate to the corresponding 'y' screen coordinate
   */
  public int Y(float ywc){
    float linearworld = converter.convert(ywc);//revert to linear
    return super.Y(linearworld);
  }
  /**
   *  Returns the world height for the screen height
   */
  public float worldHeight(float screenHeight)
  {
    float worldheight = super.worldHeight(screenHeight);//get the world value
    return  converter.revert(worldheight);//convert
  }
  /**
   *   Returns the screen height for the world height
   */
  public int screenHeight(float worldHeight)
  {
    float linearworldheight = converter.convert(worldHeight);//revert to linear
    return Math.round((yscale)*linearworldheight);
  }
  public void setymax(float _ymax)
  {
    max = _ymax;
    super.setymax(converter.convert(_ymax));//convert the range
  }
  public void setymin(float _ymin)
  {
    min = _ymin;
    super.setymin(converter.convert(_ymin));//convert the range
  }
  public float getymax()
  {
    return max;
    //return converter.revert(super.getymax());
  }
  public float getymin()
  {
    return min;
    //return converter.revert(super.getymin());
  }

}