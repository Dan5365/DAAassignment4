package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Graph data model.
 */
public class GraphTest {
    
    @Test
    public void testGraphCreation() {
        Graph graph = new Graph(5);
        assertEquals(5, graph.getN());
        assertEquals(0, graph.getEdgeCount());
    }
    
    @Test
    public void testAddEdges() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.5);
        graph.addEdge(1, 2, 3.0);
        
        assertEquals(2, graph.getEdgeCount());
        assertEquals(1, graph.getNeighbors(0).size());
        assertEquals(1, graph.getNeighbors(1).size());
        assertEquals(0, graph.getNeighbors(2).size());
    }
    
    @Test
    public void testReverseGraph() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);
        
        Graph reversed = graph.reverse();
        assertEquals(3, reversed.getN());
        assertEquals(2, reversed.getEdgeCount());
        
        List<Edge> neighbors = reversed.getNeighbors(1);
        assertEquals(1, neighbors.size());
        assertEquals(0, neighbors.get(0).getV());
        
        neighbors = reversed.getNeighbors(2);
        assertEquals(1, neighbors.size());
        assertEquals(1, neighbors.get(0).getV());
    }
    
    @Test
    public void testGraphFromGraphData() {
        GraphData.EdgeData edge1 = new GraphData.EdgeData();
        edge1.setU(0);
        edge1.setV(1);
        edge1.setW(2.5);
        
        GraphData data = new GraphData();
        data.setN(2);
        data.setDirected(true);
        data.setEdges(java.util.Arrays.asList(edge1));
        
        Graph graph = new Graph(data);
        assertEquals(2, graph.getN());
        assertEquals(1, graph.getEdgeCount());
    }
}

