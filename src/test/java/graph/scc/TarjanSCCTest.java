package graph.scc;

import model.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Tarjan's SCC algorithm.
 */
public class TarjanSCCTest {
    
    @Test
    public void testSimpleCycle() {
        // Graph: 0 -> 1 -> 2 -> 0 (single SCC)
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 0, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        
        assertEquals(1, sccs.size());
        Set<Integer> component = sccs.get(0).stream().collect(Collectors.toSet());
        assertEquals(Set.of(0, 1, 2), component);
    }
    
    @Test
    public void testTwoSCCs() {
        // Graph: SCC1: 0<->1, SCC2: 2<->3, edge from SCC1 to SCC2
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 0, 1.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(3, 2, 1.0);
        graph.addEdge(0, 2, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        
        assertEquals(2, sccs.size());
        
        // Check that vertices are grouped correctly
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        assertEquals(vertexToSCC[0], vertexToSCC[1]); // Same SCC
        assertEquals(vertexToSCC[2], vertexToSCC[3]); // Same SCC
        assertNotEquals(vertexToSCC[0], vertexToSCC[2]); // Different SCCs
    }
    
    @Test
    public void testDAG() {
        // Pure DAG: 0 -> 1 -> 2 -> 3 (all separate SCCs)
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        
        // In a DAG, each vertex is its own SCC
        assertEquals(4, sccs.size());
    }
    
    @Test
    public void testSingleVertex() {
        Graph graph = new Graph(1);
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        
        assertEquals(1, sccs.size());
        assertEquals(1, sccs.get(0).size());
        assertEquals(0, sccs.get(0).get(0));
    }
    
    @Test
    public void testMetrics() {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 0, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        tarjan.findSCCs();
        
        assertNotNull(tarjan.getMetrics());
        assertTrue(tarjan.getMetrics().getTimeNanos() >= 0);
        assertTrue(tarjan.getMetrics().getDfsVisits() > 0);
    }
}


