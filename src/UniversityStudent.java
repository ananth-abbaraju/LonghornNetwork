import java.util.*;

/**
 * Concrete implementation of the Student class representing a University Student.
 * Includes specific logic for calculating connection strengths based on university criteria.
 */
public class UniversityStudent extends Student {
    // TODO: Constructor and additional methods to be implemented
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
        this.roommatePreferences = roommatePreferences;
        this.previousInternships = internships;
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
        // TODO: placeholder
        return 0;
    }

    /**
     * Returns the currently assigned roommate.
     * @return The UniversityStudent object representing the roommate, or null if none.
     */
    public UniversityStudent getRoommate() {
        return null; // TODO: placeholder
    }
    
    /**
     * Returns a string representation of the student.
     * @return A string containing the student's name and attributes.
     */
    @Override
    public String toString() {
        return this.name; 
    }
}

