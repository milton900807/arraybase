package com.arraybase.qmath.flexigraph;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.GeneralPath;

public class Plot {

	private Grid tgrid = new Grid();
	private Paint mPaint = Color.blue;
	private Paint nPaint = Color.red;

	public Plot() {
		init();
	}

	public void setGrid(Grid _tgrid) {
		tgrid = _tgrid;
	}

	public Grid getGrid() {
		return tgrid;
	}

	private void init() {
		mPaint = Color.blue;
		nPaint = Color.red;
	}

	/**
	 * The termgraph knows how to draw itself.
	 * 
	 * @param _g
	 */
	public void draw(Canvas _c, Grid _grid) {
		_grid.rescale();
		Graphics2D gr = (Graphics2D) _c.getGraphics();
		tgrid.rescale();
		// int height = _grid.screenHeight(tgrid.getHeight());
		int top = _grid.Y(tgrid.getYi() + tgrid.getHeight());

		int right_pt = _grid.X(tgrid.getXi() + tgrid.getWidth());
		GeneralPath _path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

		int yincrement = 1;
		int xincrement = 1;
		for (float y = tgrid.getymin(); y < tgrid.getymax(); y += yincrement) {
			for (float x = tgrid.getxmin(); x < tgrid.getxmax(); x += xincrement) {
				_path.moveTo(_grid.X(tgrid.X(x)),
						_grid.Y(tgrid.Y(tgrid.getymin())));
				_path.lineTo(_grid.X(tgrid.X(x)),
						_grid.Y(tgrid.Y(tgrid.getymax())));
				gr.draw(_path);
				// _c.drawText("" + x, tgrid.X(x), 5 + tgrid.Y(tgrid.getymin()),
				// nPaint);
			}
		}

		// Rect r = new Rect(_grid.X(tgrid.getXi()), top, right_pt,
		// _grid.Y(tgrid
		// .getYi()));
		gr.fillRect(_grid.X(tgrid.getXi()), top, right_pt,
				_grid.Y(tgrid.getYi()));
		gr.fillOval(_grid.X(tgrid.getXi()), _grid.Y(tgrid.getYi()), 5, 5);
	}

	public void setColor(int color) {
		mPaint = new Color(color);
	}

}