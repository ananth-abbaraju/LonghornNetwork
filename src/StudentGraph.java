import java.util.*;

/**
 * Represents the network of students as a weighted graph.
 * Supports adding students, creating edges based on connection strength,
 * and traversing the graph.
 */
public class StudentGraph {
    private Map<UniversityStudent, List<Edge>> adjacencyList;
    private Map<String, UniversityStudent> studentMap;

    /**
     * Inner class representing a weighted edge between two students.
     */
    public static class Edge {
        public UniversityStudent neighbor;
        public int weight;

        public Edge(UniversityStudent neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }
    }

    /**
     * Constructs the graph from a list of students.
     * Automatically adds all students as nodes and calculates edges between them.
     *
     * @param students A list of UniversityStudent objects to populate the graph.
     */
    public StudentGraph(List<UniversityStudent> students) {
        adjacencyList = new HashMap<>();
        studentMap = new HashMap<>();

        for (UniversityStudent student : students) {
            adjacencyList.put(student, new ArrayList<>());
            studentMap.put(student.getName(), student);
        }

        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) {
                UniversityStudent s1 = students.get(i);
                UniversityStudent s2 = students.get(j);
                
                int strength = s1.calculateConnectionStrength(s2);
                if (strength > 0) {
                    addEdge(s1, s2, strength);
                }
            }
        }
    }

    // Adds a weighted edge between two students (undirected).
    public void addEdge(UniversityStudent s1, UniversityStudent s2, int weight) {
        adjacencyList.get(s1).add(new Edge(s2, weight));
        adjacencyList.get(s2).add(new Edge(s1, weight));
    }

    /**
     * Retrieves the list of edges (neighbors) for a specific student.
     *
     * @param student The student whose neighbors are being requested.
     * @return A list of Edge objects connecting the student to others.
     */
    public List<Edge> getNeighbors(UniversityStudent student) {
        return adjacencyList.getOrDefault(student, new ArrayList<>());
    }

    /**
     * Returns all students (nodes) in the graph.
     *
     * @return A list of all UniversityStudent objects in the graph.
     */
    public List<UniversityStudent> getAllNodes() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    // Returns a student by name.
    public UniversityStudent getStudent(String name) {
        return studentMap.get(name);
    }

    /**
     * Prints a visual representation of the graph to the console.
     * Useful for debugging and verification.
     */
    public void displayGraph() {
        System.out.println("Student Graph:");
        for (UniversityStudent student : adjacencyList.keySet()) {
            System.out.print(student.getName() + " -> [");
            List<Edge> edges = adjacencyList.get(student);
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                System.out.print("(" + edge.neighbor.getName() + ", " + edge.weight + ")");
                if (i < edges.size() - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }
}