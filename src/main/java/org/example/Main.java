package org.example;

import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPath;
import graph.scc.CondensationGraph;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import model.Graph;
import model.GraphData;

import java.io.IOException;
import java.util.List;

/**
 * Main class for Smart City / Smart Campus Scheduling assignment.
 * Orchestrates SCC detection, topological sorting, and shortest/longest paths in DAGs.
 */
public class Main {
    
    public static void main(String[] args) {
        // Use default file if no arguments provided (for IDE runs)
        String inputFile;
        if (args.length < 1) {
            inputFile = "data/tasks.json";
            System.out.println("No arguments provided, using default: " + inputFile);
        } else {
            inputFile = args[0];
        }
        
        try {

            System.out.println("Loading graph from: " + inputFile);
            GraphData graphData = GraphData.loadFromFile(inputFile);
            Graph graph = new Graph(graphData);
            
            System.out.println("\n=== Graph Information ===");
            System.out.println("Vertices: " + graph.getN());
            System.out.println("Edges: " + graph.getEdgeCount());
            System.out.println("Weight Model: " + graphData.getWeightModel());
            if (graphData.getSource() != null) {
                System.out.println("Source: " + graphData.getSource());
            }
            
            // 1. Find Strongly Connected Components
            System.out.println("\n=== 1. Strongly Connected Components (Tarjan) ===");
            TarjanSCC tarjan = new TarjanSCC(graph);
            List<List<Integer>> sccs = tarjan.findSCCs();
            
            System.out.println("Number of SCCs: " + sccs.size());
            for (int i = 0; i < sccs.size(); i++) {
                List<Integer> component = sccs.get(i);
                System.out.printf("  SCC %d: %s (size: %d)%n", i, component, component.size());
            }
            System.out.println("Metrics: " + tarjan.getMetrics());
            
            // Build condensation graph
            System.out.println("\n=== 2. Condensation Graph ===");
            int[] vertexToSCC = tarjan.getVertexToSCCMapping();
            CondensationGraph condensation = new CondensationGraph(graph, sccs, vertexToSCC);
            Graph condGraph = condensation.build();
            System.out.println("Condensation Graph Vertices: " + condGraph.getN());
            System.out.println("Condensation Graph Edges: " + condGraph.getEdgeCount());
            System.out.println("Metrics: " + condensation.getMetrics());
            
            // 2. Topological Sort
            System.out.println("\n=== 3. Topological Sort (Kahn) ===");
            TopologicalSort topo = new TopologicalSort(condGraph);
            List<Integer> topoOrder = topo.kahnTopologicalSort();
            
            if (topoOrder != null) {
                System.out.println("Topological Order of Components:");
                for (int i = 0; i < topoOrder.size(); i++) {
                    int comp = topoOrder.get(i);
                    System.out.printf("  Step %d: Component %d (vertices: %s)%n", 
                            i + 1, comp, sccs.get(comp));
                }
                
                // Derive order of original tasks
                System.out.println("\nDerived Order of Original Tasks:");
                int taskNum = 1;
                for (int comp : topoOrder) {
                    for (int vertex : sccs.get(comp)) {
                        System.out.printf("  Task %d: Vertex %d (SCC %d)%n", taskNum++, vertex, comp);
                    }
                }
            } else {
                System.out.println("Cycle detected! (Should not happen in condensation graph)");
            }
            System.out.println("Metrics: " + topo.getMetrics());
            
            // 3. Shortest Paths in DAG
            if (graphData.getSource() != null) {
                System.out.println("\n=== 4. Shortest Paths in DAG ===");
                // Map source vertex to its component
                int sourceComp = vertexToSCC[graphData.getSource()];
                System.out.println("Source Component: " + sourceComp);
                
                DAGShortestPath dagSP = new DAGShortestPath(condGraph, sourceComp);
                double[] distances = dagSP.getDistances();
                
                System.out.println("Shortest Distances from Component " + sourceComp + ":");
                for (int i = 0; i < distances.length; i++) {
                    if (distances[i] != Double.POSITIVE_INFINITY) {
                        System.out.printf("  Component %d: %.2f%n", i, distances[i]);
                    } else {
                        System.out.printf("  Component %d: unreachable%n", i);
                    }
                }
                
                // Show a path example
                if (distances.length > 1) {
                    for (int i = 0; i < distances.length; i++) {
                        if (i != sourceComp && distances[i] != Double.POSITIVE_INFINITY) {
                            List<Integer> path = dagSP.getPath(i);
                            System.out.printf("  Path to Component %d: %s%n", i, path);
                            break;
                        }
                    }
                }
                System.out.println("Metrics: " + dagSP.getMetrics());
            }
            
            // 4. Longest Path (Critical Path)
            System.out.println("\n=== 5. Longest Path (Critical Path) ===");
            DAGLongestPath dagLP = new DAGLongestPath(condGraph);
            double criticalPathLength = dagLP.getLongestPathLength();
            List<Integer> criticalPath = dagLP.getCriticalPath();
            
            System.out.printf("Critical Path Length: %.2f%n", criticalPathLength);
            System.out.println("Critical Path (Component order): " + criticalPath);
            
            // Expand to original vertices
            if (!criticalPath.isEmpty()) {
                System.out.println("Critical Path (Original vertices):");
                for (int comp : criticalPath) {
                    System.out.printf("  Component %d: %s%n", comp, sccs.get(comp));
                }
            }
            System.out.println("Metrics: " + dagLP.getMetrics());
            
            // Summary
            System.out.println("\n=== Summary ===");
            System.out.println("SCCs found: " + sccs.size());
            System.out.println("Condensation graph is a DAG: " + (topoOrder != null));
            if (graphData.getSource() != null) {
                System.out.println("Shortest paths computed from source component: " + vertexToSCC[graphData.getSource()]);
            }
            System.out.printf("Critical path length: %.2f%n", criticalPathLength);
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


