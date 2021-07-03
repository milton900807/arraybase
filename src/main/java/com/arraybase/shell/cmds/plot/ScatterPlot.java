package com.arraybase.shell.cmds.plot;

import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.arraybase.GBPlugin;
import com.arraybase.GBSearchIterator;
import com.arraybase.GBV;
import com.arraybase.lac.LAC;
import com.arraybase.modules.UsageException;
import com.arraybase.qmath.flexigraph.IGraph;
import com.arraybase.shell.cmds.search2;

public class ScatterPlot implements GBPlugin {
    final IGraph graph = new IGraph();

    private int width = 1200;
    private int height = 1200;


    // target.plot(searchstring)[x][y]
    public String exec(String command, String variable_key)
            throws UsageException {
        String search_ = command;
        int ti = search_.indexOf('.');
        int t2 = search_.indexOf('(');
        int t3 = search_.lastIndexOf(')');
        String field_start = search_.substring(t3 + 1);
        search2 s = new search2();
        ArrayList<String> cols = search2.getColumns(field_start);
        if (cols == null || cols.size() <= 1) {
            throw new UsageException(
                    "Please provide two columns... x and y that you want to plot.");
        }
        final String x_field = cols.get(0);
        final String y_field = cols.get(1);
        String[] lac = LAC.parse(command);
        final FieldSort xminf = new FieldSort(lac[0], "search", lac[2], x_field, "asc");
        final FieldSort xmaxf = new FieldSort(lac[0], "search", lac[2], x_field, "desc");
//		System.out.println ("xmin : " +  xminf  + " xmax " + xmaxf );
        final FieldSort yminf = new FieldSort(lac[0], "search", lac[2], y_field, "asc");
        final FieldSort ymaxf = new FieldSort(lac[0], "search", lac[2], y_field, "desc");
//		System.out.println ("ymin : " +  yminf  + " ymax " + ymaxf );
        final float xmn = xminf.calculate();
        final float ymn = yminf.calculate();
        final float xma = xmaxf.calculate();
        final float yma = ymaxf.calculate();
        String gg = command.replace(".plot", ".search");
        final GBV<Iterator> search = s.execGBVIn(gg, null);
        Runnable r = new Runnable() {
            public void run() {
                float _xmin = (xmn + (0.1f * xmn));
                float _xmax = (xma + (0.1f * xma));
                float _ymin = (ymn + (0.1f * ymn));
                float _ymax = (yma + (0.1f * yma));
                graph.setxmin(_xmin);
                graph.setxmax(_xmax);
                graph.setymin(_ymin);
                graph.setymax(_ymax);

                int increment = 0;
                GBSearchIterator it = (GBSearchIterator) search.get();
                while (it.hasNext()) {
                    ArrayList<LinkedHashMap<String, Object>> bi = it.next();
                    for (LinkedHashMap<String, Object> l : bi) {
                        Object xt = l.get(x_field);
                        Object yt = l.get(y_field);
                        if (xt != null && xt instanceof Float && yt != null
                                && yt instanceof Float) {
                            Float xd = (Float) xt;
                            Float yd = (Float) yt;
                            graph.plot(xd.floatValue(), yd.floatValue());
                        } else if (xt != null && (!(xt instanceof Number)) && yt != null && yt instanceof Number) {
                            Float yd = (Float) yt;
                            graph.plot(increment, yd.floatValue());
                            increment++;
                        }
                    }
                }

                BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D ig2 = bi.createGraphics();
                graph.paint(ig2, width, height);

                try {
                    ImageIO.write(bi, "PNG", new File("yourImageName.PNG"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        Thread t = new Thread(r);
        t.start();

        return "ploting it";
    }

    public GBV execGBVIn(String cmd, GBV input) throws UsageException {
        return null;
    }
}