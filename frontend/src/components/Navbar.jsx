import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Activity, LogOut, User, Calendar, Stethoscope, ShieldCheck } from 'lucide-react';

const Navbar = () => {
  const { user, isAuthenticated, logout, isPatient, isDoctor, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <div className="nav-content">
        <Link to="/" className="brand-logo">
          <Activity size={28} color="#0284c7" />
          <span>MediCare<span style={{ color: '#0f172a' }}>Portal</span></span>
        </Link>

        <nav className="nav-links">
          <Link to="/" className="nav-link">Home</Link>
          <Link to="/doctors" className="nav-link">Find Doctors</Link>

          {isAuthenticated ? (
            <>
              {isPatient && (
                <Link to="/patient/dashboard" className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <Calendar size={16} /> My Appointments
                </Link>
              )}

              {isDoctor && (
                <Link to="/doctor/dashboard" className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <Stethoscope size={16} /> Doctor Portal
                </Link>
              )}

              {isAdmin && (
                <Link to="/admin/dashboard" className="nav-link" style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <ShieldCheck size={16} /> Admin Panel
                </Link>
              )}

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginLeft: '0.5rem' }}>
                <span className="badge badge-role" style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                  <User size={13} /> {user.fullName}
                </span>
                <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Sign Out">
                  <LogOut size={16} /> Logout
                </button>
              </div>
            </>
          ) : (
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <Link to="/login" className="btn btn-secondary btn-sm">Sign In</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Navbar;
