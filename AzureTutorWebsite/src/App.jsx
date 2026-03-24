import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/layout/Layout';

// Auth
import Login from './pages/Auth/Login';
import Register from './pages/Auth/Register';

// Student
import StudentDashboard from './pages/student/Dashboard';
import ExploreCourses from './pages/student/ExploreCourses';
import Enrollments from './pages/student/Enrollments';

// Tutor
import TutorDashboard from './pages/tutor/Dashboard';
import ManageCourses from './pages/tutor/ManageCourses';
import Earnings from './pages/tutor/Earnings';
import BankDetails from './pages/tutor/BankDetails';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
            <Route path="/student/dashboard" element={<StudentDashboard />} />
            <Route path="/student/explore" element={<ExploreCourses />} />
            <Route path="/student/enrollments" element={<Enrollments />} />
            
            <Route path="/tutor/dashboard" element={<TutorDashboard />} />
            <Route path="/tutor/courses" element={<ManageCourses />} />
            <Route path="/tutor/earnings" element={<Earnings />} />
            <Route path="/tutor/bank-details" element={<BankDetails />} />
          </Route>

          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
