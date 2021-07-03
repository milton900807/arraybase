package com.arraybase.flare;

import com.arraybase.GB;
import com.arraybase.GBTableLoader;
import com.arraybase.db.HBConnect;
import com.arraybase.io.GBFileManager;
import com.arraybase.modules.UsageException;
import com.arraybase.tm.builder.jobs.Job;
import com.arraybase.util.IOUTILs;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by jmilton on 5/12/2015.
 */
public class BatchLoader implements Runnable {
    private File root = new File(".");
    public BatchLoader(File _root) throws BatchLoaderFailedException {
        root = _root;
        if (!root.exists())
            throw new BatchLoaderFailedException(" Location of root directory does not exist : " + _root);
    }


    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        BatchLoaderProgress status = new BatchLoaderProgress();
        try {
            if (root.exists()) {
                if (root.isDirectory()) {
                    load(root, status);
                } else {
                    loadFile(root, status);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UsageException e) {
            e.printStackTrace();
        }

    }

    private void loadFile(File f, BatchLoaderProgress status) {
        String name = f.getName();
        name = name.toLowerCase();
        if (name.endsWith(".abq")) {
            loadABQ(name, f, status);
        }
    }

    class BatchLoaderProgress {
        public void update(String s) {
            GB.print("\t\t" + s);
        }
    }

    private void load(File root, BatchLoaderProgress status) throws IOException, UsageException {
        File[] files = root.listFiles();
        for (File f : files) {
            GB.print ( " Loading " + f.getAbsolutePath() );
            if (jobsActive()) {
                while (jobsActive()) {
                    try {
                        Thread.sleep(5000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            String name = f.getName();
            name = name.toLowerCase();
            if (name.endsWith(".abq")) {
                loadABQ(name, f, status);
            } else if (name.endsWith(".abj")) {
                loadABJ(name, f, status);
            }else if (name.endsWith(".abc")) {
                loadABC(name, f, status);
            }

        }
    }

    private void loadABJ(String name, File f, BatchLoaderProgress status) {
        FileReader reader = null;
        try {
            reader = new FileReader(f);
            Properties p = new Properties();
            p.load(reader);

            String input_path = p.getProperty(ABJFile.INPUT_PATH);
            if (input_path == null || input_path.length() <= 0) {
                status.update("ABJ file : " + f.getAbsolutePath() + " failed to load since it doese not contain an input_path ");
                return;
            }
            String output_path = p.getProperty(ABJFile.OUTPUT_PATH);
            if (output_path == null || output_path.length() <= 0) {
                status.update("ABJ file : " + f.getAbsolutePath() + " failed to load since it doese not contain an output_path ");
            }
            GB.copy(input_path, output_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }
    }

    private void loadABC(String name, File f, BatchLoaderProgress status) {
        FileReader reader = null;
        try {
            reader = new FileReader(f);
            Properties p = new Properties();
            p.load(reader);

            String input_path = p.getProperty(ABJFile.INPUT_PATH);
            if (input_path == null || input_path.length() <= 0) {
                status.update("ABJ file : " + f.getAbsolutePath() + " failed to load since it doese not contain an input_path ");
                return;
            }
            String output_path = p.getProperty(ABJFile.OUTPUT_PATH);
            if (output_path == null || output_path.length() <= 0) {
                status.update("ABJ file : " + f.getAbsolutePath() + " failed to load since it doese not contain an output_path ");
            }
            GB.copy(input_path, output_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUTILs.closeResource(reader);
        }
    }

    private void loadABQ(String name, File f, BatchLoaderProgress status) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("--type", GBFileManager.ABQ);
        map.put("--user", "autoload");
        String job_id = name + new Date().toString();

        Session session = HBConnect.getSession();
        Job job = new Job();
        job.setStarted(new Date());
        job.setJob_id(job_id);
        try {
            session.beginTransaction();
            job.setStatus(Job.ACTIVE_STATUS);
            session.save(job);
            session.getTransaction().commit();

            try {
                GBTableLoader.loadABQ("autoload", f.getAbsolutePath(), null, job_id, null); //);, local_file, gb_file);
                status.update(name + " complete.");
            } catch (IOException e) {
                e.printStackTrace();
                status.update(name + " failed..." + e.getLocalizedMessage());
            } catch (UsageException e) {
                e.printStackTrace();
                status.update(name + " failed..." + e.getLocalizedMessage());
            }
        } finally {
            HBConnect.close(session);
        }

    }

    private boolean jobsActive() {
        Session session = HBConnect.getSession();
        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(Job.class).add(Restrictions.eq("status", Job.ACTIVE_STATUS));
            List<Job> l = c.list();
            return l.size() > 0;
        } catch (Exception _e) {
            _e.printStackTrace();
        } finally {
            HBConnect.close(session);
        }
        return false;
    }
}
