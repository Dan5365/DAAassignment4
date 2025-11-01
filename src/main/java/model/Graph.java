package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a directed graph with weighted edges.
 */
public class Graph {
    private final int n;
    private final List<List<Edge>> adjacencyList;
    private final List<Edge> allEdges;
    
    public Graph(int n) {
        this.n = n;
        this.adjacencyList = new ArrayList<>();
        this.allEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }
    
    public Graph(GraphData data) {
        this(data.getN());
        for (GraphData.EdgeData edgeData : data.getEdges()) {
            addEdge(edgeData.getU(), edgeData.getV(), edgeData.getW());
        }
    }
    
    public void addEdge(int u, int v, double weight) {
        Edge edge = new Edge(u, v, weight);
        adjacencyList.get(u).add(edge);
        allEdges.add(edge);
    }
    
    public int getN() {
        return n;
    }
    
    public List<Edge> getNeighbors(int u) {
        return adjacencyList.get(u);
    }
    
    public List<Edge> getAllEdges() {
        return allEdges;
    }
    
    /**
     * Creates a reversed graph (all edges reversed).
     * @return new Graph with reversed edges
     */
    public Graph reverse() {
        Graph reversed = new Graph(n);
        for (Edge edge : allEdges) {
            reversed.addEdge(edge.getV(), edge.getU(), edge.getWeight());
        }
        return reversed;
    }
    
    /**
     * Gets the number of edges in the graph.
     * @return number of edges
     */
    public int getEdgeCount() {
        return allEdges.size();
    }
}


