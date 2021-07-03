package com.arraybase.net;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;

/**
 * Created by jmilton on 5/2/2016.
 */
public class Download {


    public static java.io.File downloadToTemp(String url) {
        int ind = url.lastIndexOf('/');
        String filename = url.substring(ind+1);
        filename = filename.trim();
        java.io.FileOutputStream fos = null;
        try {
            File tempfile = new File ( filename );
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            fos = new java.io.FileOutputStream(tempfile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            return tempfile;
        } catch (java.io.IOException _e) {
            _e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * GunZip it
     */
    public static File gunzipIt(File file) {
        String outputfile = file.getName();
        if (outputfile.endsWith(".gz"))
            outputfile = outputfile.substring(0, outputfile.length() - 3);
        byte[] buffer = new byte[1024];
        try {
            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(file));
            FileOutputStream out =
                    new FileOutputStream(new File(outputfile));
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            gzis.close();
            out.close();
            System.out.println(file + " is  uncompressed.. ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new File ( outputfile );
    }


}
