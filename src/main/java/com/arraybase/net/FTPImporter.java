package com.arraybase.net;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPInputStream;

/**
 * Created by jmilton on 5/2/2016.
 */
public class FTPImporter {


    public static void main(String[] args) {

//        load("ftp.ensembl.org", "/pub/release-84/fasta/homo_sapiens/dna");
       // gunzipIt ( new File ( "C:\\Users\\jmilton\\dev\\ab\\Homo_sapiens.GRCh38.dna.chromosome.1.fa.gz"));

    }


    public static void load(String host, String path) {

        // we have a directory
        try {
            InetAddress intAddr = InetAddress.getByName(host);
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(intAddr);
            ftpClient.login("anonymous", "");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

//            System.out.println (  " path : " + ftpClient.changeWorkingDirectory(path) );
            FTPFile[] files = ftpClient.listFiles(path);

            DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (FTPFile file : files) {
                String details = file.getName();
//                if (file.isDirectory()) {
//                    details = "[" + details + "]";
//                }
                details += "\t\t" + file.getSize();
                details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
                System.out.println(details);

                String file_name = file.getName().trim();

                if (file_name.endsWith(".gz")) {

                    File tempfile = new File(file.getName().trim());
                    OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(tempfile));


                    InputStream inputStream = ftpClient.retrieveFileStream(path + "/" + file_name);
                    byte[] bytesArray = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                        outputStream2.write(bytesArray, 0, bytesRead);
                    }
                    boolean success = ftpClient.completePendingCommand();
                    if (success) {
                        System.out.println(file_name + " has been downloaded successfully.");
                    }
                    outputStream2.close();
                    inputStream.close();


//                    gunzipIt ( tempfile );

                }
            }
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException _e) {
            _e.printStackTrace();
        }

    }

    /**
     * GunZip it
     */
    public static void gunzipIt(File file) {
        String outputfile = file.getName();
        if (outputfile.endsWith(".gz"))
            outputfile = outputfile.substring(0, outputfile.length() - 3);
        byte[] buffer = new byte[1024];
        try {
            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(file));
            FileOutputStream out =
                    new FileOutputStream(new File ( outputfile ));
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
    }

    public static void extract(File _f) {




    }


    public static void load(String local_file) {
        int pathin = local_file.indexOf(7, '/');
        if (pathin > 0) {
            String path = local_file.substring(pathin + 1);
            String host = local_file.substring(0, pathin);
            load(host, path);
        }
    }
}
