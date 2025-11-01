package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GraphData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating test graph datasets.
 * Generates graphs with different sizes, densities, and structures.
 */
public class GraphGenerator {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    
    /**
     * Generates a graph with specified parameters.
     * @param n number of vertices
     * @param density edge density (0.0 to 1.0)
     * @param hasCycles whether to include cycles
     * @param multipleSCCs whether to create multiple SCCs
     * @param source source vertex index (null for no source)
     * @return GraphData object
     */
    public static GraphData generateGraph(int n, double density, boolean hasCycles, 
                                         boolean multipleSCCs, Integer source) {
        GraphData data = new GraphData();
        List<GraphData.EdgeData> edges = new ArrayList<>();
        
        // Ensure minimum connectivity
        int minEdges = n - 1;
        int maxEdges = (int) (density * n * (n - 1));
        int targetEdges = Math.max(minEdges, maxEdges);
        
        if (hasCycles && multipleSCCs) {
            // Create multiple SCCs
            int numSCCs = Math.max(2, n / 4);
            int verticesPerSCC = n / numSCCs;
            
            // Create edges within each SCC (cycles)
            for (int scc = 0; scc < numSCCs; scc++) {
                int start = scc * verticesPerSCC;
                int end = (scc == numSCCs - 1) ? n : (scc + 1) * verticesPerSCC;
                
                // Create cycle within SCC
                for (int i = start; i < end - 1; i++) {
                    edges.add(createEdge(i, i + 1, random.nextDouble() * 10 + 1));
                }
                if (end - start > 1) {
                    edges.add(createEdge(end - 1, start, random.nextDouble() * 10 + 1));
                }
                
                // Add some additional edges within SCC
                int extraEdges = Math.max(0, (end - start) / 2);
                for (int i = 0; i < extraEdges; i++) {
                    int u = start + random.nextInt(end - start);
                    int v = start + random.nextInt(end - start);
                    if (u != v && !edgeExists(edges, u, v)) {
                        edges.add(createEdge(u, v, random.nextDouble() * 10 + 1));
                    }
                }
            }
            
            // Add edges between SCCs (DAG structure)
            for (int scc = 0; scc < numSCCs - 1; scc++) {
                int fromStart = scc * verticesPerSCC;
                int fromEnd = (scc == numSCCs - 1) ? n : (scc + 1) * verticesPerSCC;
                int toStart = (scc + 1) * verticesPerSCC;
                int toEnd = (scc + 2) == numSCCs ? n : (scc + 2) * verticesPerSCC;
                
                int u = fromStart + random.nextInt(fromEnd - fromStart);
                int v = toStart + random.nextInt(toEnd - toStart);
                edges.add(createEdge(u, v, random.nextDouble() * 10 + 1));
            }
            
        } else if (hasCycles) {
            // Single large cycle or cyclic structure
            for (int i = 0; i < n - 1; i++) {
                edges.add(createEdge(i, i + 1, random.nextDouble() * 10 + 1));
            }
            edges.add(createEdge(n - 1, 0, random.nextDouble() * 10 + 1)); // Cycle back
            
            // Add additional random edges
            while (edges.size() < targetEdges) {
                int u = random.nextInt(n);
                int v = random.nextInt(n);
                if (u != v && !edgeExists(edges, u, v)) {
                    edges.add(createEdge(u, v, random.nextDouble() * 10 + 1));
                }
            }
            
        } else {
            // Pure DAG - chain structure with some branches
            for (int i = 0; i < n - 1; i++) {
                edges.add(createEdge(i, i + 1, random.nextDouble() * 10 + 1));
            }
            
            // Add some parallel branches
            int branches = Math.min(n / 3, targetEdges - (n - 1));
            for (int i = 0; i < branches; i++) {
                int u = random.nextInt(n - 1);
                int v = u + 1 + random.nextInt(n - u - 1);
                if (!edgeExists(edges, u, v)) {
                    edges.add(createEdge(u, v, random.nextDouble() * 10 + 1));
                }
            }
        }
        
        data.setN(n);
        data.setDirected(true);
        data.setWeightModel("edge");
        data.setSource(source != null ? source : 0);
        data.setEdges(edges);
        
        return data;
    }
    
    private static GraphData.EdgeData createEdge(int u, int v, double w) {
        return new GraphData.EdgeData(u, v, w);
    }
    
    private static boolean edgeExists(List<GraphData.EdgeData> edges, int u, int v) {
        return edges.stream().anyMatch(e -> e.getU() == u && e.getV() == v);
    }
    
    /**
     * Saves GraphData to a JSON file.
     */
    public static void saveToFile(GraphData data, String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }
    
    /**
     * Generates all required datasets.
     */
    public static void generateAllDatasets() throws IOException {
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Small datasets (6-10 nodes)
        System.out.println("Generating small datasets...");
        GraphData small1 = generateGraph(8, 0.3, true, false, 0);
        saveToFile(small1, "data/small_cyclic.json");
        
        GraphData small2 = generateGraph(10, 0.2, false, false, 0);
        saveToFile(small2, "data/small_dag.json");
        
        GraphData small3 = generateGraph(7, 0.4, true, true, 3);
        saveToFile(small3, "data/small_multiple_scc.json");
        
        // Medium datasets (10-20 nodes)
        System.out.println("Generating medium datasets...");
        GraphData medium1 = generateGraph(15, 0.25, true, false, 0);
        saveToFile(medium1, "data/medium_cyclic.json");
        
        GraphData medium2 = generateGraph(18, 0.2, false, false, 2);
        saveToFile(medium2, "data/medium_dag.json");
        
        GraphData medium3 = generateGraph(16, 0.35, true, true, 5);
        saveToFile(medium3, "data/medium_multiple_scc.json");
        
        // Large datasets (20-50 nodes)
        System.out.println("Generating large datasets...");
        GraphData large1 = generateGraph(30, 0.2, true, false, 0);
        saveToFile(large1, "data/large_cyclic.json");
        
        GraphData large2 = generateGraph(35, 0.15, false, false, 3);
        saveToFile(large2, "data/large_dag.json");
        
        GraphData large3 = generateGraph(28, 0.3, true, true, 7);
        saveToFile(large3, "data/large_multiple_scc.json");
        
        System.out.println("All datasets generated successfully!");
    }
    
    public static void main(String[] args) {
        try {
            generateAllDatasets();
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

