package com.arraybase.qmath.flexigraph;


//@PersistenceCapable
public class FloatPoint implements java.io.Serializable
{

	private static final String BLUE = "0000ff";
//	@Persistent
    public float x, y;
//	@Persistent
    public String label = "";
//	@Persistent
    protected boolean selected = false;

    public FloatPoint()
    {
        this.x = 0;
        this.y = 0;
    }

    public FloatPoint(float _x, float _y)
    {
        this.x = _x;
        this.y = _y;
    }
    public FloatPoint(float _x, float _y, String _label)
    {
        this(_x, _y);
        this.label = _label;
    }
    public void paint(Graphics g, Grid o)
    {
        //g.drawString(""+ this.x, o.X(this.x), o.Y(this.y));
        g.fillOval(o.X(this.x), o.Y(this.y), 1, 2);
    }
    public boolean isPoint(float _x, float _y, Grid _grid)
    {
        return x == _x && this.y == _y;
    }
    public void update ( float _x, float _y )
    {
        this.x = _x;
        this.y = _y;
    }
    public float getX ()
    {
        return x;
    }
    public float getY ()
    {
        return y;
    }
    public void setX ( float _x )
    {
        x = _x;
    }
    public void setY ( float _y )
    {
        y = _y;
    }
    public void select()
    {
        this.selected = true;
    }
    public void deselect()
    {
        this.selected = false;
    }
    public boolean isSelected()
    {
        return selected;
    }
    public String toString ()
    {
        return ""+x+", "+y;
    }

}



