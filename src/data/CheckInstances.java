/*
package data;

import Mule.Mule;
import automation.Management;
import automation.data.LaunchedClient;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.ui.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckInstances {

    private Mule main;
    private List<LaunchedClient> runningClients;
    private long bInstStartTime = 0;
    private ArrayList<String> badInstances;
    private final long BAD_INST_CHECK_MS = 600000;
    private static final String ERROR_FILE_PATH = "C:\\Users\\bllit\\OneDrive\\Desktop\\RSPeerErrors.txt";

    public CheckInstances(Mule main) {
        this.main = main;
        badInstances = new ArrayList<>();
    }

    private void setRunningClients(int retries) {
        try {
            runningClients = Management.getRunningClients(main.API_KEY);

            while (runningClients.size() <= 0 && retries > 0) {
                runningClients = Management.getRunningClients(main.API_KEY);
                Time.sleep(5000);
                retries--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkBadInstances(int retries) {
        Log.fine("Checking Bad Instances");
        setRunningClients(10);

        if (retries < 0) {
            writeToErrorFile("checkBadInstances() | Out of retries");
            bInstStartTime = -1;
            return;
        }
        bInstStartTime = -1;

        try {
            if (runningClients != null) {
                for (LaunchedClient c : runningClients) {

                    String cTag = c.getTag();

                    if (badInstances.contains(cTag) &&
                            (c.getRsn() == null || c.getRsn().contains("Not Logged In")) &&
                            (c.getScriptName() == null || c.getScriptName().contains("No Script"))) {

                        if (c.kill(main.API_KEY)) {
                            Log.fine("Bad Instance Killed");
                            badInstances.remove(cTag);
                        }

                    } else if (!badInstances.contains(cTag) &&
                            (c.getRsn() == null || c.getRsn().contains("Not Logged In")) &&
                            (c.getScriptName() == null || c.getScriptName().contains("No Script"))) {

                        badInstances.add(cTag);

                    } else {
                        if (badInstances.contains(cTag)) {
                            badInstances.remove(cTag);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            checkBadInstances(retries - 1);
        }

        bInstStartTime = System.currentTimeMillis();

        if (badInstances.size() > 0)
            Log.severe(badInstances.size());
    }

    public boolean isBadInstanceTime() {
        return bInstStartTime != -1 && (System.currentTimeMillis() - bInstStartTime) > BAD_INST_CHECK_MS;
    }

    private void writeToErrorFile(String errMsg) {
        try (FileWriter fw = new FileWriter(new File(ERROR_FILE_PATH), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(errMsg);
        } catch (IOException e) {
            Log.severe("Error file not found");
        }
    }
}
*/
