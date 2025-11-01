# Smart City / Smart Campus Scheduling - Assignment 4

## Overview

This project implements graph algorithms for processing city-service task dependencies:
1. **Strongly Connected Components (SCC)** using Tarjan's algorithm
2. **Topological Sorting** using Kahn's algorithm
3. **Shortest and Longest Paths in DAGs** using dynamic programming over topological order

## Project Structure

```
PrimMST/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/          # Graph data models
│   │   │   ├── graph/
│   │   │   │   ├── scc/        # SCC algorithms
│   │   │   │   ├── topo/       # Topological sort
│   │   │   │   └── dagsp/      # DAG shortest/longest paths
│   │   │   ├── util/           # Metrics and utilities
│   │   │   └── org/example/    # Main class
│   │   └── resources/
│   └── test/
│       └── java/               # JUnit tests
├── data/                        # Test datasets
├── pom.xml                      # Maven configuration
└── README.md
```

## Requirements

- Java 21 or higher
- Maven 3.6+

## Building and Running

### Build the project:
```bash
mvn clean compile
```

### Run the main program:
```bash
mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="data/tasks.json"
```

Or compile and run directly:
```bash
mvn compile
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) org.example.Main data/tasks.json
```

### Run tests:
```bash
mvn test
```

### Generate datasets:
```bash
# Option 1: Using Maven
mvn compile exec:java -Dexec.mainClass="util.GraphGenerator"

# Option 2: After compilation
java -cp target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout) util.GraphGenerator

# Note: All 9 required datasets are already generated in the /data/ directory
```

## Dataset Information

The `/data/` directory contains 9 datasets:

### Small Datasets (6-10 nodes):
- `small_cyclic.json` - 8 nodes, cyclic structure
- `small_dag.json` - 10 nodes, pure DAG
- `small_multiple_scc.json` - 7 nodes, multiple SCCs

### Medium Datasets (10-20 nodes):
- `medium_cyclic.json` - 15 nodes, cyclic structure
- `medium_dag.json` - 18 nodes, pure DAG
- `medium_multiple_scc.json` - 16 nodes, multiple SCCs

### Large Datasets (20-50 nodes):
- `large_cyclic.json` - 30 nodes, cyclic structure
- `large_dag.json` - 35 nodes, pure DAG
- `large_multiple_scc.json` - 28 nodes, multiple SCCs

### Weight Model

All datasets use **edge weights** (not node durations). Edge weights represent the time or cost associated with task dependencies.

## Algorithm Implementations

### 1. Strongly Connected Components (Tarjan's Algorithm)

**Package**: `graph.scc.TarjanSCC`

**Time Complexity**: O(V + E)  
**Space Complexity**: O(V)

**Key Features**:
- Detects all SCCs in a directed graph
- Returns mapping from vertices to their SCC index
- Tracks DFS visits and edge traversals for metrics

**Usage**:
```java
TarjanSCC tarjan = new TarjanSCC(graph);
List<List<Integer>> sccs = tarjan.findSCCs();
int[] vertexToSCC = tarjan.getVertexToSCCMapping();
```

### 2. Condensation Graph

**Package**: `graph.scc.CondensationGraph`

**Purpose**: Builds a DAG by contracting each SCC into a single node.

**Key Features**:
- Creates edges between components based on original graph edges
- Ensures the result is a DAG (no cycles)

### 3. Topological Sort (Kahn's Algorithm)

**Package**: `graph.topo.TopologicalSort`

**Time Complexity**: O(V + E)  
**Space Complexity**: O(V)

**Key Features**:
- Kahn's algorithm (BFS-based with in-degree tracking)
- Also provides DFS-based variant
- Detects cycles (returns null if cycle found)
- Tracks queue operations for metrics

**Usage**:
```java
TopologicalSort topo = new TopologicalSort(dag);
List<Integer> order = topo.kahnTopologicalSort();
```

### 4. DAG Shortest Paths

**Package**: `graph.dagsp.DAGShortestPath`

**Time Complexity**: O(V + E)  
**Space Complexity**: O(V)

**Algorithm**: Single-source shortest paths using dynamic programming over topological order.

**Key Features**:
- Computes shortest distances from source to all vertices
- Path reconstruction
- Tracks edge relaxations for metrics

**Usage**:
```java
DAGShortestPath sp = new DAGShortestPath(dag, source);
double[] distances = sp.getDistances();
List<Integer> path = sp.getPath(target);
```

### 5. DAG Longest Path (Critical Path)

**Package**: `graph.dagsp.DAGLongestPath`

**Time Complexity**: O(V + E)  
**Space Complexity**: O(V)

**Algorithm**: Longest path computation using DP with maximization instead of minimization.

**Key Features**:
- Finds critical path (longest path) in the DAG
- Path reconstruction
- Identifies source and target of critical path

**Usage**:
```java
DAGLongestPath lp = new DAGLongestPath(dag);
double length = lp.getLongestPathLength();
List<Integer> criticalPath = lp.getCriticalPath();
```

## Metrics and Instrumentation

All algorithms implement the `Metrics` interface through `AlgorithmMetrics`:

- **Timing**: Using `System.nanoTime()` for high-precision measurement
- **Operation Counters**:
  - DFS visits (for SCC and DFS-based topological sort)
  - Edge traversals
  - Queue operations (for Kahn's algorithm)
  - Edge relaxations (for shortest/longest paths)

**Example**:
```java
AlgorithmMetrics metrics = tarjan.getMetrics();
System.out.println("Time: " + metrics.getTimeMillis() + " ms");
System.out.println("DFS Visits: " + metrics.getDfsVisits());
```

## Input Format

JSON files follow this structure:

```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3.0},
    {"u": 1, "v": 2, "w": 2.0}
  ],
  "source": 0,
  "weight_model": "edge"
}
```

- `directed`: always `true` for directed graphs
- `n`: number of vertices (0-indexed)
- `edges`: list of directed edges with weights
- `source`: source vertex for shortest path computation (optional)
- `weight_model`: `"edge"` for edge weights

## Output

The main program outputs:

1. **Graph Information**: vertices, edges, weight model
2. **SCC Results**: list of SCCs with their sizes
3. **Condensation Graph**: size of the condensed DAG
4. **Topological Order**: order of components and derived task order
5. **Shortest Paths**: distances from source to all components
6. **Critical Path**: longest path length and path reconstruction
7. **Metrics**: timing and operation counts for each algorithm

## Testing

JUnit tests cover:
- **SCC**: simple cycles, multiple SCCs, DAGs, edge cases
- **Topological Sort**: simple DAGs, multiple sources, cycle detection
- **Shortest Paths**: simple paths, multiple paths, unreachable vertices
- **Longest Paths**: various path configurations
- **Condensation**: conversion from cyclic graphs to DAGs

Run tests:
```bash
mvn test
```

## Performance Analysis

### SCC Algorithm
- **Bottleneck**: DFS traversal depth in graphs with large cycles
- **Effect of Density**: Dense graphs with many cycles have more SCCs, but algorithm remains O(V+E)
- **Effect of Structure**: Pure DAGs result in V separate SCCs (one per vertex)

### Topological Sort
- **Bottleneck**: Queue operations in graphs with many source vertices
- **Effect of Density**: More edges lead to more queue operations, but still O(V+E)
- **Effect of Structure**: Long chains have fewer queue operations than branching structures

### DAG Shortest/Longest Paths
- **Bottleneck**: Edge relaxations in dense DAGs
- **Effect of Density**: More edges = more relaxations, but algorithm is optimal O(V+E)
- **Effect of Structure**: Linear chains are faster than highly branched graphs

## When to Use Each Method

1. **Tarjan's SCC**: Best for detecting cycles and compressing cyclic components. Use when you need to identify strongly connected groups in a dependency graph.

2. **Kahn's Topological Sort**: Best for DAGs when you need a valid execution order. Efficient for graphs with many source nodes. Use for task scheduling.

3. **DFS Topological Sort**: Alternative when you need to detect cycles during sorting. Slightly more memory-intensive but provides cycle detection.

4. **DAG Shortest Path**: Optimal for single-source shortest paths in DAGs. Faster than Dijkstra (O(V+E) vs O(E log V)) because topological order is known. Use for finding minimum cost paths.

5. **DAG Longest Path**: Essential for critical path analysis. Use for project scheduling and identifying bottleneck tasks.

## Practical Recommendations

1. **For Task Scheduling**: 
   - Use SCC to detect circular dependencies (must be resolved manually)
   - Use condensation to get a DAG
   - Use topological sort to determine execution order
   - Use longest path to identify critical path and estimate total time

2. **For Performance**:
   - Prefer Kahn's algorithm for DAGs with many sources
   - Use DFS-based topological sort when cycle detection is needed
   - Cache topological order if computing multiple shortest/longest paths

3. **For Large Graphs**:
   - Condensation reduces graph size significantly for graphs with many cycles
   - Consider parallel processing for independent SCCs

## Code Quality

- **Packages**: Clear separation (`graph.scc`, `graph.topo`, `graph.dagsp`)
- **Documentation**: Javadoc comments for all public classes and methods
- **Testing**: Comprehensive JUnit tests with edge cases
- **Metrics**: Instrumentation interface for performance analysis
- **Error Handling**: Graceful handling of invalid inputs and edge cases

## Summary Report

A detailed analysis and results report is available in `SUMMARY_REPORT.md`, including:
- Dataset analysis and summaries
- Performance metrics and timing results
- Bottleneck analysis for each algorithm
- Practical recommendations and use cases
- Complete test coverage information

## License

This project is created for educational purposes as part of Assignment 4.


