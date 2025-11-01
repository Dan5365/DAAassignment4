package graph.scc;

import model.Edge;
import model.Graph;
import util.AlgorithmMetrics;

import java.util.*;

/**
 * Builds a condensation graph (DAG) from the original graph by contracting SCCs.
 * Each SCC becomes a single node in the condensation graph.
 */
public class CondensationGraph {
    private final Graph originalGraph;
    private final List<List<Integer>> sccs;
    private final int[] vertexToSCC;
    private Graph condensationGraph;
    private AlgorithmMetrics metrics;
    
    public CondensationGraph(Graph originalGraph, List<List<Integer>> sccs, int[] vertexToSCC) {
        this.originalGraph = originalGraph;
        this.sccs = sccs;
        this.vertexToSCC = vertexToSCC;
        this.metrics = new AlgorithmMetrics();
    }
    
    /**
     * Builds the condensation graph (DAG of SCCs).
     * @return condensation graph where each node represents an SCC
     */
    public Graph build() {
        metrics.reset();
        metrics.startTiming();
        
        int numComponents = sccs.size();
        condensationGraph = new Graph(numComponents);
        
        // Track edges between components to avoid duplicates
        Set<String> edgeSet = new HashSet<>();
        
        // Process all edges from the original graph
        for (Edge edge : originalGraph.getAllEdges()) {
            metrics.incrementEdgeTraversals();
            
            int u = edge.getU();
            int v = edge.getV();
            int compU = vertexToSCC[u];
            int compV = vertexToSCC[v];
            
            // Only add edge if it goes between different components
            if (compU != compV) {
                String edgeKey = compU + "->" + compV;
                if (!edgeSet.contains(edgeKey)) {
                    edgeSet.add(edgeKey);
                    // Use minimum weight among all edges between these components
                    // or average - we'll use the first edge's weight
                    condensationGraph.addEdge(compU, compV, edge.getWeight());
                }
            }
        }
        
        metrics.stopTiming();
        return condensationGraph;
    }
    
    /**
     * Gets the condensation graph (builds it if not already built).
     * @return condensation graph
     */
    public Graph getCondensationGraph() {
        if (condensationGraph == null) {
            build();
        }
        return condensationGraph;
    }
    
    /**
     * Gets the list of SCCs.
     * @return list of strongly connected components
     */
    public List<List<Integer>> getSCCs() {
        return sccs;
    }
    
    /**
     * Gets the mapping from vertices to their SCC index.
     * @return vertex to SCC mapping
     */
    public int[] getVertexToSCC() {
        return vertexToSCC;
    }
    
    /**
     * Gets the metrics for the condensation building process.
     * @return AlgorithmMetrics object
     */
    public AlgorithmMetrics getMetrics() {
        return metrics;
    }
}


