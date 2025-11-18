import java.util.*;

/**
 * Represents the network of students as a weighted graph.
 * Supports adding students, creating edges based on connection strength,
 * and traversing the graph.
 */
public class StudentGraph {

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
        
    }

 

    /**
     * Retrieves the list of edges (neighbors) for a specific student.
     *
     * @param student The student whose neighbors are being requested.
     * @return A list of Edge objects connecting the student to others.
     */
    public List<Edge> getNeighbors(UniversityStudent student) {
        return new ArrayList<>();
    }

    /**
     * Returns all students (nodes) in the graph.
     *
     * @return A list of all UniversityStudent objects in the graph.
     */
    public List<UniversityStudent> getAllNodes() {
        return new ArrayList<>();
    }

    /**
     * Prints a visual representation of the graph to the console.
     * Useful for debugging and verification.
     */
    public void displayGraph() {

    }

}