package app;

/**
 * Stellt eine Kante/Edge innerhalb eines Graphen dar
 */
public class Edge {
	
	private int id;
	private Vertex from;
	private Vertex to;
	
	private int weight;
	
	/**
	 * Erstellt eine Kante ohne Verbindung
	 */
	public Edge(int id) {
		this.id = id;
	}
	
	/**
	 * Erstellt eine Kante/Edge vom Vertex @param from zum Vertex @param to
	 */
	public Edge(int id, Vertex from, Vertex to) {
		this.id   = id;
		this.from = from;
		this.to   = to;
	}
	
	/**
	 * Erstellt eine gewichtete Kante/Edge vom Vertex @param from zum Vertex @param to
	 */
	public Edge(int id, Vertex from, Vertex to, int weight) {
		this.id     = id;
		this.from   = from;
		this.to     = to;
		this.weight = weight;
	}
	
	
	// gettes and setter //
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public Vertex getFrom() {
		return from;
	}

	public void setFrom(Vertex from) {
		this.from = from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setTo(Vertex to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
