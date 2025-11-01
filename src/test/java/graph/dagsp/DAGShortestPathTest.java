package graph.dagsp;

import model.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for DAG Shortest Path algorithm.
 */
public class DAGShortestPathTest {
    
    @Test
    public void testSimplePath() {
        // Graph: 0 -> 1 (w=2) -> 2 (w=3)
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);
        
        DAGShortestPath sp = new DAGShortestPath(graph, 0);
        double[] distances = sp.getDistances();
        
        assertEquals(0.0, distances[0], 0.001);
        assertEquals(2.0, distances[1], 0.001);
        assertEquals(5.0, distances[2], 0.001);
    }
    
    @Test
    public void testMultiplePaths() {
        // Graph: 0 -> 1 (w=5), 0 -> 2 (w=2), 1 -> 3 (w=1), 2 -> 3 (w=4)
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5.0);
        graph.addEdge(0, 2, 2.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 4.0);
        
        DAGShortestPath sp = new DAGShortestPath(graph, 0);
        double[] distances = sp.getDistances();
        
        assertEquals(0.0, distances[0], 0.001);
        assertEquals(5.0, distances[1], 0.001);
        assertEquals(2.0, distances[2], 0.001);
        assertEquals(6.0, distances[3], 0.001); // min(5+1, 2+4) = 6
    }
    
    @Test
    public void testUnreachableVertex() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        // Vertex 2 is unreachable from 0
        
        DAGShortestPath sp = new DAGShortestPath(graph, 0);
        double[] distances = sp.getDistances();
        
        assertEquals(0.0, distances[0], 0.001);
        assertEquals(1.0, distances[1], 0.001);
        assertEquals(Double.POSITIVE_INFINITY, distances[2]);
    }
    
    @Test
    public void testPathReconstruction() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);
        graph.addEdge(2, 3, 1.0);
        
        DAGShortestPath sp = new DAGShortestPath(graph, 0);
        List<Integer> path = sp.getPath(3);
        
        assertEquals(List.of(0, 1, 2, 3), path);
    }
    
    @Test
    public void testMetrics() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        
        DAGShortestPath sp = new DAGShortestPath(graph, 0);
        sp.getDistances();
        
        assertNotNull(sp.getMetrics());
        assertTrue(sp.getMetrics().getTimeNanos() >= 0);
        assertTrue(sp.getMetrics().getRelaxations() >= 0);
    }
}


