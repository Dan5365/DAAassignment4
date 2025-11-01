package model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class for parsing JSON graph input files.
 */
public class GraphData {
    @SerializedName("directed")
    private boolean directed;
    
    @SerializedName("n")
    private int n;
    
    @SerializedName("edges")
    private List<EdgeData> edges;
    
    @SerializedName("source")
    private Integer source;
    
    @SerializedName("weight_model")
    private String weightModel;
    
    public static class EdgeData {
        @SerializedName("u")
        private int u;
        
        @SerializedName("v")
        private int v;
        
        @SerializedName("w")
        private double w;
        
        public EdgeData() {}
        
        public EdgeData(int u, int v, double w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
        
        public int getU() { return u; }
        public int getV() { return v; }
        public double getW() { return w; }
        
        public void setU(int u) { this.u = u; }
        public void setV(int v) { this.v = v; }
        public void setW(double w) { this.w = w; }
    }
    
    public boolean isDirected() {
        return directed;
    }
    
    public int getN() {
        return n;
    }
    
    public List<EdgeData> getEdges() {
        return edges != null ? edges : new ArrayList<>();
    }
    
    public Integer getSource() {
        return source;
    }
    
    public String getWeightModel() {
        return weightModel != null ? weightModel : "edge";
    }
    
    public void setDirected(boolean directed) { this.directed = directed; }
    public void setN(int n) { this.n = n; }
    public void setEdges(List<EdgeData> edges) { this.edges = edges; }
    public void setSource(Integer source) { this.source = source; }
    public void setWeightModel(String weightModel) { this.weightModel = weightModel; }
    
    /**
     * Loads graph data from a JSON file.
     * @param filePath path to the JSON file
     * @return GraphData object parsed from the file
     * @throws IOException if file cannot be read
     */
    public static GraphData loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GraphData.class);
        }
    }
}

