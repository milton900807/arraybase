package com.arraybase.shell.cmds;

import com.arraybase.GB;
import com.arraybase.GBPlugin;
import com.arraybase.GBV;
import com.arraybase.schedule.GBScheduler;

import java.io.*;

public class SetCron implements GBPlugin {

    public String exec(String command, String variable_key) {
        // just pull the first space.
        int index = command.indexOf("cron ");
        String cron_value = command.substring(index + 5);
        try {
            setCron(cron_value.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Cron loaded";
    }

    // keep it simple
    private void setCron(String cron) throws IOException {
        String[] ts = cron.split(" ");
        String command = "";
        for (int i = 6; i < ts.length; i++) {
            command += ts[i] + " ";
        }
        String time = "";
        for (int i = 0; i < 6; i++) {
            time += ts[i] + " ";
        }
        command = command.trim();
        time = time.trim();
        System.out.println(" line " + cron);
        GB.addToScheduler(command, "arraybase", command, time);
        GB.print("Scheduler has started.");
    }

    public GBV execGBVIn(String cmd, GBV input) {
        return null;
    }
}
