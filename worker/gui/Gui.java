package gui;

import app.Edge;
import app.Graph;
import app.Vertex;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;



@SuppressWarnings("serial")
public class Gui extends JFrame {

	private GraphPanel graphPanel;
	private Graph graph;

	/**
	 * Erstellt eine GUI und stellt den �bergebenen Graphen dar
	 * @param graph
	 */
	public Gui(Graph graph) {
		this.graph = graph;
		
		setSize(1200, 850);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		buildGUI();
	}

	/**
	 * Build Gui show the given graph.
	 */
	private void buildGUI() {
//		dijkstra(graph.getVertex(2));
//		astar(graph.getVertex(2), graph.getVertex(24));
		graphPanel = new GraphPanel(graph);
		add(graphPanel);
		
		
		// option Panel
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(2, 4));
        add(optionPanel, BorderLayout.SOUTH);

        JLabel startLabel = new JLabel("Start:");
        optionPanel.add(startLabel);

        JTextField start = new JTextField();
        start.setText("2");
        optionPanel.add(start);
        
        JLabel targetLabel = new JLabel("Ziel:");
        optionPanel.add(targetLabel);
        JTextField target = new JTextField();
        target.setText("4");
        optionPanel.add(target);


        JButton clearGraph = new JButton("Graph zur�cksetzen");
        optionPanel.add(clearGraph);

        JButton dijkstra = new JButton("Dijkstra");
        optionPanel.add(dijkstra);

        JButton astar = new JButton("A* (A Stern)");
        optionPanel.add(astar);

        // dummy panel for layout
        optionPanel.add(new JPanel());
        
        
        // action Listener
        clearGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graph.getHighlightedEdges().clear();
				graph.getHighlightedVerticies().clear();
				graphPanel.repaint();
			}
		});
        
        dijkstra.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dijkstra(graph.getVertex(Integer.parseInt(start.getText())));

				graphPanel.repaint();
			}
		});
        
        astar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				astar(graph.getVertex(Integer.parseInt(start.getText())), graph.getVertex(Integer.parseInt(target.getText())));
				graphPanel.repaint();
			}
		});
	}

	// Algorithms //
	/**
	 * Ermittelt den k�rzesten weg vom Start zum Zielknoten
	 * @param start Startknoten 
	 * @throws NoSuchElementException wenn ein Knoten nicht im graph enthalten ist
	 */
	public void dijkstra(Vertex start) throws NoSuchElementException {
		LinkedList<Vertex> vertices            = graph.getVertices();
		LinkedList<Vertex> highlightedVertices = graph.getHighlightedVerticies();
		LinkedList<Edge>   highlightedEdges    = graph.getHighlightedEdges();

		// check if vertices are in the graph
		if (!vertices.contains(start)) {
			// one vertex is not in the graph
			throw new NoSuchElementException();
		} else {
			start = graph.getVertex(start.getId());
		}

		// init distance and pre
		for (Vertex v : vertices) {
			v.setDistance(Double.POSITIVE_INFINITY);
			v.setPre(null);
		}
		
		// init start vertex
		highlightedVertices.add(start);
		start.setDistance(0.0);
		start.setPre(null);

		// create new heap (binary heap)
		PriorityQueue<Vertex> q = new PriorityQueue<>(new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				// a negative integer, zero, or a positive integer
				// as the first argument is less than, equal to, or greater than the second.
				if (o1.getDistance() < o2.getDistance()) return -1;
				if (o1.getDistance() > o2.getDistance()) return  1;
				return 0;
			}
		});

		// insert vertices HEAP INSERT
		// for (Vertex v : vertices) {	q.add(v); }
		q.addAll(vertices);

		// while heap is not empty
		while (!q.isEmpty()) {
			Vertex u = q.poll(); // HEAP EXTRACT MIN
			if (u == null) return;

			// for all neighbours of u
			for (Vertex v : graph.neighbours(u)) {
				//highlightedVertices = new LinkedList<>();
				double distance = u.getDistance() + graph.getEdgeWeight(v, u);
				
				if (v.getDistance() > distance) {
					// HEAP DECREASE-KEY not supported 
					q.remove(v);

					v.setDistance(distance);
					v.setPre(u);

					q.add(v);
					// END

					// highlight vertex
					highlightedVertices.add(v);
					revalidate();
				}
			}
		}
		
		// highlighted edges
		for (Vertex v : vertices) {
			if (v.getPre() == null) continue;
			for (Edge e : v.getEdges()) {
				if (v.getPre().getEdges().contains(e)) {
					highlightedEdges.add(e);
					break;
				}
			}
		}

	}
	
	public void astar(Vertex from, Vertex to) throws NoSuchElementException {
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
			if (u == null) return;
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
				
				printPath(to);
				return;
			}
			
			closedList.add(u);
			expandNode(openList, closedList, u);
		}
		System.out.println("Es konnte kein Pfad gefunden werden...");
	}
	
	private void printPath(Vertex end) {
		if (end.getPre() != null) {
			graph.getHighlightedVerticies().add(end);
			Edge e = graph.getEdge(end, end.getPre());
			graph.getHighlightedEdges().add(e);
			
			printPath(end.getPre());
		}
	}
	
	private void expandNode(PriorityQueue<Vertex> openList, LinkedList<Vertex> closedList, Vertex u) {
		
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
	
	
	
} // END - Class Gui
