package graph.dagsp;

import model.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for DAG Longest Path (Critical Path) algorithm.
 */
public class DAGLongestPathTest {
    
    @Test
    public void testSimplePath() {
        // Graph: 0 -> 1 (w=2) -> 2 (w=3)
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 2.0);
        graph.addEdge(1, 2, 3.0);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        double length = lp.getLongestPathLength();
        
        assertEquals(5.0, length, 0.001);
        
        List<Integer> path = lp.getCriticalPath();
        assertTrue(path.size() >= 2);
    }
    
    @Test
    public void testMultiplePaths() {
        // Graph: 0 -> 1 (w=5), 0 -> 2 (w=2), 1 -> 3 (w=1), 2 -> 3 (w=4)
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 5.0);
        graph.addEdge(0, 2, 2.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 3, 4.0);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        double length = lp.getLongestPathLength();
        
        assertEquals(6.0, length, 0.001); // max(5+1, 2+4) = 6
    }
    
    @Test
    public void testComplexPath() {
        // Graph with longer path
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 2.0);
        graph.addEdge(2, 3, 3.0);
        graph.addEdge(3, 4, 4.0);
        graph.addEdge(0, 4, 5.0); // Shorter direct path
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        double length = lp.getLongestPathLength();
        
        assertEquals(10.0, length, 0.001); // 1+2+3+4 = 10 (longer than 5)
        
        List<Integer> criticalPath = lp.getCriticalPath();
        assertTrue(criticalPath.contains(0));
        assertTrue(criticalPath.contains(4));
    }
    
    @Test
    public void testMetrics() {
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        lp.getLongestPathLength();
        
        assertNotNull(lp.getMetrics());
        assertTrue(lp.getMetrics().getTimeNanos() >= 0);
        assertTrue(lp.getMetrics().getRelaxations() >= 0);
    }
}


