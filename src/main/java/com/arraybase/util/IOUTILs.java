package com.arraybase.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class IOUTILs {
    public static String getDirectory(String instanceDir) {
        String instance_directory = trimLead(instanceDir);
        return instance_directory;
    }
    public static String getDirectory(java.nio.file.Path instanceDir) {
        String instance_directory = trimLead(instanceDir.toString());
        return instance_directory;
    }


    private static String trimLead(String instance_directory) {
        System.out.println(" IOUTILS : " + instance_directory);
        return instance_directory;
    }

    public static File getFile(String _directory, String _file) {
        return new File(_directory, _file);
    }

    public static void copyFolder(File src, File dest) throws IOException {

        System.out.println ( " src : " + src.getAbsolutePath() );
        System.out.println ( "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ : " );
        System.out.println ( " dest : "+ dest.getAbsolutePath() );



        FileUtils.copyDirectory(src, dest);
//
//        InputStream in = null;
//        OutputStream out = null;
//        if (src.isDirectory()) {
//
//            // if directory not exists, create it
//            if (!dest.exists()) {
//                dest.mkdir();
//                System.out.println("Directory copied from " + src + "  to "
//                        + dest);
//            }
//
//            // list all the directory contents
//            String files[] = src.list();
//
//            for (String file : files) {
//                // construct the src and dest file structure
//                File srcFile = new File(src, file);
//                File destFile = new File(dest, file);
//                // recursive copy
//                copyFolder(srcFile, destFile);
//            }
//
//        } else {
//            // if file, then copy it
//            // Use bytes stream to support all file types
//            in = new FileInputStream(src);
//            out = new FileOutputStream(dest);
//
//            try {
//                byte[] buffer = new byte[1024];
//
//                int length;
//                // copy the file content in bytes
//                while ((length = in.read(buffer)) > 0) {
//                    out.write(buffer, 0, length);
//                }
//            } finally {
//                IOUTILs.closeResource(in);
//                IOUTILs.closeResource(out);
//            }
//            System.out.println("File copied from " + src + " to " + dest);
//        }
    }

    public static void closeResource(Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mv(File mv_from, File move_to) {

        Path from_path = mv_from.toPath();
        Path to_path = move_to.toPath();
        try {
            Files.copy(from_path, to_path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
