package util;

/**
 * Interface for tracking algorithm metrics including operation counters and timing.
 * Used for instrumentation of graph algorithms.
 */
public interface Metrics {
    /**
     * Gets the execution time in nanoseconds.
     * @return execution time in nanoseconds
     */
    long getTimeNanos();
    
    /**
     * Gets the execution time in milliseconds.
     * @return execution time in milliseconds
     */
    double getTimeMillis();
    
    /**
     * Resets all counters and timing.
     */
    void reset();
    
    /**
     * Returns a string representation of all metrics.
     * @return formatted metrics string
     */
    String toString();
}


