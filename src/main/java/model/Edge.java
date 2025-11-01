package model;

/**
 * Represents a directed edge in the graph.
 */
public class Edge {
    private final int u;
    private final int v;
    private final double weight;
    
    public Edge(int u, int v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
    
    public int getU() {
        return u;
    }
    
    public int getV() {
        return v;
    }
    
    public double getWeight() {
        return weight;
    }
    
    @Override
    public String toString() {
        return String.format("(%d -> %d, w=%.1f)", u, v, weight);
    }
}


