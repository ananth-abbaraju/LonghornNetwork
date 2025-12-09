# LonghornNetwork React Extra Credit

## Environment

- **OS**: macOS Sequoia 15.2
- **Java**: JDK 11+ (check with `java -version`)
- **Node.js**: 16+ (check with `node -v`)
- **npm**: 8+ (check with `npm -v`)

## Libraries Used

### Backend (Java)
- `com.sun.net.httpserver` - Java HTTP server 

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
