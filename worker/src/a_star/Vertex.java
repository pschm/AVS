package a_star;

import java.util.LinkedList;


/**
 * Stellt einen Knoten/Vertex innerhalb eines Graphen dar
 */
public class Vertex {

    private int id;
    private Position pos;
    private LinkedList<Edge> edges = new LinkedList<>();

    // for A*
    private Vertex pre = null;
    private Double distance = Double.POSITIVE_INFINITY;
    private Double heuristic = Double.POSITIVE_INFINITY;
    private Double order = Double.POSITIVE_INFINITY;


    /**
     * Erstellt einen neuen Knoten/Vertex im ursprung
     */
    public Vertex(int id) {
        pos = new Position(0, 0);
        this.id = id;
    }

    /**
     * Erstellt einen neuen Knoten/Vertex an der Kooridnate @param c
     */
    public Vertex(int id, Position c) {
        this.id = id;
        this.pos = c;
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public void removeEdge(Edge e) {
        edges.remove(e);
    }


    // getters and setters //

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public Position getPosition() {
        return pos;
    }

    public double distanceTo(Vertex vertex) {
        double xDistance = Math.abs(getPosition().x - vertex.getPosition().x);
        double yDistance = Math.abs(getPosition().y - vertex.getPosition().y);
        double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

        return distance;
    }

    public void setC(Position position) {
        this.pos = position;
    }

    public int getId() {
        return id;
    }

    public Vertex getPre() {
        return pre;
    }

    public void setPre(Vertex pre) {
        this.pre = pre;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Double heuristic) {
        this.heuristic = heuristic;
    }

    public Double getOrder() {
        return order;
    }

    public void setOrder(Double order) {
        this.order = order;
    }

}
