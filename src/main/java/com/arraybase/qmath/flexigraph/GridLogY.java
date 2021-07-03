package com.arraybase.qmath.flexigraph;



/****************************************************
 *@@author D. Jeff Milton
 ***************************************************/
public class GridLogY extends Grid
{

	float logvalue = 0.434294f;
	float expvalue = 10;
	
	public GridLogY ( float _xmin, float _ymin, float _xmax, float _ymax )
	{
		super ( _xmin, _ymin, _xmax, _ymax );
	}
	
	
	
	public void rescaleY ( float newymin, float newymax )
	{
	  setymin ( (float) (logvalue*Math.log( newymin )) );
	  setymax ( (float) (logvalue*Math.log( newymax )) );
	  float yAxisLength = Math.abs(ymax - ymin);
	  yscale = ((height - (2 * getYInset ()))/( yAxisLength));
	  yshift = -ymin;
	}
	public void rescaleY ( float yAxisLength )
	{
	  yscale = ((height - (2 * getYInset ()))/( yAxisLength));
	  yshift = -ymin;
	}		
	
	
        public void rescale(float xAxisLength, float yAxisLength)
        {
            rescaleX ( xAxisLength );
            rescaleY ( yAxisLength );
        }
 
 
 
 
 
	public float Ywc ( int _y )
	{
		float yAxisValue;
		//ywc = (float)(yLength-((ysc-yinset+yi)/yscale));//needs fixed.  (yshift)
		yAxisValue = ((( _y- getYInset () + yi ) / yscale ) - yshift );
		return (float) Math.exp( yAxisValue*Math.log ( expvalue ) );
	}
	
	
	
	public int Y ( float _y ){
	  float y;  
	      //ysc = ((yLength - (ywc + yshift)) * yscale + yinset) + yi;
		   // xsc = ((xwc + xshift) * xscale + xinset) + xi;
			
	  y = (float) (( logvalue * Math.log( _y ) + yshift * yscale )+ getYInset () + yi);
	    return Math.round( y );
	}  
}
