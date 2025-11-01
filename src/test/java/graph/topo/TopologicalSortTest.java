package graph.topo;

import model.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Topological Sort.
 */
public class TopologicalSortTest {
    
    @Test
    public void testSimpleDAG() {
        // Graph: 0 -> 1 -> 2
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertNotNull(order);
        assertEquals(3, order.size());
        assertEquals(0, order.get(0));
        assertEquals(1, order.get(1));
        assertEquals(2, order.get(2));
    }
    
    @Test
    public void testDAGWithMultipleSources() {
        // Graph: 0 -> 2, 1 -> 2, 2 -> 3
        Graph graph = new Graph(4);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);
        
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertNotNull(order);
        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }
    
    @Test
    public void testCycleDetection() {
        // Graph: 0 -> 1 -> 2 -> 0 (cycle)
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 0, 1.0);
        
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertNull(order); // Cycle detected
    }
    
    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1);
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahnTopologicalSort();
        
        assertNotNull(order);
        assertEquals(1, order.size());
        assertEquals(0, order.get(0));
    }
    
    @Test
    public void testDFSTopologicalSort() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(0, 2, 1.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 1.0);
        
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.dfsTopologicalSort();
        
        assertNotNull(order);
        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }
    
    @Test
    public void testMetrics() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        
        TopologicalSort topo = new TopologicalSort(graph);
        topo.kahnTopologicalSort();
        
        assertNotNull(topo.getMetrics());
        assertTrue(topo.getMetrics().getTimeNanos() >= 0);
    }
}


