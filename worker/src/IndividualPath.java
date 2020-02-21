import app.Edge;
import app.Graph;
import app.Vertex;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.*;

public class IndividualPath {
	 // Holds our IndividualPath of products
    @SerializedName("IndividualPath")
    @Expose
    private ArrayList<Product> IndividualPath = new ArrayList<Product>();
    // Cache
    @SerializedName("fitness")
    @Expose
    private double fitness = 0;
    @SerializedName("distance")
    @Expose
    private double distance = 0;
    
    /**
     * Constructs a blank IndividualPath
     */
    public IndividualPath(){
        for (int i = 0; i < PathManager.numberOfProducts(); i++) {
            IndividualPath.add(null);
        }
    }

    public IndividualPath(int numberOfProducts)
    {
        for (int i = 0; i < numberOfProducts; i++) {
            IndividualPath.add(null);
        }
    }
    
    /**
     * Constructs a IndividualPath
     * @param IndividualPath
     */
    public IndividualPath(ArrayList<Product> IndividualPath){
        this.IndividualPath = IndividualPath;
    }

    /**
     * Creates a random individual
     */
    public void generateIndividual() {
        // Loop through all our destination products and add them to our IndividualPath
        for (int productIndex = 1; productIndex < PathManager.numberOfProducts()-1; productIndex++) {
          setProduct(productIndex, PathManager.getProduct(productIndex));
        }
        // Randomly reorder the IndividualPath
        Collections.shuffle(IndividualPath);
    }

    /**
     * Gets a product from the IndividualPath
     * @param tourPosition
     * @return Product
     */
    public Product getProduct(int tourPosition) {
        return (Product)IndividualPath.get(tourPosition);
    }

    /**
     * Sets a product in a certain position within a IndividualPath
     * @param IndividualPathPosition
     * @param product
     */
    public void setProduct(int IndividualPathPosition, Product product) {
        IndividualPath.set(IndividualPathPosition, product);
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    public void addProduct(int index, Product product)
    {
        IndividualPath.add(index, product);
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    public void removeProduct(Product product)
    {
        IndividualPath.remove(product);
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }
    
    /**
     * Gets the IndividualPaths fitness
     * @return Fitness (Double)
     */
    public double getFitness(Graph graph) {
        if (fitness == 0 || fitness == 0.0) {
            fitness = 1/(double)getDistance(graph);
        }
        return fitness;
    }

    public ArrayList<Product> getIndividualPath() {
        return IndividualPath;
    }

    private static double astar(Vertex from, Vertex to, Graph graph) throws NoSuchElementException {
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
            if (u == null) return 0.0;
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

                closedList.add(to);

                double aStarDistance = 0.0;
                if(closedList.size() >= 2)
                {
                    for (int i = 0; i < closedList.size() - 1; i++)
                    {
                        Vertex a = closedList.get(i);
                        Vertex b = closedList.get(i + 1);

                        aStarDistance += a.distanceTo(b);
                    }
                }

                return aStarDistance;
            }

            closedList.add(u);
            expandNode(openList, closedList, u, graph);
        }
        System.out.println("Es konnte kein Pfad gefunden werden...");
        return 0.0;
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
    
    /**
     * Gets the total distance of the IndividualPath
     * @return Distance (INT)
     */
    public double getDistance(Graph graph){
        if (distance == 0 || distance == 0.0) {
            int IndividualPathDistance = 0;
            // Loop through our IndividualPath's products
            for (int productIndex=0; productIndex < pathSize(); productIndex++) {
                // Get product we're travelling from
            	Product fromProduct = getProduct(productIndex);
                // Product we're travelling to
            	Product destinationProduct;
                // Check we're not on our IndividualPath's last product, if we are set our
                // IndividualPath's final destination product to our starting product
                if(productIndex+1 < pathSize()){
                    destinationProduct = getProduct(productIndex+1);
                }
                else{
                    destinationProduct = getProduct(0);
                }
                // Get the distance between the two products
                IndividualPathDistance += astar(graph.getVertex(fromProduct.id),graph.getVertex(destinationProduct.id),graph);
            }
            distance = IndividualPathDistance;
        }
        return distance;
    }

    /**
     * Get number of products on our IndividualPath
     * @return
     */
    public int pathSize() {
        return IndividualPath.size();
    }
    
    /**
     * Check if the IndividualPath contains a product
     * @param product
     * @return Boolean
     */
    public boolean containsProduct(Product product){
        return IndividualPath.contains(product);
    }

    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < pathSize(); i++) {
            geneString += getProduct(i)+"|";
        }
        return geneString;
    }
}
