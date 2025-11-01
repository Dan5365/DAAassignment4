package org.example;

import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPath;
import graph.scc.CondensationGraph;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import model.Graph;
import model.GraphData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete pipeline: SCC -> Condensation -> Topo -> Shortest/Longest Paths
 */
public class IntegrationTest {
    
    @Test
    public void testCompletePipeline() throws IOException {
        // Load the sample tasks.json
        GraphData graphData = GraphData.loadFromFile("data/tasks.json");
        Graph graph = new Graph(graphData);
        
        // 1. Find SCCs
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        assertNotNull(sccs);
        assertTrue(sccs.size() > 0);
        
        // 2. Build condensation graph
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        Graph condGraph = condensation.build();
        assertNotNull(condGraph);
        
        // 3. Topological sort should succeed (condensation is a DAG)
        TopologicalSort topo = new TopologicalSort(condGraph);
        List<Integer> topoOrder = topo.kahnTopologicalSort();
        assertNotNull(topoOrder, "Condensation graph must be acyclic");
        assertEquals(condGraph.getN(), topoOrder.size());
        
        // 4. Shortest paths (if source exists)
        if (graphData.getSource() != null) {
            int sourceComp = vertexToSCC[graphData.getSource()];
            DAGShortestPath sp = new DAGShortestPath(condGraph, sourceComp);
            double[] distances = sp.getDistances();
            assertNotNull(distances);
            assertEquals(0.0, distances[sourceComp], 0.001);
        }
        
        // 5. Longest path
        DAGLongestPath lp = new DAGLongestPath(condGraph);
        double length = lp.getLongestPathLength();
        assertTrue(length >= 0 || length == Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void testGraphWithSingleSCC() {
        // Graph where all nodes form one large SCC
        Graph graph = new Graph(4);
        graph.addEdge(0, 1, 1.0);
        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 1.0);
        graph.addEdge(3, 0, 1.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(2, 0, 1.0);
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        assertEquals(1, sccs.size());
        assertEquals(4, sccs.get(0).size());
        
        // Condensation should have 1 vertex
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        Graph condGraph = condensation.build();
        assertEquals(1, condGraph.getN());
        assertEquals(0, condGraph.getEdgeCount()); // No edges in single-vertex graph
    }
    
    @Test
    public void testGraphWithIsolatedVertices() {
        // Graph with isolated vertices
        Graph graph = new Graph(5);
        graph.addEdge(0, 1, 1.0);
        // Vertices 2, 3, 4 are isolated
        
        TarjanSCC tarjan = new TarjanSCC(graph);
        List<List<Integer>> sccs = tarjan.findSCCs();
        // Each isolated vertex is its own SCC, plus the edge forms another component
        assertTrue(sccs.size() >= 3);
        
        int[] vertexToSCC = tarjan.getVertexToSCCMapping();
        CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
        Graph condGraph = condensation.build();
        
        TopologicalSort topo = new TopologicalSort(condGraph);
        List<Integer> order = topo.kahnTopologicalSort();
        assertNotNull(order);
    }
}

