package com.arraybase.tm.builder.jobs;

import com.arraybase.modules.InstallationProblemException;
import com.arraybase.util.GBLogger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.File;
import java.util.Properties;

/**
 * Created by jmilton on 5/13/2015.
 */
public class BuildJobsSchema {
    static GBLogger log = GBLogger.getLogger(BuildJobsSchema.class);
    public static void main(String[] _args){
        try {
            installJobTables(_args);
        } catch (InstallationProblemException e) {
            e.printStackTrace();
        }
    }



    public static void installJobTables(String[] _args)
            throws InstallationProblemException {
        File install_fi = new File("scripts/install");
        File hb = new File(install_fi, "hb.cfg.xml");
        if (!hb.exists())
            throw new InstallationProblemException(
                    "Failed to find the hibernate install file "
                            + hb.getAbsolutePath());
        log.install("Installing the Template module. ");
        AnnotationConfiguration config = new AnnotationConfiguration();
        Class t_class = Job.class;
        config.addAnnotatedClass(t_class);
        config.configure(hb);

        Properties p = config.getProperties();
        String connection_url = p.getProperty("connection.url");
        log.install("New database is : " + connection_url);
        String user = p.getProperty("connection.username");
        log.install("connection.username : " + user);
        if (connection_url == null) {

        }
        new SchemaExport(config).create(true, true);
    }
}
