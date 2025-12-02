import java.io.*;
import java.util.*;

/**
 * Utility class responsible for parsing student data from external files.
 * Converts text-based input into UniversityStudent objects.
 */
public class DataParser {
    /**
     * Parses a given input file and creates a list of UniversityStudent objects.
     *
     * @param filename The path to the input file containing student data.
     * @return A List of UniversityStudent objects parsed from the file.
     * @throws IOException If the file cannot be read or found.
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        String name = null, gender = null, major = null;

        int age = 0;
        int year = 0;
        double gpa = 0.0;

        List<String> roommatePreferences = new ArrayList<>();
        List<String> previousInternships = new ArrayList<>();

        boolean inStudent = false;

        line = reader.readLine();
        while (line != null) {
            line = line.trim();

            if (line.isEmpty()) continue;

            if (line.equals("Student:")) {
                if (inStudent && name != null) {
                    students.add(new UniversityStudent(name, age, gender, year, major, gpa, roommatePreferences, previousInternships));
                }
                name = null; 
                gender = null; 
                major = null; 
                age = 0; 
                year = 0; 
                gpa = 0.0;
                
                roommatePreferences = new ArrayList<>();
                previousInternships = new ArrayList<>();
                inStudent = true;
                
                continue;
            }

            if (!inStudent) continue;
            
            if (!line.contains(":")) {
                System.out.println("Parsing error: Incorrect format in line: '" + line + "'. Expected format 'Name: <value>'.");
                reader.close();

                return students;
            }
            
            String[] parsed = line.split(":", 2);
            
            String key = parsed[0].trim();
            String value = "";
            if (parsed.length > 1) value = parsed[1].trim();
            
            try {
                switch (key) {
                    case "Name":
                        name = value;
                        break;
                    case "Age":
                        age = Integer.parseInt(value);
                        break;
                    case "Gender":
                        gender = value;
                        break;
                    case "Year":
                        year = Integer.parseInt(value);
                        break;
                    case "Major":
                        major = value;
                        break;
                    case "GPA":
                        gpa = Double.parseDouble(value);
                        break;
                    case "RoommatePreferences":
                        if (!value.isEmpty() && !value.equalsIgnoreCase("None")) {
                            roommatePreferences = new ArrayList<>(Arrays.asList(value.split(",\\s*")));
                        }
                        break;
                    case "PreviousInternships":
                        if (!value.isEmpty() && !value.equalsIgnoreCase("None")) {
                            previousInternships = new ArrayList<>(Arrays.asList(value.split(",\\s*")));
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Number format error: Invalid number format for " + key.toLowerCase() + ": '" + value + "' in student entry for " + name + ".");
                reader.close();
                return students;
            }

            line = reader.readLine();
        }

        if (inStudent && name != null) {
            students.add(new UniversityStudent(name, age, gender, year, major, gpa, roommatePreferences, previousInternships));
        }
        reader.close();
        return students;
    }
}
