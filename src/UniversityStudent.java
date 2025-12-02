import java.util.*;

/**
 * Concrete implementation of the Student class representing a University Student.
 * Includes specific logic for calculating connection strengths based on university criteria.
 */
public class UniversityStudent extends Student {
    private UniversityStudent roommate;
    private List<String> friends;
    private List<String> chatHistory;

    /**
     * Constructs a new UniversityStudent with the specified attributes.
     *
     * @param name                The student's name.
     * @param age                 The student's age.
     * @param gender              The student's gender.
     * @param year                The student's year in school (1=Freshman, etc).
     * @param major               The student's major.
     * @param gpa                 The student's GPA.
     * @param roommatePreferences A list of names of preferred roommates.
     * @param internships         A list of companies where the student has interned.
     */
    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa, List<String> roommatePreferences, List<String> internships) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = new ArrayList<>(roommatePreferences);
        if (internships != null) {
            this.previousInternships = new ArrayList<>(internships);
        } else this.previousInternships = new ArrayList<>();

        this.roommate = null;
        this.friends = new ArrayList<>();
        this.chatHistory = new ArrayList<>();
    }

    /**
     * Calculates the connection strength between this student and another based on:
     * <ul>
     * <li>Roommate: +4</li>
     * <li>Shared Internship: +3 per internship</li>
     * <li>Same Major: +2</li>
     * <li>Same Age: +1</li>
     * </ul>
     *
     * @param other The student to compare against.
     * @return The total connection strength score.
     */
    @Override
    public int calculateConnectionStrength(Student other) {
        int strength = 0;

        if ((other instanceof UniversityStudent)) {
            UniversityStudent o = (UniversityStudent) other;

            if (this.roommate != null && this.roommate.equals(o)) strength += 4;
            
            for (String internship : this.previousInternships) {
                if (o.previousInternships.contains(internship)) {
                    strength += 3;
                }
            }

            if (this.major != null && this.major.equals(o.major)) strength += 2;
            if (this.age == o.age) strength += 1;
            
        }
        
        return strength;
    }

    public String getName() { 
        return name; 
    }
    public int getAge() { 
        return age; 
    }
    public String getGender() { 
        return gender; 
    }
    public int getYear() { 
        return year; 
    }
    public String getMajor() { 
        return major; 
    }
    public double getGpa() { 
        return gpa; 
    }
    
    public List<String> getRoommatePreferences() { 
        return roommatePreferences; 
    }
    public List<String> getPreviousInternships() { 
        return previousInternships; 
    }

    /**
     * Returns the currently assigned roommate.
     * @return The UniversityStudent object representing the roommate, or null if none.
     */
    public UniversityStudent getRoommate() { return roommate; }
    public void setRoommate(UniversityStudent roommate) { this.roommate = roommate; }

    public List<String> getFriends() { return friends; }
    public void addFriend(String friendName) { if (!friends.contains(friendName)) friends.add(friendName); }

    public List<String> getChatHistory() { return chatHistory; }
    public void addChatMessage(String message) { chatHistory.add(message); }

    /**
     * Returns a string representation of the student.
     * @return A string containing the student's name and attributes.
     */
    @Override
    public String toString() {
        return "UniversityStudent{name='" + name + "', age=" + age + ", gender='" + gender + "', year=" + year + ", major='" + major + "', GPA=" + gpa + ", roommatePreferences=" + roommatePreferences + ", previousInternships=" + previousInternships + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UniversityStudent other = (UniversityStudent) obj;
        return name != null && name.equals(other.name);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(name); 
    }
}

