package app;

import java.util.LinkedList;
import java.util.NoSuchElementException;


public class Graph {

    private LinkedList<Vertex> vertices; // Alle Knoten/Vertices des Graphen
    private LinkedList<Edge> edges;         // Alle Kanten/Edges des Graphen

    private LinkedList<Vertex> highlightedVerticies = new LinkedList<>();
    private LinkedList<Edge> highlightedEdges = new LinkedList<>();

    private int lastEdgeId = 0;

    /**
     * Erzeugt einen neuen, leeren Graphen
     */
    public Graph() {
        vertices = new LinkedList<>();
        edges = new LinkedList<>();
    }

    // Knoten/Vertices //

    /**
     * F�gt dem Graphen die �bergebene Vertex-Instanz hinzu.
     */
    public void addVertex(Vertex v) {
        vertices.add(v);
    }

    /**
     * Entfernt einen Vertex/Knoten @param v aus dem Graphen
     * - (entfernt auch alle betroffenen Kanten)
     */
    public void removeVertex(Vertex v) {
        for (Edge e : v.getEdges()) { // Alle Kantenbeziehungen rausl�schen (aus allen benachbarten Kanten)
            if (e.getFrom().equals(v)) e.getTo().removeEdge(e);
            else e.getFrom().removeEdge(e);
        }
        vertices.remove(v); // Knoten selbst l�schen
    }

    /**
     * Liefert den Vertex mit der gegebenen @param id
     */
    public Vertex getVertex(int id)
    {
        for (Vertex v : vertices) {
            if (v.getId() == id) return v;
        }

        throw new NoSuchElementException("Could not find Vertex with ID: " + id + "");
    }

    /**
     * Liefert ALLE Vertices/Knoten des Graphen
     *
     * @return
     */
    public LinkedList<Vertex> getVertices() {
        return vertices;
    }


    // Kanten/Edges //

    /**
     * F�gt dem Graphen die �bergebene Edge-Instanz hinzu
     */
    public void addEdge(Edge e) {
        edges.add(e);
        e.getFrom().addEdge(e);
        e.getTo().addEdge(e);
    }

    /**
     * F�gt dem Graph eine Edge/Kante, die am Vertex @param from startet und am Vertex @param to endet.
     * Das hinzuf�gen doppelter Kanten wird unterbunden.
     */
    public void addEdge(Vertex from, Vertex to) {
        // skip edge if already there
        for (Edge e : from.getEdges()) {
            if (e.getTo().equals(to)) return;
        }

        Edge e = new Edge(lastEdgeId++, from, to); // neue Kante erstellen
        from.addEdge(e);                 // die Knoten �ber
        to.addEdge(e);                     // neue Kante "informieren"
        edges.add(e);                     // Kante dem Graphen hinzuf�gen
    }

    /**
     * F�gt dem Graph eine Edge/Kante, die am Vertex @param from startet und am Vertex @param to endet.
     */
    public void addEdge(int id, Vertex from, Vertex to) {
        Edge e = new Edge(id, from, to); // neue Kante erstellen
        from.addEdge(e);                 // die Knoten �ber
        to.addEdge(e);                     // neue Kante "informieren"
        edges.add(e);                     // Kante dem Graphen hinzuf�gen
    }

    /**
     * F�gt dem Graph eine GEWICHTETE Edge/Kante, die am Vertex @param from startet und am Vertex @param to endet.
     */
    public void addEdge(int id, Vertex from, Vertex to, int weight) {
        Edge e = new Edge(id, from, to, weight); // neue Kante erstellen

        from.addEdge(e);             // die Knoten �ber
        to.addEdge(e);                 // neue Kante "informieren"
        edges.add(e);                 // Kante dem Graphen hinzuf�gen
    }

    /**
     * Entfernt die Kante/Edge @param e
     */
    public void removeEdge(Edge e) {
        e.getFrom().removeEdge(e); // die Knoten �ber l�schung
        e.getTo().removeEdge(e);   // der Kante "informieren"
        edges.remove(e);           // Kante aus dem Graphen entfernen
    }

    /**
     * Entfernt (falls vorhanden) die Kante, die am Vertex @param from startet und am Vertex @param to endet.
     */
    public void removeEdge(Vertex from, Vertex to) {
        for (Edge e : from.getEdges()) {      // Alle Kanten des Knoten/Vertex from durchgehen
            if (to.getEdges().contains(e)) {  // falls eine Kante am Vertex to liegt
                from.removeEdge(e);              // disen aus beiden l�schen
                to.removeEdge(e);
                edges.remove(e);
            }
        }
    }

    /**
     * Liefert alle Edges/Kanten des Graphen
     *
     * @return
     */
    public LinkedList<Edge> getEdges() {
        return edges;
    }

    /**
     * Liefert das Kantengewicht der Kante @param e
     */
    public int getEdgeWeight(Edge e) {
        return e.getWeight();
    }

    /**
     * Liefert das Kantengewicht der Kante von Veretex @param a zu Vertex @param b (falls vorhanden)
     */
    public int getEdgeWeight(Vertex a, Vertex b) {
        for (Edge e : a.getEdges()) {        // Alle Kanten des Knoten/Vertex a durchgehen
            if (b.getEdges().contains(e)) { // falls eine Kante am Vertex b anliegt
                return e.getWeight();        // das entsprechende gewicht zur�ckgeben
            }
        }
        return -1;
    }

    /**
     * Setzt das Kantengewicht der Kante @param e
     */
    public void setEdgeWeight(Edge e, int weight) {
        e.setWeight(weight);
    }

    /**
     * Setzt das Kantengewicht @param weight der Kante von Vertex @param a zu Vertex @param b (falls vorhanden)
     *
     * @return true, wenn eine Kante gefunden und das Gewicht gesetzt wurde
     */
    public boolean setEdgeWeight(Vertex a, Vertex b, int weight) {
        for (Edge e : a.getEdges()) {         // Alle Kanten des Knoten/Vertex a durchgehen
            if (b.getEdges().contains(e)) {  // falls eine Kante am Vertex b liegt
                e.setWeight(weight);         // das kantengewicht setzen
                return true;
            }
        }
        return false;
    }

    // Anfragen //

    /**
     * @return true, wenn Vertex @param a und Vertex @param b benachbart sind
     */
    public boolean adjacent(Vertex a, Vertex b) {
        for (Edge e : a.getEdges()) {         // Alle Kanten des Knoten/Vertex a durchgehen
            if (b.getEdges().contains(e)) {  // falls eine Kante am Vertex b liegt
                return true;
            }
        }
        return false;
    }

    /**
     * @return Liefert eine Liste der Vertices von denen eine Kante
     * zum Knoten/Vertex @param v (oder umgekehrt) existiert
     * --> Alle Nachbarknoten
     */
    public LinkedList<Vertex> neighbours(Vertex v) {
        LinkedList<Vertex> neighbours = new LinkedList<>();

        for (Edge e : v.getEdges()) {         // Alle Kanten des Knoten/Vertex v durchgehen
            if (e.getFrom().equals(v)) {
                // Wenn v der ausgehende Vertex ist,
                neighbours.add(e.getTo());
            } else {
                // f�ge den jeweils anderen Vertex als Nachbarn hinzu
                neighbours.add(e.getFrom());
            }

        }

        return neighbours;
    }

    /**
     * Print AdjacentList in console.
     * Output: V(Vertexnumber) : (Neighbour a, b, ...) \n
     */
    public void printAdjacentList() {
        for (Vertex v : vertices) { // alle Knoten durchgehen
            System.out.print("V(" + v.getId() + ") : (");
            String s = "";
            for (Vertex n : neighbours(v)) {
                s = s + n.getId() + ", ";
            }
            if (!s.isEmpty()) s = s.substring(0, s.length() - 2); // remove last ", "
            System.out.print(s + ")\n");
        }
    }

    public void printVertexList() {
        System.out.print("Vertices: ");
        for (Vertex v : vertices) {
            System.out.print(v.getId() + " ");
        }
        System.out.print("\n");
    }

    /**
     * Konsolenausgabe aller Kanten mit der entsprechenden Gewichtung
     * Output: "Edges: edge1(weight1), ..., edgen(weightn)"
     */
    public void printEdgeList() {
        System.out.print("Edges: ");
        for (Edge e : edges) {
            System.out.print(e.getId() + "(" + e.getWeight() + "), ");
        }
        System.out.print("\n");
    }


    // getter and setter

    /**
     * @param id of the Vertex
     * @return Get Vertex by given ID
     * @throws NoSuchElementException if the id is not in graph
     */
    public Vertex getVetex(int id) throws NoSuchElementException {
        for (Vertex v : vertices) {
            if (v.getId() == id) return v;
        }

        throw new NoSuchElementException();
    }

    /**
     * @param id of the Edge
     * @return Get Edge by given ID
     * @throws NoSuchElementException if the id is not in graph
     */
    public Edge getEdge(int id) throws NoSuchElementException {
        for (Edge e : edges) {
            if (e.getId() == id) return e;
        }

        throw new NoSuchElementException();
    }

    public Edge getEdge(Vertex a, Vertex b) throws NoSuchElementException {
        for (Edge e : a.getEdges())
            if (b.getEdges().contains(e)) return e;
        throw new NoSuchElementException();
    }

    public boolean edgeExists(Edge e) {
        for (Edge ed : edges) {
            if (ed.getFrom().equals(e.getFrom()) && ed.getTo().equals(e.getTo())) return true;
        }
        return false;
    }


    public LinkedList<Vertex> getHighlightedVerticies() {
        return highlightedVerticies;
    }

    public void setHighlightedVerticies(LinkedList<Vertex> highlightedVerticies) {
        this.highlightedVerticies = highlightedVerticies;
    }

    public LinkedList<Edge> getHighlightedEdges() {
        return highlightedEdges;
    }

    public void setHighlightedEdges(LinkedList<Edge> highlightedEdges) {
        this.highlightedEdges = highlightedEdges;
    }


}
