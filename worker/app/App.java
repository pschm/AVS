package app;

import com.google.gson.Gson;
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
}
