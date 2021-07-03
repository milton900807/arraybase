package com.arraybase.qmath.flexigraph;


public class FloatGrid implements java.io.Serializable {

	protected float xi = 0f;
	
	protected float yi = 0f;

	protected float width;
	
	protected float height;

	protected float xinset = 25f;

	protected float yinset = 25f;
	
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

	public FloatGrid() {
	}

	/**
	 * Constructs a Grid with screen width and height.
	 */
	public FloatGrid(float _width, float _height) {
		this.width = _width;
		this.height = _height;
	}
	
	
	public FloatGrid(float _xmin, float _ymin, float _xmax, float _ymax,
			int _width, int _height) {
		this(_width, _height);
		this.xmin = _xmin;
		this.ymin = _ymin;
		this.xmax = _xmax;
		this.ymax = _ymax;
	}

	public FloatGrid(float _xmin, float _ymin, float _xmax, float _ymax) {
		this(_xmin, _ymin, _xmax, _ymax, 0, 0);
	}

	public FloatGrid(FloatGrid _grid) {
		resizeWorld(_grid.getxmin(), _grid.getymin(), _grid.getxmax(), _grid
				.getymax());
		setBounds(_grid.xi, _grid.yi, _grid.getWidth(), _grid.getHeight());
	}

	/**
	 * Resizes the screen coordinates to the new dimensions
	 */
	public void setDimension(float _width, float _height) {
		this.width = _width;
		this.height = _height;
	}


	/**
	 * Reshapes all the screen coordinates
	 */
	public void setBounds(float x, float y, float w, float h) {
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
	public float Xwc(float xsc) {
		float xwc;
		xwc = ((xsc - xinset + xi) / xscale) - xshift;
		return xwc;
	}

	/**
	 * Converts the 'y' screen coordinate to the corresponding 'y' world
	 * coordinate
	 */
	public float Ywc(float ysc) {
		float ywc;
		ywc = (yLength - ((ysc - yinset + yi) / yscale)) - yshift;// needs
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
	public float rescaleY(float ymin, float ymax) {
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

	public float getXInset() {
		return this.xinset;
	}

	public void setYInset(float _inset) {
		this.yinset = _inset;
	}

	public float getYInset() {
		return this.yinset;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
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

	public float getXi() {
		return xi;
	}

	public float getYi() {
		return yi;
	}

	public void setXi(int _xi) {
		this.xi = _xi;
	}

	public void setYi(int _yi) {
		this.yi = _yi;
	}

}
