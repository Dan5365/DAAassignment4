package graph.dagsp;

import model.Edge;
import model.Graph;
import graph.topo.TopologicalSort;
import util.AlgorithmMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of longest path (critical path) in a Directed Acyclic Graph (DAG).
 * Uses topological ordering with sign inversion to compute longest paths.
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class DAGLongestPath {
    private final Graph dag;
    private final AlgorithmMetrics metrics;
    private double[] dist;
    private int[] parent;
    private int longestPathSource;
    private int longestPathTarget;
    private double longestPathLength;
    
    public DAGLongestPath(Graph dag) {
        this.dag = dag;
        this.metrics = new AlgorithmMetrics();
    }
    
    /**
     * Computes the longest path (critical path) in the DAG.
     * @return length of the longest path
     */
    public double computeLongestPath() {
        metrics.reset();
        metrics.startTiming();
        
        int n = dag.getN();
        dist = new double[n];
        parent = new int[n];
        
        Arrays.fill(dist, Double.NEGATIVE_INFINITY);
        Arrays.fill(parent, -1);
        
        // Get topological order
        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> topoOrder = topo.kahnTopologicalSort();
        
        if (topoOrder == null) {
            // Graph has cycles
            metrics.stopTiming();
            longestPathLength = Double.NEGATIVE_INFINITY;
            return longestPathLength;
        }
        
        // Initialize all sources (in-degree 0) to distance 0
        boolean[] hasIncoming = new boolean[n];
        for (int u = 0; u < n; u++) {
            for (Edge edge : dag.getNeighbors(u)) {
                metrics.incrementEdgeTraversals();
                hasIncoming[edge.getV()] = true;
            }
        }
        
        for (int i = 0; i < n; i++) {
            if (!hasIncoming[i]) {
                dist[i] = 0.0;
            }
        }
        
        // Relax edges in topological order (maximize distance)
        for (int u : topoOrder) {
            if (dist[u] != Double.NEGATIVE_INFINITY) {
                for (Edge edge : dag.getNeighbors(u)) {
                    metrics.incrementEdgeTraversals();
                    int v = edge.getV();
                    double weight = edge.getWeight();
                    
                    if (dist[u] + weight > dist[v]) {
                        metrics.incrementRelaxations();
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }
        
        // Find the vertex with maximum distance (end of critical path)
        longestPathLength = Double.NEGATIVE_INFINITY;
        longestPathTarget = -1;
        
        for (int i = 0; i < n; i++) {
            if (dist[i] > longestPathLength) {
                longestPathLength = dist[i];
                longestPathTarget = i;
            }
        }
        
        // Trace back to find the source
        longestPathSource = longestPathTarget;
        while (longestPathSource != -1 && parent[longestPathSource] != -1) {
            longestPathSource = parent[longestPathSource];
        }
        
        metrics.stopTiming();
        return longestPathLength;
    }
    
    /**
     * Gets the length of the longest path (critical path).
     * @return length of critical path
     */
    public double getLongestPathLength() {
        if (dist == null) {
            computeLongestPath();
        }
        return longestPathLength;
    }
    
    /**
     * Reconstructs the critical path (longest path).
     * @return list of vertices on the critical path
     */
    public List<Integer> getCriticalPath() {
        if (dist == null) {
            computeLongestPath();
        }
        
        if (longestPathTarget == -1 || longestPathLength == Double.NEGATIVE_INFINITY) {
            return Collections.emptyList();
        }
        
        List<Integer> path = new ArrayList<>();
        int current = longestPathTarget;
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Gets the source vertex of the critical path.
     * @return source vertex index
     */
    public int getCriticalPathSource() {
        if (dist == null) {
            computeLongestPath();
        }
        return longestPathSource;
    }
    
    /**
     * Gets the target vertex of the critical path.
     * @return target vertex index
     */
    public int getCriticalPathTarget() {
        if (dist == null) {
            computeLongestPath();
        }
        return longestPathTarget;
    }
    
    /**
     * Gets the metrics for the longest path computation.
     * @return AlgorithmMetrics object
     */
    public AlgorithmMetrics getMetrics() {
        return metrics;
    }
}


