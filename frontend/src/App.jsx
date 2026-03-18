import React from 'react'
import { Routes, Route, Navigate, Link, useNavigate } from 'react-router-dom'
import RegisterPage from './pages/RegisterPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import { isAuthed, logout } from './lib/auth.js'

function NavBar() {
  const navigate = useNavigate()
  const authed = isAuthed()
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand" to="/">LoanPay</Link>
        <div className="d-flex gap-2">
          {!authed && <Link className="btn btn-outline-light btn-sm" to="/register">Register</Link>}
          {!authed && <Link className="btn btn-outline-light btn-sm" to="/login">Login</Link>}
          {authed && (
            <button className="btn btn-warning btn-sm" onClick={() => { logout(); navigate('/login') }}>
              Logout
            </button>
          )}
        </div>
      </div>
    </nav>
  )
}

function PrivateRoute({ children }) {
  return isAuthed() ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <>
      <NavBar />
      <div className="container my-4">
        <Routes>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="*" element={<div className="alert alert-danger">Not found</div>} />
        </Routes>
      </div>
    </>
  )
}
