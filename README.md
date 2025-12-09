# LonghornNetwork React Extra Credit


## Questions:
### a. Did you use AI to code the UI? If so, what were the sources that the AI used, what was the AI good at and what was it not so good at? What did you do to fill in the gaps. 
- Yes. The AI was good at whipping up the entire React configuration and getting a frontend up and running. It made that whole process a lot easier than having to manually do it. It also gave me a really nice framework for the UI, with different sections and their headers, basically like a skeleton. 
- It was not so good at the specific visual requirements that this lab needed though. I had to fill in the gaps for this part. However, the actual backend logic from steps 3 and 4 was easy to transfer into the frontend by making it into an API, so there weren’t any problems with actual logical errors.
- For the visuals, specifically, I had to modify the dropdowns to make it intuitive to select students and companies in the Referral Path finder.
- Also had to make the graph features more intuitive, having scrolling and interactive features, to account for cases where there are a lot more points.
- I had to modify roommate assignments display to be more intuitive as well, listing out the pairs, such that each person is mentioned once with their partner in equal sized text.
  
### b. If you did not use AI, what sources did you use to learn React, and what were the hardest things to learn? 
- As mentioned above, I had to make some changes and verify what AI coded for the UI. I used geeks for geeks a little, but also just discussed with AI to help me understand things about React. 

### c. We are planning to cover React next semester for this class, in what unit do you think this would be appropriate to teach?
- I think teaching it towards the end of the course makes sense, like right after Design Patterns. 
## Environment

- **OS**: macOS Sequoia 15.2+
- **Java**: JDK 11+ (check with `java -version`)
- **Node.js**: 16+ (check with `node -v`)
- **npm**: 8+ (check with `npm -v`)

## Libraries Used

### Backend (Java)
- `com.sun.net.httpserver` - Java HTTP server, part of JDK

### Frontend (React)
- All frontend dependencies are in `frontend/package.json` 
- Doing `npm install` in step 5 below will automatically install them.
- But specificaklly:
- `react` and `react-dom` - React framework
- `vis-network` and `vis-data` - library for graph visualization 



## Running

1. Open a terminal. Go to the project folder:
   ```bash
   cd /path/to/LonghornNetwork
   ```

2. Compile Java backend:
   ```bash
   cd src
   javac *.java
   ```

3. Start the API server. Starts on http://localhost:8080:
   ```bash
   java ApiServer
   ```

4. Open a second terminal. Go to to the frontend folder:
   ```bash
   cd /path/to/LonghornNetwork/frontend
   ```

5. Install dependencies:
   ```bash
   npm install
   ```

6. Start React frontend:
   ```bash
   npm start
   ```

7. It will open on a browser at http://localhost:3000
