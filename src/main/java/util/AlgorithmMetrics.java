package util;

/**
 * Implementation of Metrics interface for tracking algorithm performance.
 * Tracks execution time and various operation counters.
 */
public class AlgorithmMetrics implements Metrics {
    private long startTime;
    private long endTime;
    private long dfsVisits;
    private long edgeTraversals;
    private long queueOperations; // pushes and pops
    private long relaxations;
    
    public AlgorithmMetrics() {
        reset();
    }
    
    public void startTiming() {
        startTime = System.nanoTime();
    }
    
    public void stopTiming() {
        endTime = System.nanoTime();
    }
    
    public void incrementDfsVisits() {
        dfsVisits++;
    }
    
    public void incrementEdgeTraversals() {
        edgeTraversals++;
    }
    
    public void incrementQueueOperations() {
        queueOperations++;
    }
    
    public void incrementRelaxations() {
        relaxations++;
    }
    
    @Override
    public long getTimeNanos() {
        return endTime - startTime;
    }
    
    @Override
    public double getTimeMillis() {
        return getTimeNanos() / 1_000_000.0;
    }
    
    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        dfsVisits = 0;
        edgeTraversals = 0;
        queueOperations = 0;
        relaxations = 0;
    }
    
    public long getDfsVisits() {
        return dfsVisits;
    }
    
    public long getEdgeTraversals() {
        return edgeTraversals;
    }
    
    public long getQueueOperations() {
        return queueOperations;
    }
    
    public long getRelaxations() {
        return relaxations;
    }
    
    @Override
    public String toString() {
        return String.format("Time: %.3f ms | DFS Visits: %d | Edge Traversals: %d | Queue Ops: %d | Relaxations: %d",
                getTimeMillis(), dfsVisits, edgeTraversals, queueOperations, relaxations);
    }
}


