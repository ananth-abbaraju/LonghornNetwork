import java.util.*;


/**
 * Implements the Gale-Shapley algorithm to solve the Stable Matching Problem
 * for roommate assignments.
 */
public class GaleShapley {

    /**
     * Assigns roommates to students based on their preference lists.
     * Ensures a stable matching where no two students prefer each other over their
     * current assignments.
     *
     * @param students A list of UniversityStudent objects to be matched.
     * Students are matched and their 'roommate' field is updated.
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        Map<String, UniversityStudent> studentMap = new HashMap<>();
        for (UniversityStudent s : students) {
            studentMap.put(s.getName(), s);
        }
        
        Queue<UniversityStudent> free = new LinkedList<>();
        Map<UniversityStudent, Integer> nextProp = new HashMap<>();
        
        for (UniversityStudent s : students) {
            if (!s.getRoommatePreferences().isEmpty()) {
                free.add(s);
                nextProp.put(s, 0);
            }
        }
        
        while (!free.isEmpty()) {
            UniversityStudent proposer = free.poll();
            if (proposer.getRoommate() != null) continue;
            
            List<String> prefs = proposer.getRoommatePreferences();
            int i = nextProp.get(proposer);
            
            if (i >= prefs.size()) continue;
            
            String prefName = prefs.get(i);
            nextProp.put(proposer, i + 1);
            
            UniversityStudent pref = studentMap.get(prefName);
            if (pref == null) {
                free.add(proposer);
                continue;
            }
            
            if (pref.getRoommate() == null) {
                proposer.setRoommate(pref);
                pref.setRoommate(proposer);
            } else {
                UniversityStudent current = pref.getRoommate();
                
                if (prefersOver(pref, proposer, current)) {
                    current.setRoommate(null);
                    free.add(current);

                    proposer.setRoommate(pref);
                    pref.setRoommate(proposer);
                } else {
                    free.add(proposer);
                }
            }
        }
    }
    
    // Returns true if student prefers candidate over current partner.
    private static boolean prefersOver(UniversityStudent student, UniversityStudent candidate, UniversityStudent current) {
        List<String> prefs = student.getRoommatePreferences();
        int candidateRank = prefs.indexOf(candidate.getName());
        int currentRank = prefs.indexOf(current.getName());

        if (candidateRank==-1) return false;
        if (currentRank==-1) return true;

        return candidateRank < currentRank;
    }
}
