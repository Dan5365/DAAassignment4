package graph.scc;

import model.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for Condensation Graph builder.
 */
public class CondensationGraphTest {
    
    @Test
    public void testCondensationFromTwoSCCs() {
        // Graph: SCC1: 0<->1, SCC2: 2<->3, edge from SCC1 to SCC2
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 0, 1.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(3, 2, 1.0);
        graph.addEdge(0, 2, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        var sccs = tarjan.findSCCs();
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        Graph condGraph = condensation.build();
        
        // Should have 2 vertices (one per SCC)
        assertEquals(2, condGraph.getN());
        // Should have at least 1 edge (from SCC1 to SCC2)
        assertTrue(condGraph.getEdgeCount() >= 1);
    }
    
    @Test
    public void testCondensationFromDAG() {
        // Pure DAG - each vertex is its own SCC
        Graph graph = new Graph(3);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        var sccs = tarjan.findSCCs();
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        Graph condGraph = condensation.build();
        
        // In DAG, condensation should be isomorphic to original
        assertEquals(3, condGraph.getN());
        assertEquals(2, condGraph.getEdgeCount());
    }
    
    @Test
    public void testMetrics() {
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 0, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        var sccs = tarjan.findSCCs();
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        condensation.build();
        
        assertNotNull(condensation.getMetrics());
        assertTrue(condensation.getMetrics().getTimeNanos() >= 0);
    }
}


