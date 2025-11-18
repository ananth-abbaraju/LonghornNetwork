import java.util.*;

/**
 * Abstract base class representing a generic student.
 * Defines core attributes and the contract for calculating connection strengths.
 */
public abstract class Student {
    protected String name;
    protected int age;
    protected String gender;
    protected int year;
    protected String major;
    protected double gpa;
    protected List<String> roommatePreferences;
    protected List<String> previousInternships;

    /**
     * Calculates the connection strength (weight) between this student and another.
     *
     * @param other The other student to calculate the connection strength with.
     * @return An integer representing the weight of the connection.
     */
    public abstract int calculateConnectionStrength(Student other);
}
