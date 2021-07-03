package com.arraybase.aws;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.arraybase.ABTable;
import com.arraybase.GB;
import com.arraybase.shell.iterminal.c.interal.*;
import org.apache.poi.util.TempFile;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;

/**
 * Created by jmilton on 12/12/2016.
 */
public class S3Importer {


    public static void main(String[] args) {

        File f = loadJar("s3://isis-dev/abv2_ionisplugins.jar");
        System.out.println(" file " + f.getAbsolutePath());

        try {
            URL url = f.toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

//            com.ionisph.chem.notation.SugarChemistry lc;

//            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//            method.setAccessible(true);
//            method.invoke(classLoader, url);


//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public static File loadJar(String s3url) {
        // s3://isis-dev/
        String temp = s3url.substring(5).trim();
        int bi = temp.indexOf('/');

        String bucketName = temp.substring(0, bi);
        String key = temp.substring(bi + 1);

        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        S3Object object = s3Client.getObject(
                new GetObjectRequest(bucketName, key));
        InputStream objectData = object.getObjectContent();
        try {
            return downloadFile(objectData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File downloadFile(InputStream objectData) throws Exception {
        DataOutputStream dos = null;
        OutputStream out = null;
        try {
            File newfile = TempFile.createTempFile("_temp_jar_file", ".jar");
            out = new FileOutputStream(newfile);
            byte[] fileAsBytes = new byte[objectData.available()];
            objectData.read(fileAsBytes);
            dos = new DataOutputStream(out);
            dos.write(fileAsBytes);
            dos.close();
            return newfile;
        } catch (IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void load(String uri, String gb_file) {
        try {
            String bucketName = S3.deriveBucketName(uri);
            String key = S3.deriveKey(bucketName, uri);
            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
            S3Object object = s3Client.getObject(
                    new GetObjectRequest(bucketName, key));
            InputStream objectData = object.getObjectContent();
            BufferedReader bf = new BufferedReader(new com.arraybase.shell.iterminal.c.interal.InputStreamReader(objectData));
            String line = bf.readLine();

            ABTable table = new ABTable(GB.pwd() + "/" + gb_file);
            while (line != null) {
                System.out.println(" line " + line);
                line = bf.readLine();
                String[] lt = line.split("\t");
                LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
                data.put("ion", lt[0]);
                data.put("helm", lt[1]);
                table.append(data);
            }
            table.commit();
            // Process the objectData stream.
            objectData.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
        }

    }
}
