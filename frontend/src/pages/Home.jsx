import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { Stethoscope, Calendar, Shield, Clock, Heart, Award, ArrowRight } from 'lucide-react';

const Home = () => {
  const [doctors, setDoctors] = useState([]);
  const [specializations, setSpecializations] = useState([]);

  useEffect(() => {
    api.get('/doctors')
      .then((res) => setDoctors(res.data.slice(0, 3)))
      .catch((err) => console.error('Error loading doctors:', err));

    api.get('/doctors/specializations')
      .then((res) => setSpecializations(res.data))
      .catch((err) => console.error('Error loading specialties:', err));
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section className="hero">
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(255,255,255,0.18)', padding: '0.35rem 1rem', borderRadius: '9999px', fontSize: '0.875rem', fontWeight: 600, marginBottom: '1.25rem' }}>
          <Heart size={16} /> Certified Healthcare Specialists
        </div>
        <h1>Instant Doctor Appointments<br />&amp; Digital Health Records</h1>
        <p>
          Connect with trusted doctors, book verified time slots without double-booking conflicts, and access digital prescriptions anytime.
        </p>
        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
          <Link to="/doctors" className="btn btn-secondary" style={{ background: '#ffffff', color: '#0284c7', border: 'none' }}>
            Book Consultation <ArrowRight size={18} />
          </Link>
          <Link to="/register" className="btn btn-outline" style={{ borderColor: '#ffffff', color: '#ffffff' }}>
            Join as Patient / Doctor
          </Link>
        </div>
      </section>

      {/* Feature Highlights */}
      <section style={{ marginBottom: '3rem' }}>
        <div className="grid-3">
          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ width: '48px', height: '48px', margin: '0 auto 1rem', background: 'var(--primary-light)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--primary)' }}>
              <Clock size={24} />
            </div>
            <h3 style={{ marginBottom: '0.5rem' }}>Real-time Slot Booking</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Zero double-booking guarantee backed by transactional database isolation and optimistic concurrency control.
            </p>
          </div>

          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ width: '48px', height: '48px', margin: '0 auto 1rem', background: 'var(--accent-light)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--accent)' }}>
              <Award size={24} />
            </div>
            <h3 style={{ marginBottom: '0.5rem' }}>Verified Medical Specialists</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Detailed doctor profiles with credentials, qualifications, consultation fees, and clinical experience.
            </p>
          </div>

          <div className="card" style={{ textAlign: 'center' }}>
            <div style={{ width: '48px', height: '48px', margin: '0 auto 1rem', background: 'var(--warning-light)', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#b45309' }}>
              <Shield size={24} />
            </div>
            <h3 style={{ marginBottom: '0.5rem' }}>Secure Digital Prescriptions</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Role-protected patient records and prescriptions issued directly by attending doctors with JWT auth.
            </p>
          </div>
        </div>
      </section>

      {/* Featured Doctors */}
      <section style={{ marginBottom: '3rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', marginBottom: '1.5rem' }}>
          <div>
            <h2 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Featured Doctors</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>Consult with top-rated medical specialists</p>
          </div>
          <Link to="/doctors" style={{ color: 'var(--primary)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            View All <ArrowRight size={16} />
          </Link>
        </div>

        <div className="grid-3">
          {doctors.map((doc) => (
            <div key={doc.id} className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                <span className="badge badge-role">{doc.specialization}</span>
                <span style={{ fontWeight: 700, color: 'var(--primary)' }}>₹{doc.consultationFee}</span>
              </div>
              <h3 style={{ fontSize: '1.15rem', marginBottom: '0.25rem' }}>{doc.fullName}</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '0.75rem' }}>
                {doc.qualification} &bull; {doc.experienceYears} yrs experience
              </p>
              <p style={{ color: 'var(--text-main)', fontSize: '0.875rem', marginBottom: '1.25rem', height: '40px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {doc.bio}
              </p>
              <Link to={`/doctors/${doc.id}`} className="btn btn-primary btn-sm" style={{ width: '100%' }}>
                <Calendar size={15} /> Book Consultation
              </Link>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;
