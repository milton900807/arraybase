package com.arraybase.qmath.flexigraph;

import java.awt.Point;


//import android.graphics.Point;



//@PersistenceCapable
public class Grid implements java.io.Serializable {

	/*
	 * Initial 'x' screen coordinate
	 */
//	@Persistent
	protected int xi = 0;

	/*
	 * Initial 'y' screen coordinate
	 */
//	@Persistent
	protected int yi = 0;
	/*
	 * Width of the graph ( screen coordinates )
	 */
//	@Persistent
	protected int width;
	/*
	 * Height of the graph ( screen coordinates )
	 */
//	@Persistent
	protected int height;
	/*
	 * Insets ( screen coordinates )
	 */
//	@Persistent
	protected int xinset = 25;

//	@Persistent
	protected int yinset = 25;

	/*
	 * ymax - ymin; (world coordinates )
	 */
//	@Persistent
	protected float yLength;
	/*
	 * 'x' coordinate conversion ratio ( sc/wc )
	 */
//	@Persistent
	protected float xscale = 1;
	/*
	 * 'y' coordinate conversion ratio ( sc/wc )
	 */
//	@Persistent
	protected float yscale = 1;
	/*
	 * The current 'x' world coordinate displacement
	 */
//	@Persistent
	protected float xshift = 0;
	/*
	 * The current 'y' world coordinate displacement
	 */
//	@Persistent
	protected float yshift = 0;
	/*
	 * xmin ( world coordinates )
	 */
//	@Persistent
	protected float xmin = 0;
	/*
	 * ymin ( world coordinates )
	 */
//	@Persistent
	protected float ymin = 0;
	/*
	 * xmax (world coordinates )
	 */
//	@Persistent
	protected float xmax = 1;
	/*
	 * ymax (world coordinates )
	 */
//	@Persistent
	protected float ymax = 1;

	public Grid() {
	}

	/**
	 * Constructs a Grid with screen width and height.
	 */
	public Grid(int _width, int _height) {
		this.width = _width;
		this.height = _height;
	}

	/**
	 * Constructs the Grid with the current shape
	 */
	public Grid(int _xi, int _yi, int _width, int _height) {
		this(_width, _height);
		this.xi = _xi;
		this.yi = _yi;
	}

	/**
	 * Constructs a Grid with the world coordinate range: <br>
	 * xmin, ymin, xmax, ymax
	 * <P>
	 * With Screen dimensions of width and height.
	 */
	public Grid(float _xmin, float _ymin, float _xmax, float _ymax,
			int _width, int _height) {
		this(_width, _height);
		this.xmin = _xmin;
		this.ymin = _ymin;
		this.xmax = _xmax;
		this.ymax = _ymax;
	}

	public Grid(float _xmin, float _ymin, float _xmax, float _ymax) {
		this(_xmin, _ymin, _xmax, _ymax, 0, 0);
	}

	public Grid(Grid _grid) {
		resizeWorld(_grid.getxmin(), _grid.getymin(), _grid.getxmax(), _grid
				.getymax());
		setBounds(_grid.xi, _grid.yi, _grid.getWidth(), _grid.getHeight());
	}

	/**
	 * Returns the current dimensions
	 */
	public Dimension size() {
		return new Dimension(width, height);
	}

	/**
	 * Returns the current start screen coordinate
	 */
	public Point point() {
		return new Point(xi, yi);
	}

	/**
	 * Resizes the screen coordinates to the new dimensions
	 */
	public void setDimension(int _width, int _height) {
		this.width = _width;
		this.height = _height;
	}

	/**
	 * Resizes the screen coordinates to the new dimensions
	 */
	public void setSize(Dimension d) {
		this.width = d.width;
		this.height = d.height;
	}

	/**
	 * Reshapes all the screen coordinates
	 */
	public void setBounds(int x, int y, int w, int h) {
		this.width = w;
		this.height = h;
		this.xi = x;
		this.yi = y;
		rescale();
	}

	/**
	 * Converts the 'x' screen coordinate to the corresponding 'x' world
	 * coordinate
	 */
	public float Xwc(int xsc) {
		float xwc;
		xwc = ((xsc - xinset + xi) / xscale) - xshift;
		return xwc;
	}

	/**
	 * Converts the 'y' screen coordinate to the corresponding 'y' world
	 * coordinate
	 */
	public float Ywc(int ysc) {
		float ywc;
		ywc = (yLength - ((ysc - yinset + yi) / yscale)) - yshift;// needs
		// fixed.
		// (yshift)
		// jmCS1.001212002
		// this should be fixed
		return ywc;
	}

	/**
	 * Converts the 'x' world coordinate to the corrsponding 'x' screen
	 * coordinate
	 */
	public int X(float xwc) {
		float xsc;
		xsc = ((xwc + xshift) * xscale + xinset) + xi;
		return Math.round(xsc);
	}

	/**
	 * Converts the 'y' world coordinate to the corresponding 'y' screen
	 * coordinate
	 */
	public int Y(float ywc) {
		float ysc;
		ysc = ((yLength - (ywc + yshift)) * yscale + yinset) + yi;
		return Math.round(ysc);
	}

	/**
	 * Returns the world height for the screen height
	 */
	public float worldHeight(float screenHeight) {
		return (1 / yscale) * screenHeight;
	}

	/**
	 * Returns the world width for the screen height
	 */
	public float worldWidth(float screenWidth) {
		return (1 / xscale) * screenWidth;
	}

	/**
	 * Returns the screen height for the world height
	 */
	public int screenHeight(float worldHeight) {
		return Math.round((yscale) * worldHeight);
	}

	/**
	 * Returns the screen width for the world widht.
	 */
	public int screenWidth(float worldWidth) {
		return Math.round((xscale) * worldWidth);
	}

	/**
	 * Rescales the grid to current world coordinate values.
	 */
	public void rescale() {
		float xlength = xmax - xmin;
		float ylength = ymax - ymin;
		rescale(xlength, ylength);
	}

	/**
	 * Rescales the coordinates to the new world ranges.
	 */
	public void rescale(float xAxisLength, float yAxisLength) {
		yLength = yAxisLength;
		xscale = (width - (2 * xinset)) / (xAxisLength);
		yscale = (height - (2 * yinset)) / (yAxisLength);
		xshift = -xmin;
		yshift = -ymin;
	}

	/**
	 * Rescales the y direction
	 */
	public float rescaleY(int ymin, int ymax) {
		this.ymin = ymin;
		this.ymax = ymax;
		yLength = ymax - ymin;
		yscale = (height - (2 * yinset)) / (yLength);
		yshift = -ymin;
		return yscale;
	}

	/**
	 * Rescales the x direction
	 */
	public void rescaleX(float xAxisLength) {
		xscale = (width - (2 * xinset)) / (xAxisLength);
		xshift = -xmin;
	}

	/**
	 * Sets the new xmin
	 */
	public void setxmin(float _xmin) {
		this.xmin = _xmin;
	}

	/**
	 * Sets the new ymin
	 */
	public void setymin(float _ymin) {
		this.ymin = _ymin;
	}

	/**
	 * Sets the new xmax
	 */
	public void setxmax(float _xmax) {
		this.xmax = _xmax;
	}

	/**
	 * Sets the new ymax
	 */
	public void setymax(float _ymax) {
		this.ymax = _ymax;
	}

	public void resizeWorld(float _xmin, float _ymin, float _xmax,
			float _ymax) {
		this.xmin = _xmin;
		this.ymin = _ymin;
		this.xmax = _xmax;
		this.ymax = _ymax;
	}

	/**
	 * Initialize the origin of each axis. This will copy the new values for
	 * xmin and ymin respectively.
	 */
	public void setOrigin(float _xmin, float _ymin) {
		this.xmin = _xmin;
		this.ymin = _ymin;
	}

	public void setInset(int _xinset, int _yinset) {
		this.xinset = _xinset;
		this.yinset = _yinset;
	}

	/**
	 * Sets the insets
	 */
	public void setXInset(int _xinset) {
		this.xinset = _xinset;
	}

	public int getXInset() {
		return this.xinset;
	}

	public void setYInset(int _inset) {
		this.yinset = _inset;
	}

	public int getYInset() {
		return this.yinset;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public float getxmax() {
		return this.xmax;
	}

	public float getxmin() {
		return this.xmin;
	}

	public float getymin() {
		return this.ymin;
	}

	public float getymax() {
		return this.ymax;
	}

	public void setHeight(int _height) {
		this.height = _height;
	}

	public void setWidth(int _width) {
		this.width = _width;
	}

	public int getXi() {
		return xi;
	}

	public int getYi() {
		return yi;
	}

	public void setXi(int _xi) {
		this.xi = _xi;
	}

	public void setYi(int _yi) {
		this.yi = _yi;
	}

}
