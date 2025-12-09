import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Network } from 'vis-network';
import { DataSet } from 'vis-data';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [testCases, setTestCases] = useState([]);
  const [currentTestCase, setCurrentTestCase] = useState(1);
  const [students, setStudents] = useState([]);
  const [graphData, setGraphData] = useState({ nodes: [], edges: [] });
  const [roommates, setRoommates] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [referralStart, setReferralStart] = useState('');
  const [referralCompany, setReferralCompany] = useState('');
  const [referralPath, setReferralPath] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const graphRef = useRef(null);
  const networkRef = useRef(null);

  // API call to fetch data
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [testCasesRes, studentsRes, graphRes, roommatesRes] = await Promise.all([
        fetch(`${API_BASE}/testcases`),
        fetch(`${API_BASE}/students`),
        fetch(`${API_BASE}/graph`),
        fetch(`${API_BASE}/roommates`)
      ]);
      
      const testCasesData = await testCasesRes.json();
      const studentsData = await studentsRes.json();
      const graphDataRes = await graphRes.json();
      const roommatesData = await roommatesRes.json();
      
      setTestCases(testCasesData.testCases || []);
      setCurrentTestCase(testCasesData.current || 1);
      setStudents(studentsData.students || []);
      setGraphData(graphDataRes);
      setRoommates(roommatesData.roommates || []);
      
      if (studentsData.students?.length > 0) {
        setReferralStart(studentsData.students[0].name);
      }
    } catch (err) {
      setError('Failed to connect to API. Make sure the Java server is running on port 8080.');
      console.error(err);
    }
    setLoading(false);
  }, []);

  // Load data on mount
  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Initialize graph visualization
  useEffect(() => {
    if (!graphRef.current || !graphData.nodes?.length) return;

    const nodes = new DataSet(
      graphData.nodes.map(n => ({
        id: n.id,
        label: n.label,
        title: `${n.label}\nMajor: ${n.major}\nAge: ${n.age}\nYear: ${n.year}\nGPA: ${n.gpa}`,
        color: {
          background: '#bf5700',
          border: '#8b4000',
          highlight: { background: '#ff7722', border: '#bf5700' }
        },
        font: { color: 'white' }
      }))
    );

    const edges = new DataSet(
      graphData.edges.map((e, i) => ({
        id: i,
        from: e.from,
        to: e.to,
        label: String(e.weight),
        width: Math.max(1, e.weight / 2),
        color: { color: '#888', highlight: '#bf5700' },
        font: { size: 12, color: '#666', strokeWidth: 0 }
      }))
    );

    const options = {
      nodes: {
        shape: 'circle',
        size: 30,
        borderWidth: 2
      },
      edges: {
        smooth: { type: 'continuous' }
      },
      physics: {
        stabilization: { iterations: 100 },
        barnesHut: {
          gravitationalConstant: -3000,
          springLength: 150
        }
      },
      interaction: {
        hover: true,
        tooltipDelay: 200
      }
    };

    if (networkRef.current) {
      networkRef.current.destroy();
    }

    const network = new Network(graphRef.current, { nodes, edges }, options);
    networkRef.current = network;

    network.on('click', (params) => {
      if (params.nodes.length > 0) {
        const studentName = params.nodes[0];
        const student = students.find(s => s.name === studentName);
        if (student) {
          setSelectedStudent(student);
        }
      }
    });

    return () => {
      if (networkRef.current) {
        networkRef.current.destroy();
      }
    };
  }, [graphData, students]);

  // Highlight referral path on graph
  useEffect(() => {
    if (!networkRef.current || !referralPath?.path?.length) return;

    const pathNodes = referralPath.path;
    
    // Reset all nodes and edges
    const allNodes = graphData.nodes.map(n => ({
      id: n.id,
      color: pathNodes.includes(n.id) 
        ? { background: '#28a745', border: '#1e7e34' }
        : { background: '#bf5700', border: '#8b4000' }
    }));
    
    if (networkRef.current) {
      try {
        networkRef.current.body.data.nodes.update(allNodes);
      } catch (e) {
        // Ignore if network not ready
      }
    }
  }, [referralPath, graphData]);

  // Load a different test case
  const handleLoadTestCase = async (id) => {
    setLoading(true);
    try {
      await fetch(`${API_BASE}/load?id=${id}`, { method: 'POST' });
      await fetchData();
      setReferralPath(null);
      setSelectedStudent(null);
    } catch (err) {
      setError('Failed to load test case');
    }
    setLoading(false);
  };

  // Find referral path
  const handleFindReferral = async () => {
    if (!referralStart || !referralCompany) return;
    try {
      const res = await fetch(
        `${API_BASE}/referral?start=${encodeURIComponent(referralStart)}&company=${encodeURIComponent(referralCompany)}`
      );
      const data = await res.json();
      setReferralPath(data);
    } catch (err) {
      setError('Failed to find referral path');
    }
  };

  // Get unique companies from all students
  const getCompanies = () => {
    const companies = new Set();
    students.forEach(s => {
      s.previousInternships?.forEach(intern => {
        if (intern && intern !== 'None') {
          companies.add(intern);
        }
      });
    });
    return Array.from(companies);
  };

  if (loading && students.length === 0) {
    return <div className="loading">Loading LonghornNetwork...</div>;
  }

  return (
    <div className="app">
      <header className="header">
        <h1>🤘 LonghornNetwork</h1>
        <div className="test-case-selector">
          <span>Test Case:</span>
          <select 
            value={currentTestCase} 
            onChange={(e) => handleLoadTestCase(Number(e.target.value))}
          >
            {testCases.map(tc => (
              <option key={tc.id} value={tc.id}>
                {tc.name} - {tc.description}
              </option>
            ))}
          </select>
        </div>
      </header>

      {error && <div className="error">{error}</div>}

      <div className="main-content">
        <div className="left-panel">
          {/* Graph Visualization */}
          <div className="card">
            <div className="card-header">Student Network Graph</div>
            <div className="card-body">
              <div className="graph-container" ref={graphRef} />
            </div>
          </div>

          {/* Roommates */}
          <div className="card">
            <div className="card-header">Roommate Assignments (Gale-Shapley)</div>
            <div className="card-body">
              <div className="roommates-list">
                {roommates
                  .filter((r, i, arr) => {
                    // Only show each pair once (where student name < roommate name alphabetically)
                    if (!r.roommate) return true; // Show unpaired students
                    return r.student < r.roommate;
                  })
                  .map((r, i) => (
                  <div key={i} className="roommate-pair">
                    <span className="student">{r.student}</span>
                    <span className="roommate">
                      {r.roommate ? `↔ ${r.roommate}` : '(No roommate)'}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Referral Path Finder */}
          <div className="card">
            <div className="card-header">Referral Path Finder (Dijkstra's Algorithm)</div>
            <div className="card-body">
              <div className="referral-finder">
                <div className="controls">
                  <select 
                    value={referralStart} 
                    onChange={(e) => setReferralStart(e.target.value)}
                  >
                    {students.map(s => (
                      <option key={s.name} value={s.name}>{s.name}</option>
                    ))}
                  </select>
                  <select
                    value={referralCompany}
                    onChange={(e) => setReferralCompany(e.target.value)}
                  >
                    <option value="">Select a company...</option>
                    {getCompanies().map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                  <button onClick={handleFindReferral}>Find Path</button>
                </div>
                {referralPath && (
                  <div className="referral-path">
                    {referralPath.found ? (
                      referralPath.path.map((name, i) => (
                        <React.Fragment key={i}>
                          <span className="path-node">{name}</span>
                          {i < referralPath.path.length - 1 && (
                            <span className="path-arrow">→</span>
                          )}
                        </React.Fragment>
                      ))
                    ) : (
                      <span className="no-data">
                        No path found to someone at {referralPath.company}
                      </span>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="right-panel">
          {/* Student List */}
          <div className="card">
            <div className="card-header">Students ({students.length})</div>
            <div className="card-body student-list">
              {students.map(s => (
                <div 
                  key={s.name}
                  className={`student-item ${selectedStudent?.name === s.name ? 'selected' : ''}`}
                  onClick={() => setSelectedStudent(s)}
                >
                  <div className="name">{s.name}</div>
                  <div className="info">{s.major} • Year {s.year} • GPA {s.gpa}</div>
                </div>
              ))}
            </div>
          </div>

          {/* Selected Student Details */}
          {selectedStudent && (
            <div className="card">
              <div className="card-header">Student Details: {selectedStudent.name}</div>
              <div className="card-body student-details">
                <div className="detail-row">
                  <span className="label">Age</span>
                  <span className="value">{selectedStudent.age}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Gender</span>
                  <span className="value">{selectedStudent.gender}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Year</span>
                  <span className="value">{selectedStudent.year}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Major</span>
                  <span className="value">{selectedStudent.major}</span>
                </div>
                <div className="detail-row">
                  <span className="label">GPA</span>
                  <span className="value">{selectedStudent.gpa}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Roommate</span>
                  <span className="value">{selectedStudent.roommate || 'None'}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Preferences</span>
                  <span className="value">
                    {selectedStudent.roommatePreferences?.join(', ') || 'None'}
                  </span>
                </div>
                <div className="detail-row">
                  <span className="label">Internships</span>
                  <span className="value">
                    {selectedStudent.previousInternships?.join(', ') || 'None'}
                  </span>
                </div>

                {/* Friends and Chat History */}
                <div className="friends-chats">
                  <div className="friends-list">
                    <h4>Friends</h4>
                    {selectedStudent.friends?.length > 0 ? (
                      selectedStudent.friends.join(', ')
                    ) : (
                      <span className="no-data">No friends yet</span>
                    )}
                  </div>
                  <div className="chat-history">
                    <h4>Chat History</h4>
                    {selectedStudent.chatHistory?.length > 0 ? (
                      selectedStudent.chatHistory.map((msg, i) => (
                        <div key={i} className="chat-message">{msg}</div>
                      ))
                    ) : (
                      <span className="no-data">No messages yet</span>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
