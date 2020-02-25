package a_star;

import com.google.gson.Gson;
import json_import_avs.MeshNode;
import json_import_avs.UnityMapStructure;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Philipp Schmeier
 */
public class GraphParser {

    private static final Logger logger = Logger.getLogger(GraphParser.class.getCanonicalName());

    /**
     * Parse given mesh as Graph
     *
     * @return parsed Graph or null if the string could't be parsed
     */
    public static Graph readNavMesh(String navMeshAsJson) {
        Graph graph = new Graph();

        try {
            Gson gson = new Gson();
            UnityMapStructure res = gson.fromJson(navMeshAsJson, UnityMapStructure.class);

            if (res == null) {
                logger.log(Level.WARNING, "Could not read json!");
                return null;
            }

            List<MeshNode> navMesh = res.getNavMesh();

            // add all vertices
            for (MeshNode node : navMesh) {
                int id = node.getId();
                a_star.Position pos = node.getPos().convert();
                graph.addVertex(new Vertex(id, pos));
            }

            // generate edges
            for (MeshNode node : navMesh) {
                Vertex from = graph.getVertex(node.getId());
                List<Integer> nextNodes = node.getNextNodes();
                for (int nodeId : nextNodes) {
                    Vertex to = graph.getVertex(nodeId);

                    int a = Math.abs(from.getPosition().x - to.getPosition().x);
                    int b = Math.abs(from.getPosition().y - to.getPosition().y);
                    int weight = (int) Math.sqrt((a * a) + (b * b));

                    graph.addEdge(from, to);
                }
            }

            logger.log(Level.INFO, "Successfully parsed navMesh.");
            return graph;
        } catch (Exception e) {
            logger.warning("Exception while parsing navMesh:");
            e.printStackTrace();
        }

        return null;
    }
}
