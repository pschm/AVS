import a_star.Graph;
import a_star.GraphParser;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Worker {
    public static void start(String ipAndPort) throws IOException, InterruptedException, JSONException {
        SchedulerAPI schedulerAPI = new SchedulerAPI(ipAndPort);

        Population actualPopulation = null;
        String uuid = "";
        Graph graph = null;

        while (true) {
            if (!uuid.isEmpty()) {
                if (actualPopulation != null) {
                    double initialDistance = actualPopulation.getFittest(graph).getDistance(graph);

                    actualPopulation = GA.evolvePopulation(actualPopulation, graph);
                    for (int i = 0; i < 100; i++) {
                        actualPopulation = GA.evolvePopulation(actualPopulation, graph);
                    }

                    // Print final results
                    System.out.println("Finished");
                    actualPopulation.getFittest(graph).getDistance(graph);

                    for (int i = 0; i < actualPopulation.paths.length; i++) {
                        System.out.print(actualPopulation.paths[i].getDistance(graph) + " | ");
                    }
                    double finalDistance = actualPopulation.getBestDistance(graph);
                    System.out.println("\nBefore: " + initialDistance);
                    System.out.println("After : " + finalDistance);
                    double distanceDelta = initialDistance - finalDistance;
                    System.out.println("Delta : " + distanceDelta);
                    System.out.println("---");

                    JSONObject obj = schedulerAPI.putWorker(uuid, actualPopulation);

                    if (obj != null) {
                        if (obj.has("status")) {
                            if (obj.getString("status").equals("bad")) {
                                String popAndUUID = schedulerAPI.getWorker();

                                if (popAndUUID != null) {

                                    JSONObject popUUIDObj = new JSONObject(popAndUUID);

                                    JSONObject popObj = popUUIDObj.getJSONObject("population");

                                    String s = popObj.toString();

                                    Gson gson = new Gson();
                                    actualPopulation = gson.fromJson(popObj.toString(), Population.class);

                                    uuid = popUUIDObj.getString("uuid");

                                    String jsonALL = popUUIDObj.toString();

                                    graph = GraphParser.readNavMesh(jsonALL);
                                }
                            }
                        } else {
                            Gson gson = new Gson();
                            actualPopulation = gson.fromJson(obj.getJSONObject("population").toString(), Population.class);
                        }
                    } else {
                        System.out.println("PUT Worker: Response NULL");
                    }
                } else {
                    //...
                }
            } else {
                String popAndUUID = schedulerAPI.getWorker();

                if (popAndUUID != null) {

                    JSONObject popUUIDObj = new JSONObject(popAndUUID);

                    JSONObject popObj = popUUIDObj.getJSONObject("population");

                    String s = popObj.toString();

                    Gson gson = new Gson();
                    actualPopulation = gson.fromJson(popObj.toString(), Population.class);

                    uuid = popUUIDObj.getString("uuid");

                    String jsonALL = popUUIDObj.toString();

                    graph = GraphParser.readNavMesh(jsonALL);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, JSONException {
        // CONSOLE-ONLY
        if (args.length > 0 && args[0] != null && args[0].equals("no-gui")) {
            String address = "http://localhost:8080";

            if (args.length > 1 && args[1].contains("http")) address = args[1];

            start(address);
            return;
        }

        // GUI
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Enter Scheduler IP and Port (Example: http://localhost:8080) ");
        JTextField ipPort = new JTextField(20);
        ipPort.setText("http://localhost:8080");

        newPanel.add(label);
        newPanel.add(ipPort);

        JButton jButton = new JButton("Start Worker");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            start(ipPort.getText());
                        } catch (IOException | InterruptedException | JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        newPanel.add(jButton);

        JFrame frame = new JFrame();
        frame.add(newPanel);
        frame.setLocation(500, 500);
        frame.setSize(750, 150);
        frame.setTitle("AVS - Worker");

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                System.exit(0);
            }
        });

        frame.setVisible(true);
    }
}