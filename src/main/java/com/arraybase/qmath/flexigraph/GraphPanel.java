package com.arraybase.qmath.flexigraph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;

import javax.swing.JComponent;

public class GraphPanel extends JComponent {

	final IGraph  graph = new IGraph();
	
	

	public GraphPanel(float _xmin, float _xmax, float _ymin, float _ymax) {
		graph.setxmin(_xmin);
		graph.setxmax(_xmax);
		graph.setymin(_ymin);
		graph.setymax(_ymax);
	}
	
	protected void paintComponent(Graphics _g) {
		super.paintComponent(_g);
		Graphics2D g2d = (Graphics2D) _g;
		int width = getWidth ();
		int height = getHeight ();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		graph.paint(g2d, this);
	}

	public void plot(float _x, float _y, String _name) {
		graph.plot(_x, _y, _name);
	}

	public void setxmin(float xminf) {
		graph.setxmin(xminf);
	}

	public void plot(float _x, float _y) {
		graph.plot(_x, _y);

	}

}
