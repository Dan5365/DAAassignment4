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
 * Implementation of single-source shortest paths in a Directed Acyclic Graph (DAG).
 * Uses topological ordering to compute shortest paths in O(V + E) time.
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class DAGShortestPath {
    private final Graph dag;
    private final int source;
    private final AlgorithmMetrics metrics;
    private double[] dist;
    private int[] parent;
    
    public DAGShortestPath(Graph dag, int source) {
        this.dag = dag;
        this.source = source;
        this.metrics = new AlgorithmMetrics();
    }
    
    /**
     * Computes shortest distances from source to all vertices.
     * @return array of shortest distances (Double.POSITIVE_INFINITY if unreachable)
     */
    public double[] computeShortestPaths() {
        metrics.reset();
        metrics.startTiming();
        
        int n = dag.getN();
        dist = new double[n];
        parent = new int[n];
        
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        dist[source] = 0.0;
        
        // Get topological order
        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> topoOrder = topo.kahnTopologicalSort();
        
        if (topoOrder == null) {
            // Graph has cycles (shouldn't happen if it's truly a DAG)
            metrics.stopTiming();
            return dist;
        }
        
        // Relax edges in topological order
        for (int u : topoOrder) {
            if (dist[u] != Double.POSITIVE_INFINITY) {
                for (Edge edge : dag.getNeighbors(u)) {
                    metrics.incrementEdgeTraversals();
                    int v = edge.getV();
                    double weight = edge.getWeight();
                    
                    if (dist[u] + weight < dist[v]) {
                        metrics.incrementRelaxations();
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }
        
        metrics.stopTiming();
        return dist;
    }
    
    /**
     * Gets the shortest distance to a target vertex.
     * @param target target vertex
     * @return shortest distance, or Double.POSITIVE_INFINITY if unreachable
     */
    public double getDistance(int target) {
        if (dist == null) {
            computeShortestPaths();
        }
        return dist[target];
    }
    
    /**
     * Reconstructs the shortest path from source to target.
     * @param target target vertex
     * @return list of vertices on the shortest path, or empty list if unreachable
     */
    public List<Integer> getPath(int target) {
        if (dist == null) {
            computeShortestPaths();
        }
        
        if (dist[target] == Double.POSITIVE_INFINITY) {
            return Collections.emptyList();
        }
        
        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Gets the metrics for the shortest path computation.
     * @return AlgorithmMetrics object
     */
    public AlgorithmMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Gets all distances (computes if not already computed).
     * @return array of shortest distances
     */
    public double[] getDistances() {
        if (dist == null) {
            computeShortestPaths();
        }
        return dist;
    }
}


