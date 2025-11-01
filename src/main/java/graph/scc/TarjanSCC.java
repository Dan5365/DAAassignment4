package graph.scc;

import model.Edge;
import model.Graph;
import util.AlgorithmMetrics;

import java.util.*;

/**
 * Implementation of Tarjan's algorithm for finding Strongly Connected Components (SCCs).
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class TarjanSCC {
    private final Graph graph;
    private final AlgorithmMetrics metrics;
    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private List<List<Integer>> sccs;
    
    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new AlgorithmMetrics();
    }
    
    /**
     * Finds all strongly connected components in the graph.
     * @return list of SCCs, each SCC is a list of vertex indices
     */
    public List<List<Integer>> findSCCs() {
        metrics.reset();
        metrics.startTiming();
        
        int n = graph.getN();
        index = 0;
        indices = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        
        // Initialize arrays
        Arrays.fill(indices, -1);
        
        // Run DFS from each unvisited vertex
        for (int v = 0; v < n; v++) {
            if (indices[v] == -1) {
                strongConnect(v);
            }
        }
        
        metrics.stopTiming();
        return new ArrayList<>(sccs);
    }
    
    /**
     * Recursive helper method for Tarjan's algorithm.
     * @param v current vertex being processed
     */
    private void strongConnect(int v) {
        metrics.incrementDfsVisits();
        
        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;
        
        // Consider successors of v
        for (Edge edge : graph.getNeighbors(v)) {
            metrics.incrementEdgeTraversals();
            int w = edge.getV();
            
            if (indices[w] == -1) {
                // Successor w has not yet been visited; recurse on it
                strongConnect(w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
            } else if (onStack[w]) {
                // Successor w is in stack S and hence in the current SCC
                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
            }
        }
        
        // If v is a root node, pop the stack and create an SCC
        if (lowlinks[v] == indices[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            sccs.add(component);
        }
    }
    
    /**
     * Gets the metrics for the last SCC computation.
     * @return AlgorithmMetrics object
     */
    public AlgorithmMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Maps each vertex to its SCC index.
     * @return array where vertexToSCC[v] is the index of the SCC containing vertex v
     */
    public int[] getVertexToSCCMapping() {
        if (sccs == null || sccs.isEmpty()) {
            findSCCs();
        }
        
        int n = graph.getN();
        int[] vertexToSCC = new int[n];
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToSCC[vertex] = i;
            }
        }
        return vertexToSCC;
    }
}


