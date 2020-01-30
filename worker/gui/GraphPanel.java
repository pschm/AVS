package gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

import app.Edge;
import app.Graph;
import app.Position;
import app.Vertex;

@SuppressWarnings("serial")
public class GraphPanel extends JPanel {
	private Graph graph;

    public GraphPanel(Graph g) {
        graph = g;
    }
    

    @Override
    public void paint(Graphics g) {
    	
    	// test - background
    	g.setColor(new Color(140, 140, 140));
    	g.fillRect(0, 0, getWidth(), getHeight());

    	// print Edges
    	for (Edge e : graph.getEdges()) {
    		// use different color, if edge is in highlighted list
    		if (graph.getHighlightedEdges().contains(e))
    			g.setColor(new Color(255, 0, 0));
    		else
    			g.setColor(new Color(175, 255, 255));

    		// draw Line
    		Position p1 = e.getFrom().getPosition();
    		Position p2 = e.getTo().getPosition();
    		g.drawLine(p1.x, p1.y, p2.x, p2.y);
    	}
    	
    	// print Vertices
    	for (Vertex v : graph.getVertices()) {
    		Position p = v.getPosition();
    		
    		
    		int vertexSize = 20;
    		
    		// use different color, if vertex is in highlighted list
    		if (graph.getHighlightedVerticies().contains(v))
    			g.setColor(new Color(255, 0, 0));
    		else
    			g.setColor(new Color(255, 255, 255));
    		
    		// print bubble
    		g.fillOval(
    				p.x - (vertexSize / 2),
    				p.y - (vertexSize / 2),
    				vertexSize,
    				vertexSize
    		);
    		
    		// print IDs
    		g.setColor(new Color(255, 255, 255));
    		g.drawString("" +v.getId(), p.x + 17, p.y + 1);
    	}
    	
    	
    }
    

}
