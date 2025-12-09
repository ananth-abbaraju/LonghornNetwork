import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * REST API Server for the LonghornNetwork React UI.
 */
public class ApiServer {
    private static List<List<UniversityStudent>> testCases = new ArrayList<>();
    private static int currentTestCase = 0;
    private static List<UniversityStudent> students = new ArrayList<>();
    private static StudentGraph graph = null;

    public static void main(String[] args) throws IOException {
        testCases.add(generateTestCase1());
        testCases.add(generateTestCase2());
        testCases.add(generateTestCase3());
        loadTestCase(0);

        // HTTP server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // API endpoints
        server.createContext("/api/testcases", new TestCasesHandler());
        server.createContext("/api/load", new LoadTestCaseHandler());
        server.createContext("/api/students", new StudentsHandler());
        server.createContext("/api/graph", new GraphHandler());
        server.createContext("/api/roommates", new RoommatesHandler());
        server.createContext("/api/referral", new ReferralHandler());
        server.createContext("/api/student", new StudentDetailHandler());
        
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("API Server started on http://localhost:8080");
        System.out.println("Endpoints:");
        System.out.println("  GET  /api/testcases       - List available test cases");
        System.out.println("  POST /api/load?id=1       - Load test case (1, 2, or 3)");
        System.out.println("  GET  /api/students        - Get all students");
        System.out.println("  GET  /api/graph           - Get graph data for visualization");
        System.out.println("  POST /api/roommates       - Run GaleShapley and get assignments");
        System.out.println("  GET  /api/referral?start=Name&company=X - Find referral path");
        System.out.println("  GET  /api/student?name=X  - Get student details (friends, chat)");
    }

    // loading test case, rebuild grpah
    private static void loadTestCase(int index) {
        if (index < 0 || index >= testCases.size()) return;
        currentTestCase = index;
        students = new ArrayList<>();
        for (UniversityStudent s : testCases.get(index)) {
            students.add(new UniversityStudent( s.getName(), s.getAge(), s.getGender(), s.getYear(), s.getMajor(), s.getGpa(), s.getRoommatePreferences(), s.getPreviousInternships()));
        }
        graph = new StudentGraph(students);
        
        // assigning roommates - gale shapley
        GaleShapley.assignRoommates(students);
        
        // friend requests and chats using threads
        try {
            ExecutorService executor = Executors.newFixedThreadPool(4);
            if (students.size() >= 2) {
                executor.submit(new FriendRequestThread(students.get(0), students.get(1)));
                executor.submit(new ChatThread(students.get(0), students.get(1), "Hello!"));
                if (students.size() >= 3) {
                    executor.submit(new FriendRequestThread(students.get(1), students.get(2)));
                    executor.submit(new ChatThread(students.get(1), students.get(2), "Hi there!"));
                }
            }
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // rebuilding graph after assignments
        graph = new StudentGraph(students);
    }

    // Helper to add CORS headers
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    // Helper to send JSON response
    private static void sendResponse(HttpExchange exchange, int code, String json) throws IOException {
        addCorsHeaders(exchange);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // Helper to escape strings for JSON
    private static String esc(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // GET /api/testcases - all the test cases for the dropdown
    static class TestCasesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder json = new StringBuilder();
            json.append("{\"current\":").append(currentTestCase + 1);
            json.append(",\"testCases\":[");
            json.append("{\"id\":1,\"name\":\"Test Case 1\",\"description\":\"6 students, 2 groups\"},");
            json.append("{\"id\":2,\"name\":\"Test Case 2\",\"description\":\"3 students, DummyCompany referral\"},");
            json.append("{\"id\":3,\"name\":\"Test Case 3\",\"description\":\"3 students, one unpaired\"}");
            json.append("]}");
            sendResponse(exchange, 200, json.toString());
        }
    }

    // POST /api/load - loading a test case
    static class LoadTestCaseHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            int id = 1;
            if (query != null && query.startsWith("id=")) {
                try { id = Integer.parseInt(query.substring(3)); } catch (Exception e) {}
            }
            loadTestCase(id - 1);
            sendResponse(exchange, 200, "{\"success\":true,\"loaded\":" + id + "}");
        }
    }

    // GET /api/students  - for all students, not just one 
    static class StudentsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder json = new StringBuilder();
            json.append("{\"students\":[");
            for (int i = 0; i < students.size(); i++) {
                UniversityStudent s = students.get(i);
                if (i > 0) json.append(",");
                json.append(studentToJson(s));
            }
            json.append("]}");
            sendResponse(exchange, 200, json.toString());
        }
    }

    // GET /api/graph 
    static class GraphHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder json = new StringBuilder();
            json.append("{\"nodes\":[");
            List<UniversityStudent> nodes = graph.getAllNodes();
            for (int i = 0; i < nodes.size(); i++) {
                UniversityStudent s = nodes.get(i);
                if (i > 0) json.append(",");
                json.append("{\"id\":\"").append(esc(s.getName())).append("\"");
                json.append(",\"label\":\"").append(esc(s.getName())).append("\"");
                json.append(",\"major\":\"").append(esc(s.getMajor())).append("\"");
                json.append(",\"age\":").append(s.getAge());
                json.append(",\"year\":").append(s.getYear());
                json.append(",\"gpa\":").append(s.getGpa());
                json.append("}");
            }
            json.append("],\"edges\":[");
            Set<String> addedEdges = new HashSet<>();
            boolean first = true;
            for (UniversityStudent s : nodes) {
                for (StudentGraph.Edge edge : graph.getNeighbors(s)) {
                    String key1 = s.getName() + "-" + edge.neighbor.getName();
                    String key2 = edge.neighbor.getName() + "-" + s.getName();
                    if (!addedEdges.contains(key1) && !addedEdges.contains(key2)) {
                        if (!first) json.append(",");
                        first = false;
                        json.append("{\"from\":\"").append(esc(s.getName())).append("\"");
                        json.append(",\"to\":\"").append(esc(edge.neighbor.getName())).append("\"");
                        json.append(",\"weight\":").append(edge.weight);
                        json.append(",\"label\":\"").append(edge.weight).append("\"");
                        json.append("}");
                        addedEdges.add(key1);
                    }
                }
            }
            json.append("]}");
            sendResponse(exchange, 200, json.toString());
        }
    }

    // POST /api/roommates
    static class RoommatesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            StringBuilder json = new StringBuilder();
            json.append("{\"roommates\":[");
            boolean first = true;
            for (UniversityStudent s : students) {
                if (!first) json.append(",");
                first = false;
                json.append("{\"student\":\"").append(esc(s.getName())).append("\"");
                if (s.getRoommate() != null) {
                    json.append(",\"roommate\":\"").append(esc(s.getRoommate().getName())).append("\"");
                } else {
                    json.append(",\"roommate\":null");
                }
                json.append("}");
            }
            json.append("]}");
            sendResponse(exchange, 200, json.toString());
        }
    }

    // GET /api/referral?start=Name&company=X - Find referral path
    static class ReferralHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String startName = null;
            String company = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2) {
                        if ("start".equals(kv[0])) startName = java.net.URLDecoder.decode(kv[1], "UTF-8");
                        if ("company".equals(kv[0])) company = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    }
                }
            }
            
            StringBuilder json = new StringBuilder();
            if (startName == null || company == null) {
                json.append("{\"error\":\"Missing start or company parameter\",\"path\":[]}");
            } else {
                UniversityStudent start = graph.getStudent(startName);
                if (start == null) {
                    json.append("{\"error\":\"Student not found\",\"path\":[]}");
                } else {
                    ReferralPathFinder finder = new ReferralPathFinder(graph);
                    List<UniversityStudent> path = finder.findReferralPath(start, company);
                    json.append("{\"start\":\"").append(esc(startName)).append("\"");
                    json.append(",\"company\":\"").append(esc(company)).append("\"");
                    json.append(",\"found\":").append(!path.isEmpty());
                    json.append(",\"path\":[");
                    for (int i = 0; i < path.size(); i++) {
                        if (i > 0) json.append(",");
                        json.append("\"").append(esc(path.get(i).getName())).append("\"");
                    }
                    json.append("]}");
                }
            }
            sendResponse(exchange, 200, json.toString());
        }
    }

    // GET /api/student 
    static class StudentDetailHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String name = null;
            if (query != null && query.startsWith("name=")) {
                name = java.net.URLDecoder.decode(query.substring(5), "UTF-8");
            }
            
            if (name == null) {
                sendResponse(exchange, 400, "{\"error\":\"Missing name parameter\"}");
                return;
            }
            
            UniversityStudent s = graph.getStudent(name);
            if (s == null) {
                sendResponse(exchange, 404, "{\"error\":\"Student not found\"}");
                return;
            }
            
            sendResponse(exchange, 200, studentToJson(s));
        }
    }

    // Convert student to JSON
    private static String studentToJson(UniversityStudent s) {
        StringBuilder json = new StringBuilder();
        json.append("{\"name\":\"").append(esc(s.getName())).append("\"");
        json.append(",\"age\":").append(s.getAge());
        json.append(",\"gender\":\"").append(esc(s.getGender())).append("\"");
        json.append(",\"year\":").append(s.getYear());
        json.append(",\"major\":\"").append(esc(s.getMajor())).append("\"");
        json.append(",\"gpa\":").append(s.getGpa());
        
        json.append(",\"roommatePreferences\":[");
        List<String> prefs = s.getRoommatePreferences();
        for (int i = 0; i < prefs.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(esc(prefs.get(i))).append("\"");
        }
        json.append("]");
        
        json.append(",\"previousInternships\":[");
        List<String> interns = s.getPreviousInternships();
        for (int i = 0; i < interns.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(esc(interns.get(i))).append("\"");
        }
        json.append("]");
        
        if (s.getRoommate() != null) {
            json.append(",\"roommate\":\"").append(esc(s.getRoommate().getName())).append("\"");
        } else {
            json.append(",\"roommate\":null");
        }
        
        json.append(",\"friends\":[");
        List<String> friends = s.getFriends();
        if (friends.isEmpty()) {
            json.append("]");
        } else {
            for (int i = 0; i < friends.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(esc(friends.get(i))).append("\"");
            }
            json.append("]");
        }
        
        json.append(",\"chatHistory\":[");
        List<String> chats = s.getChatHistory();
        if (chats.isEmpty()) {
            json.append("]");
        } else {
            for (int i = 0; i < chats.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(esc(chats.get(i))).append("\"");
            }
            json.append("]");
        }
        
        json.append("}");
        return json.toString();
    }

    // Copied from main
    public static List<UniversityStudent> generateTestCase1() {
        List<UniversityStudent> students = new ArrayList<>();
        students.add(new UniversityStudent("Alice", 20, "Female", 2, "Computer Science", 3.5, Arrays.asList("Bob", "Charlie", "Frank"), Arrays.asList("Google")));
        students.add(new UniversityStudent("Bob", 21, "Male", 3, "Computer Science", 3.7, Arrays.asList("Alice", "Charlie", "Frank"), Arrays.asList("Google", "Microsoft")));
        students.add(new UniversityStudent("Charlie", 20, "Male", 2, "Mathematics", 3.2, Arrays.asList("Alice", "Bob", "Frank"), Arrays.asList("None")));
        students.add(new UniversityStudent("Frank", 23, "Male", 3, "Chemistry", 3.1, Arrays.asList("Alice", "Bob", "Charlie"), Arrays.asList()));
        students.add(new UniversityStudent("Dana", 22, "Female", 4, "Biology", 3.8, Arrays.asList("Evan"), Arrays.asList("Pfizer")));
        students.add(new UniversityStudent("Evan", 22, "Male", 4, "Biology", 3.6, Arrays.asList("Dana"), Arrays.asList("Moderna", "Pfizer")));
        return students;
    }

    public static List<UniversityStudent> generateTestCase2() {
        List<UniversityStudent> students = new ArrayList<>();
        students.add(new UniversityStudent("Greg", 24, "Male", 4, "Economics", 3.4, Arrays.asList("Helen", "Ivy"), Arrays.asList("InternshipA")));
        students.add(new UniversityStudent("Helen", 24, "Female", 4, "Economics", 3.5, Arrays.asList("Greg", "Ivy"), Arrays.asList("InternshipB")));
        students.add(new UniversityStudent("Ivy", 25, "Female", 4, "Economics", 3.8, Arrays.asList("Helen", "Greg"), Arrays.asList("DummyCompany")));
        return students;
    }

    public static List<UniversityStudent> generateTestCase3() {
        List<UniversityStudent> students = new ArrayList<>();
        students.add(new UniversityStudent("Jack", 19, "Male", 1, "History", 3.0, Arrays.asList("Kim"), Arrays.asList("MuseumIntern")));
        students.add(new UniversityStudent("Kim", 19, "Female", 1, "History", 3.2, Arrays.asList("Jack"), Arrays.asList("MuseumIntern")));
        students.add(new UniversityStudent("Leo", 20, "Male", 1, "History", 3.5, Collections.emptyList(), Arrays.asList("None")));
        return students;
    }
}
