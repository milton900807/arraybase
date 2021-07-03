package com.arraybase.aws;


import java.io.*;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.arraybase.ABTable;
import com.arraybase.shell.iterminal.c.interal.InputStreamReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by jmilton on 12/11/2016.
 */
public class S3 {

    public static ABTable load(String bucketName, String key) {
        try {

            ABTable table = new ABTable ( bucketName+"/"+key );




            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
            S3Object object = s3Client.getObject(
                    new GetObjectRequest(bucketName, key));
            InputStream objectData = object.getObjectContent();
            BufferedReader bf = new BufferedReader(new InputStreamReader(objectData));
            String line = bf.readLine();
            while (line != null) {
                System.out.println(" line " + line);
                line = bf.readLine();
            }

            // Process the objectData stream.
            objectData.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }

        return null;


    }

    public static void main(String[] args) {
//        https://s3.amazonaws.com/vfvf-test/ions_test

        load ( "vfvf-test", "ions_test" );
//        String test = "https://s3.amazonaws.com/vfvf-test/ions_test";
//        String bucketName = deriveBucketName(test);
//        String key = deriveKey(bucketName, test);
//        System.out.println(" key " + key);
//        System.out.println("  ----- ");

    }


    public static java.io.File downloadAWS(String uri) {

        PrintStream pr = null;
        java.io.File temp = null;
        try{
            temp  = java.io.File.createTempFile("_aws_file_", "");
            pr = new PrintStream(temp);
            String bucketName = deriveBucketName(uri);
            String key = deriveKey(bucketName, uri);
            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
            S3Object object = s3Client.getObject(
                    new GetObjectRequest(bucketName, key));
            InputStream objectData = object.getObjectContent();
            BufferedReader bf = new BufferedReader(new InputStreamReader(objectData));
            String line = bf.readLine();
            while (line != null) {
                System.out.println(" line " + line);
                line = bf.readLine();
                pr.println(line);
            }

            // Process the objectData stream.
            objectData.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            if (pr != null)
                pr.close();
        }
        return temp;


    }

    public static String deriveKey(String bucket_name, String uri) {
        int awsindex_com = uri.indexOf("s3.amazonaws.com/");
        awsindex_com += 17;
        uri = uri.substring(awsindex_com);


        int len = bucket_name.length();
        int index_com = uri.indexOf(bucket_name);
        index_com += len + 1;

        String sub = uri.substring(index_com);
        return sub;
    }
//    String test = "https://s3.amazonaws.com/vfvf-test/ions_test";

    public static String deriveBucketName(String uri) {
        int index_com = uri.indexOf("s3.amazonaws.com/");
        index_com += 17;
        int index_start_key = uri.indexOf("/", index_com);
        String sub = uri.substring(index_com, index_start_key);
        return sub;
    }
}

