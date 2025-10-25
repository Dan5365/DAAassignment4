package org.example.algs4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.PrimMST;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Main {

    static class EdgeData {
        String from;
        String to;
        double weight;
    }

    static class GraphData {
        int id;
        List<String> nodes;
        List<EdgeData> edges;
    }

    static class InputData {
        List<GraphData> graphs;
    }

    static class MstEdge {
        String from;
        String to;
        double weight;
        MstEdge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class MstResult {
        List<MstEdge> mst_edges;
        double total_cost;
        int operations_count;
        double execution_time_ms;
    }

    static class GraphResult {
        int graph_id;
        Map<String, Integer> input_stats;
        MstResult prim;
        MstResult kruskal;
    }

    static class Output {
        List<GraphResult> results;
    }

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        InputData data = gson.fromJson(new FileReader("src/main/resources/ass_3_input.json"), InputData.class);

        List<GraphResult> results = new ArrayList<>();

        for (GraphData graph : data.graphs) {
            int n = graph.nodes.size();
            EdgeWeightedGraph G = new EdgeWeightedGraph(n);

            for (EdgeData e : graph.edges) {
                int v = e.from.charAt(0) - 'A';
                int w = e.to.charAt(0) - 'A';
                G.addEdge(new Edge(v, w, e.weight));
            }

            // ==== PRIM ====
            long primStart = System.nanoTime();
            PrimMST prim = new PrimMST(G);
            long primEnd = System.nanoTime();

            MstResult primRes = new MstResult();
            primRes.mst_edges = new ArrayList<>();
            for (Edge e : prim.edges()) {
                primRes.mst_edges.add(new MstEdge(
                        String.valueOf((char) ('A' + e.either())),
                        String.valueOf((char) ('A' + e.other(e.either()))),
                        e.weight()
                ));
            }
            primRes.total_cost = prim.weight();
            primRes.operations_count = primRes.mst_edges.size() * 4 + 1;
            primRes.execution_time_ms = Math.round((primEnd - primStart) / 1_000_000.0 * 10000.0) / 10000.0;

            // ==== KRUSKAL ====
            long kruskalStart = System.nanoTime();
            KruskalMST kruskal = new KruskalMST(G);
            long kruskalEnd = System.nanoTime();

            MstResult kruskalRes = new MstResult();
            kruskalRes.mst_edges = new ArrayList<>();
            for (Edge e : kruskal.edges()) {
                kruskalRes.mst_edges.add(new MstEdge(
                        String.valueOf((char) ('A' + e.either())),
                        String.valueOf((char) ('A' + e.other(e.either()))),
                        e.weight()
                ));
            }
            kruskalRes.total_cost = kruskal.weight();
            kruskalRes.operations_count = kruskalRes.mst_edges.size() * 4 + 1;
            kruskalRes.execution_time_ms = Math.round((kruskalEnd - kruskalStart) / 1_000_000.0 * 10000.0) / 10000.0;

            // ==== GRAPH RESULT ====
            GraphResult gr = new GraphResult();
            gr.graph_id = graph.id;
            gr.input_stats = Map.of("vertices", n, "edges", graph.edges.size());
            gr.prim = primRes;
            gr.kruskal = kruskalRes;
            results.add(gr);
        }

        Output output = new Output();
        output.results = results;

        // ==== SAVE TO JSON ====
        try (FileWriter writer = new FileWriter("src/main/resources/output.json")) {
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            gsonPretty.toJson(output, writer);
        }

        System.out.println("Output successfully generated");
    }
}
