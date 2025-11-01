package graph.topo;

import model.Edge;
import model.Graph;
import util.AlgorithmMetrics;

import java.util.*;

/**
 * Implementation of topological sort using Kahn's algorithm (BFS-based).
 * Also provides DFS-based variant.
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class TopologicalSort {
    private final Graph graph;
    private final AlgorithmMetrics metrics;
    
    public TopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new AlgorithmMetrics();
    }
    
    /**
     * Computes topological order using Kahn's algorithm.
     * @return list of vertices in topological order, or null if cycle detected
     */
    public List<Integer> kahnTopologicalSort() {
        metrics.reset();
        metrics.startTiming();
        
        int n = graph.getN();
        int[] inDegree = new int[n];
        
        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Edge edge : graph.getNeighbors(u)) {
                metrics.incrementEdgeTraversals();
                inDegree[edge.getV()]++;
            }
        }
        
        // Initialize queue with all vertices with in-degree 0
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementQueueOperations();
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        // Process vertices
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementQueueOperations();
            result.add(u);
            
            // Remove u and update in-degrees of neighbors
            for (Edge edge : graph.getNeighbors(u)) {
                metrics.incrementEdgeTraversals();
                int v = edge.getV();
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementQueueOperations();
                }
            }
        }
        
        metrics.stopTiming();
        
        // Check if all vertices were processed (graph is acyclic)
        if (result.size() != n) {
            // Cycle detected
            return null;
        }
        
        return result;
    }
    
    /**
     * Computes topological order using DFS-based algorithm.
     * @return list of vertices in topological order (reversed finish times), or null if cycle detected
     */
    public List<Integer> dfsTopologicalSort() {
        metrics.reset();
        metrics.startTiming();
        
        int n = graph.getN();
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];
        Deque<Integer> result = new ArrayDeque<>();
        
        // Check for cycles and build topological order
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (!dfsTopologicalHelper(i, visited, recStack, result)) {
                    // Cycle detected
                    metrics.stopTiming();
                    return null;
                }
            }
        }
        
        metrics.stopTiming();
        return new ArrayList<>(result);
    }
    
    /**
     * Helper method for DFS-based topological sort.
     * @param u current vertex
     * @param visited visited array
     * @param recStack recursion stack for cycle detection
     * @param result result stack (finish times in reverse order)
     * @return false if cycle detected, true otherwise
     */
    private boolean dfsTopologicalHelper(int u, boolean[] visited, boolean[] recStack, Deque<Integer> result) {
        if (recStack[u]) {
            // Cycle detected
            return false;
        }
        
        if (visited[u]) {
            return true;
        }
        
        metrics.incrementDfsVisits();
        visited[u] = true;
        recStack[u] = true;
        
        for (Edge edge : graph.getNeighbors(u)) {
            metrics.incrementEdgeTraversals();
            if (!dfsTopologicalHelper(edge.getV(), visited, recStack, result)) {
                return false;
            }
        }
        
        recStack[u] = false;
        result.push(u); // Add to front (reverse finish time order)
        return true;
    }
    
    /**
     * Gets the metrics for the last topological sort computation.
     * @return AlgorithmMetrics object
     */
    public AlgorithmMetrics getMetrics() {
        return metrics;
    }
}


