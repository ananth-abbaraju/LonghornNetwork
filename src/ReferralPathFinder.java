import java.util.*;

/**
 * Implements Dijkstra's algorithm to find referral paths between students.
 * Used to find the "shortest path" (strongest connection chain) to a specific internship.
 */
public class ReferralPathFinder {
    private StudentGraph graph;

    /**
     * Constructs a ReferralPathFinder for a given graph.
     *
     * @param graph The StudentGraph to search through.
     */
    public ReferralPathFinder(StudentGraph graph) {
        this.graph = graph;
    }

    /**
     * Finds the optimal referral path from a starting student to any student who has
     * interned at the target company.
     *
     * @param start         The student initiating the search.
     * @param targetCompany The name of the company to find a referral for.
     * @return A list of UniversityStudent objects representing the path, or an empty list if no path exists.
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        if (start == null) return new ArrayList<>();

        if (start.getPreviousInternships().contains(targetCompany)) {
            List<UniversityStudent> path = new ArrayList<>();
            path.add(start);

            return path;
        }
        
        Map<UniversityStudent, Integer> dist = new HashMap<>();
        Map<UniversityStudent, UniversityStudent> prev = new HashMap<>();

        Comparator<UniversityStudent> comp = new Comparator<UniversityStudent>() {
            @Override
            public int compare(UniversityStudent a, UniversityStudent b) {
                return Integer.compare(dist.getOrDefault(a, Integer.MAX_VALUE), dist.getOrDefault(b, Integer.MAX_VALUE));
            }
        };
        PriorityQueue<UniversityStudent> minHeap = new PriorityQueue<>(comp);
        
        for (UniversityStudent s : graph.getAllNodes()) {
            dist.put(s, Integer.MAX_VALUE);
            prev.put(s, null);
        }
        
        dist.put(start, 0);
        minHeap.add(start);
        
        UniversityStudent target = null;
        
        while (!minHeap.isEmpty()) {
            UniversityStudent current = minHeap.poll();
            
            if (current.getPreviousInternships().contains(targetCompany) && !current.equals(start)) {
                target = current;
                break;
            }
            
            int currentDist = dist.get(current);
            if (currentDist == Integer.MAX_VALUE) continue;
            
            for (StudentGraph.Edge edge : graph.getNeighbors(current)) {
                int invertedWeight = 10 - edge.weight;
                if (invertedWeight < 1) invertedWeight = 1;

                int newDist = currentDist + invertedWeight;
                if (newDist < dist.get(edge.neighbor)) {
                    dist.put(edge.neighbor, newDist);
                    prev.put(edge.neighbor, current);

                    minHeap.add(edge.neighbor);
                }
            }
        }
        
        if (target == null) return new ArrayList<>();
        
        List<UniversityStudent> path = new ArrayList<>();
        UniversityStudent current = target;

        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
        }
        
        return path;
    }
}
