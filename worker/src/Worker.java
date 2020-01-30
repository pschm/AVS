import app.App;
import app.Edge;
import app.Graph;
import app.Position;
import app.Vertex;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Worker {

    private static LinkedList<Vertex> astar(Vertex from, Vertex to, Graph graph) throws NoSuchElementException {
        LinkedList<Vertex> vertices            = graph.getVertices();
        LinkedList<Vertex> highlightedVertices = graph.getHighlightedVerticies();
        LinkedList<Edge>   highlightedEdges    = graph.getHighlightedEdges();

        // MAKEHEAP create new heap (binary heap)
        PriorityQueue<Vertex> openList = new PriorityQueue<>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                // a negative integer, zero, or a positive integer
                // as the first argument is less than, equal to, or greater than the second.
                if (o1.getOrder() < o2.getOrder()) return -1;
                if (o1.getOrder() > o2.getOrder()) return  1;
                return 0;
            }
        });


        // test if from and to are in the graph!
        if (!vertices.contains(from) && !vertices.contains(to)) {
            // one vertex is not in the graph
            throw new NoSuchElementException();
        } else {
            from = graph.getVertex(from.getId());
            to   = graph.getVertex(to.getId());
        }

        LinkedList<Vertex> closedList = new LinkedList<>();

        // init all vertices
        for (Vertex v : vertices) {
            v.setDistance(Double.POSITIVE_INFINITY);

            // calc heuristic
            double a = Math.abs(v.getPosition().x - to.getPosition().x);
            double b = Math.abs(v.getPosition().y - to.getPosition().y);
            double heuristic = Math.sqrt((a*a) + (b*b));
            v.setHeuristic(heuristic);

            // set heuristic + distance // always infinity
            v.setOrder(v.getDistance() + v.getHeuristic());

            v.setPre(null);
        }

        // init from Vertex
        from.setDistance(0.0);
        //to.setDistance(0.0);
        openList.add(from);            // HEAP INSERT
        highlightedVertices.add(from); // highlight start

        while (!openList.isEmpty()) {
            Vertex u = openList.poll(); // HEAP-EXTRACT-MIN
            if (u == null) return null;
            if (u.equals(to)) {
                // Pfad gefunden
                // highlighted edges
                for (Vertex v : vertices) {
                    if (v.getPre() == null) continue;
                    // highlight vertex
                    highlightedVertices.add(v);

                    // highlight edges
                    for (Edge e : v.getEdges()) {
                        if (v.getPre().getEdges().contains(e)) {
                            highlightedEdges.add(e);
                            break;
                        }
                    }
                }

                printPath(to, graph);
                return closedList;
            }

            closedList.add(u);
            expandNode(openList, closedList, u, graph);
        }
        System.out.println("Es konnte kein Pfad gefunden werden...");
        return null;
    }

    private static void printPath(Vertex end, Graph graph) {
        if (end.getPre() != null) {
            graph.getHighlightedVerticies().add(end);
            Edge e = graph.getEdge(end, end.getPre());
            graph.getHighlightedEdges().add(e);

            printPath(end.getPre(), graph);
        }
    }

    private static void expandNode(PriorityQueue<Vertex> openList, LinkedList<Vertex> closedList, Vertex u, Graph graph) {

        // for all neighbours of u
        for (Vertex v : graph.neighbours(u)) {

            // wenn der Nachbar bereits teil des Pfads ist,
            // mache mit dem n�chsten Nachbarn weiter
            if (closedList.contains(v)) continue;

            // distanz berechnen
            double g = u.getDistance() + graph.getEdgeWeight(u, v);

            // wenn der Nachbar in der openList ist, aber die Distanz zu gro� ist,
            // mach mit dem n�chsten Nachbarn weiter
            if (openList.contains(v) && g >= v.getDistance()) continue;

            v.setPre(u);
            v.setDistance(g);

            double order = v.getHeuristic() + g;

            if (openList.contains(v)) {
                // HEAP DECREASE KEY
                openList.remove(v);
                v.setOrder(order);
                openList.add(v);
            } else {
                // HEAP INSERT
                v.setOrder(order);
                openList.add(v);
            }

        } // End for (all neighbours)
    }

    private static void start(String ipAndPort) throws IOException, InterruptedException {
        SchedulerAPI schedulerAPI = new SchedulerAPI(ipAndPort);

        Population actualPopulation = null;
        String uuid = "";
        Graph graph = null;

        while(true)
        {
            Thread.sleep(1000);

            if (!uuid.isEmpty())
            {
                if (actualPopulation != null)
                {
                    System.out.println("Initial distance: " + actualPopulation.getFittest().getDistance());

                    actualPopulation = GA.evolvePopulation(actualPopulation);
                    for (int i = 0; i < 100; i++)
                    {
                        actualPopulation = GA.evolvePopulation(actualPopulation);
                    }

                    IndividualPath individualPath = actualPopulation.getFittest();
                    ArrayList<Product> products = individualPath.getIndividualPath();

                    LinkedList<Vertex> rightVertices = new LinkedList<>();
                    for (Product product : products) {
                        for (int c = 0; c < graph.getVertices().size(); c++) {
                            if (product.getX().intValue() == graph.getVertices().get(c).getPosition().x && product.getY().intValue() == graph.getVertices().get(c).getPosition().y) {
                                rightVertices.add(graph.getVertices().get(c));
                            }
                        }
                    }

                    LinkedList<Vertex> rightPath = new LinkedList<>();
                    for (int i = 0; i < rightVertices.size()-1; i++)
                    {
                        LinkedList<Vertex> aStarVertices = astar(rightVertices.get(i),rightVertices.get(i+1), graph);
                        assert aStarVertices != null;
                        aStarVertices.add(rightVertices.get(i+1));
                        rightPath.addAll(aStarVertices);
                    }

                    // Print final results
                    System.out.println("Finished");
                    System.out.println("Final distance: " + actualPopulation.getFittest().getDistance());
                    System.out.println("Solution:");
                    System.out.println(actualPopulation.getFittest());

                    JSONObject obj = schedulerAPI.putWorker(uuid, actualPopulation);

                    if (obj != null) {
                        if(obj.has("status"))
                        {
                            if(obj.getString("status").equals("bad"))
                            {
                                String popAndUUID = schedulerAPI.getWorker();

                                if (popAndUUID != null) {

                                    JSONObject popUUIDObj = new JSONObject(popAndUUID);

                                    JSONObject popObj = popUUIDObj.getJSONObject("population");

                                    String s = popObj.toString();

                                    Gson gson = new Gson();
                                    actualPopulation = gson.fromJson(popObj.toString(), Population.class);

                                    uuid = popUUIDObj.getString("uuid");

                                    String jsonALL = popUUIDObj.toString();

                                    graph = App.start(jsonALL);
                                }
                            }
                        }
                        else {
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

                    graph = App.start(jsonALL);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException
    {
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Enter Scheduler IP and Port (Example: http://localhost:8080) ");
        JTextField ipPort = new JTextField(20);

        newPanel.add(label);
        newPanel.add(ipPort);

        JButton jButton = new JButton("Start Worker");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            start(ipPort.getText());
                        } catch (IOException | InterruptedException ex) {
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
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

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