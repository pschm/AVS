package app;

import com.google.gson.Gson;
import gui.Gui;
import json_import.EdgeImport;
import json_import.Result;
import json_import.VertexImport;
import json_import_avs.MeshNode;
import json_import_avs.UnityMapStructure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


/**
 * @author Philipp Schmeier
 */
public class App {

    public static Graph start(String navMeshAsJson) {
        System.out.println("Start - Graphprogramm");

        // init Graph
        Graph graph = new Graph();

		//readAlgoFile(graph, gson);
        readNavMesh(graph,navMeshAsJson);

        System.out.println("End - Graphprogramm");

        return graph;

        // init GUI
        //Gui gui = new Gui(graph);
        //gui.setVisible(true);
        //gui.revalidate();
    }

    private static void readNavMesh(Graph graph, String navMeshAsJson) {
        BufferedReader br = null;
        try
        {
            Gson gson = new Gson();
            UnityMapStructure res = gson.fromJson(navMeshAsJson, UnityMapStructure.class);

            if (res == null) {
                System.out.println("Could not read json!");
                return;
            }


            List<MeshNode> navMesh = res.getNavMesh();

            // add all vertices
            for (MeshNode node: navMesh) {
                int id = node.getId();
                System.out.println(node);
                app.Position pos = node.getPos().convert();

                graph.addVertex(new Vertex(id, pos));
            }

            // generate edges
            for (MeshNode node: navMesh) {
                Vertex from = graph.getVertex(node.getId());
                List<Integer> nextNodes = node.getNextNodes();
                for (int nodeId: nextNodes) {
                    Vertex to = graph.getVertex(nodeId);

                    int a = Math.abs(from.getPosition().x - to.getPosition().x);
                    int b = Math.abs(from.getPosition().y - to.getPosition().y);
                    int weight = (int) Math.sqrt((a * a) + (b * b));

                    graph.addEdge(from, to);
                }
            }

            graph.printAdjacentList();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void readAlgoFile(Graph graph, Gson gson) {
        BufferedReader br = null;
        try {
            // graph11 und 12 sind zu groß und können nicht mehr geladen werden
            // graph4_16 graph7_128 graph10_1024
            br = new BufferedReader(new FileReader("graph4_16.json"));

            Result res = gson.fromJson(br, Result.class);


            if (res != null) {
                for (VertexImport v : res.getVertices()) {
                    graph.addVertex(new Vertex(v.getId(), new Position(v.getPosition().getX(), v.getPosition().getY())));
                }

                for (EdgeImport e : res.getEdges()) {
                    Vertex from = graph.getVertices().get(e.getStart());
                    Vertex to = graph.getVertices().get(e.getEnd());
                    int a = Math.abs(from.getPosition().x - to.getPosition().x);
                    int b = Math.abs(from.getPosition().y - to.getPosition().y);
                    int weight = (int) Math.sqrt((a * a) + (b * b));

                    Edge edge = new Edge(e.getId(), from, to, weight);

                    if (!graph.edgeExists(edge))
                        graph.addEdge(edge);
                }

                // print A* schätzwerte
//				int targetID = 4;
//				Vertex target = graph.getVertex(targetID);
//				int x = target.getPosition().x;
//				int y = target.getPosition().y;
//
//				for (Vertex v : graph.getVertices()) {
//					if( v.getId() == targetID ) continue;
//
//					int a = Math.abs(x - v.getPosition().x);
//					int b = Math.abs(y - v.getPosition().y);
//					int length = (int) Math.sqrt((a*a) + (b*b));
//
//					System.out.println("ID: " + v.getId() + " : " + length);
//				}

                graph.printAdjacentList();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
