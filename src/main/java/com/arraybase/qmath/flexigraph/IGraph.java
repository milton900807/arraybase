package com.arraybase.qmath.flexigraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class IGraph {
	private Grid grid = new Grid();
	private ArrayList<RealPoint> points = new ArrayList<RealPoint>();
	private Color background = Color.black;
	private Color pointColor = Color.YELLOW;
	private Color lineColor = Color.darkGray;
	private double x_increment = 1d;
	private double y_increment = 1d;

	public IGraph() {
		grid.setInset(20, 20);
	}

	public void setxmax(float _xmax) {
		grid.setxmax(_xmax);
	}

	public void setymax(float _ymax) {
		grid.setymax(_ymax);
	}

	public void setymin(float _ymin) {
		grid.setymin(_ymin);
	}

	public void setxmin(float _xmin) {
		grid.setxmin(_xmin);
	}

	public double getX_increment() {
		return x_increment;
	}

	public void setX_increment(double xIncrement) {
		x_increment = xIncrement;
	}

	public double getY_increment() {
		return y_increment;
	}

	public void setY_increment(double yIncrement) {
		y_increment = yIncrement;
	}

	public void setSize(com.arraybase.qmath.flexigraph.Dimension _d) {
		grid.setSize(_d);
	}

	public synchronized void plot(float _x, float _y) {
		// grid.rescale();
		if (_x > grid.getxmax())
			grid.setxmax(_x);
		if (_x < grid.getxmin())
			grid.setxmin(_x);
		if (_y > grid.getymax())
			grid.setymax(_y);
		if (_y < grid.getymin())
			grid.setymin(_y);
		grid.rescale();

		points.add(new RealPoint(_x, _y));
	}

	public synchronized void plot(float _x, float _y, Color _color) {
		pointColor = _color;
		points.add(new RealPoint(_x, _y));
	}
	public void plot(float _x, float _y, String _name) {
		points.add(new RealPoint (_x, _y, _name));
	}


	public void reset() {
	}

	public void refresh() {
	}

	public void paint(Graphics2D g2d, JComponent component) {
		grid.setWidth(component.getWidth());
		grid.setHeight(component.getHeight());
		grid.rescale();
		drawBackdrop(g2d);
		for (RealPoint p : points) {
			g2d.setColor(pointColor);
			int xx = grid.X(p.x);
			int yy = grid.Y(p.y);
			g2d.fillOval(xx, yy, 2, 2);
		}
	}

	public void paint(Graphics2D g2d, int width, int height) {
		grid.setWidth(width);
		grid.setHeight(height);
		grid.rescale();
		drawBackdrop(g2d);
		for (RealPoint p : points) {
			g2d.setColor(pointColor);
			int xx = grid.X(p.x);
			int yy = grid.Y(p.y);
			g2d.fillOval(xx, yy, 2, 2);
		}
	}

	public void setPointColor(int r, int g, int b) {
		pointColor = new Color(r, g, b);
	}

	public void setBackground(int r, int g, int b) {
		background = new Color(r, g, b);
	}

	public void setLineColor(int r, int g, int b) {
		lineColor = new Color(r, g, b);
	}

	private void drawBackdrop(Graphics iog) {
		int offset = 0;
		iog.setColor(background);
		iog.fill3DRect(getXI() - offset, getYI() - offset, getWidth(),
				getHeight(), true);
		iog.setColor(lineColor);
		float xmax = getxmax();
		int x_max = X(xmax);
		float xmin = getxmin();
		int x_min = X(xmin);
		float ymax = getymax();
		int y_max = Y(ymax);
		float ymin = getymin();
		int y_min = Y(ymin);

		if (Math.abs((xmax - xmin) / x_increment) >= 30)
			x_increment = (int) ((xmax - xmin) / 30);
		if (Math.abs((ymax - ymin) / y_increment) >= 30) {
			y_increment = (int) ((ymax - ymin) / 30);
		}
		for (float i = xmin; i < xmax; i += x_increment) {
			iog.drawLine(X(i) - offset, y_min - offset, X(i) - offset, y_max
					- offset);
			iog.drawString("" + i, X(i), grid.getHeight()-grid.getYInset());
		}
		for (float j = ymin; j < ymax; j += y_increment) {
			iog.drawLine(x_min - offset, Y(j) - offset, x_max - offset, Y(j)
					- offset);
			iog.drawString("" + j, grid.getXi()+grid.getXInset(), Y(j));
		}
	}

	private int getYInset() {
		return grid.yinset;
	}

	private int getXInset() {
		return grid.xinset;
	}

	private int getHeight() {
		return grid.getHeight();
	}

	private int getWidth() {
		return grid.getWidth();
	}

	private int getYI() {
		return grid.getYi();
	}

	private int getXI() {
		return grid.xi;
	}

	private int Y(float _y) {
		return grid.Y(_y);
	}

	private int X(float _x) {
		return grid.X(_x);
	}

	private float getymin() {
		return grid.ymin;
	}

	private float getymax() {
		return grid.ymax;
	}

	private float getxmin() {
		return grid.xmin;
	}

	private float getxmax() {
		return grid.xmax;
	}

	public void rescale() {
		grid.rescale();
	}

	public void setSize(int width, int height) {
		rescale();
	}


}
