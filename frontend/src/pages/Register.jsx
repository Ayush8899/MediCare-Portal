import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus, AlertCircle } from 'lucide-react';

const Register = () => {
  const [role, setRole] = useState('ROLE_PATIENT');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');

  // Doctor-specific fields
  const [specialization, setSpecialization] = useState('Cardiology');
  const [qualification, setQualification] = useState('');
  const [experienceYears, setExperienceYears] = useState(3);
  const [consultationFee, setConsultationFee] = useState(500);
  const [city, setCity] = useState('New Delhi');
  const [bio, setBio] = useState('');

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const payload = {
        fullName,
        email,
        password,
        phone,
        role,
        ...(role === 'ROLE_DOCTOR' && {
          specialization,
          qualification: qualification || 'MBBS',
          experienceYears: Number(experienceYears),
          consultationFee: Number(consultationFee),
          city,
          bio: bio || 'Qualified healthcare specialist.'
        })
      };

      const user = await register(payload);
      if (user.role === 'ROLE_DOCTOR') {
        navigate('/doctor/dashboard');
      } else {
        navigate('/patient/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please check inputs and try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '520px', margin: '2rem auto' }}>
      <div className="card">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div style={{ width: '48px', height: '48px', background: 'var(--accent-light)', color: 'var(--accent)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 0.75rem' }}>
            <UserPlus size={24} />
          </div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Create New Account</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Sign up to access medical services</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={18} /> {error}
          </div>
        )}

        {/* Role Selector Tabs */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', marginBottom: '1.5rem', background: '#f1f5f9', padding: '0.25rem', borderRadius: 'var(--radius-md)' }}>
          <button
            type="button"
            className="btn"
            style={{
              background: role === 'ROLE_PATIENT' ? '#ffffff' : 'transparent',
              color: role === 'ROLE_PATIENT' ? 'var(--primary)' : 'var(--text-muted)',
              boxShadow: role === 'ROLE_PATIENT' ? 'var(--shadow-sm)' : 'none',
              padding: '0.5rem'
            }}
            onClick={() => setRole('ROLE_PATIENT')}
          >
            I am a Patient
          </button>
          <button
            type="button"
            className="btn"
            style={{
              background: role === 'ROLE_DOCTOR' ? '#ffffff' : 'transparent',
              color: role === 'ROLE_DOCTOR' ? 'var(--primary)' : 'var(--text-muted)',
              boxShadow: role === 'ROLE_DOCTOR' ? 'var(--shadow-sm)' : 'none',
              padding: '0.5rem'
            }}
            onClick={() => setRole('ROLE_DOCTOR')}
          >
            I am a Doctor
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              className="form-input"
              required
              placeholder={role === 'ROLE_DOCTOR' ? 'e.g. Dr. Jane Doe' : 'e.g. Jane Doe'}
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
            />
          </div>

          <div className="grid-2" style={{ gap: '1rem', marginBottom: '1.25rem' }}>
            <div>
              <label className="form-label">Email Address</label>
              <input
                type="email"
                className="form-input"
                required
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div>
              <label className="form-label">Phone Number</label>
              <input
                type="tel"
                className="form-input"
                placeholder="+91 98765 43210"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Password (min. 6 characters)</label>
            <input
              type="password"
              className="form-input"
              required
              minLength={6}
              placeholder="Create a secure password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {/* Conditional Fields for Doctor */}
          {role === 'ROLE_DOCTOR' && (
            <div style={{ background: '#f8fafc', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border)', marginBottom: '1.25rem' }}>
              <h4 style={{ fontSize: '0.9rem', marginBottom: '0.75rem', color: 'var(--primary)' }}>Professional Details</h4>

              <div className="grid-2" style={{ gap: '1rem', marginBottom: '0.75rem' }}>
                <div>
                  <label className="form-label">Specialization</label>
                  <select
                    className="form-select"
                    value={specialization}
                    onChange={(e) => setSpecialization(e.target.value)}
                  >
                    <option value="Cardiology">Cardiology</option>
                    <option value="Dermatology">Dermatology</option>
                    <option value="Pediatrics">Pediatrics</option>
                    <option value="Orthopedics">Orthopedics</option>
                    <option value="General Physician">General Physician</option>
                    <option value="Neurology">Neurology</option>
                  </select>
                </div>
                <div>
                  <label className="form-label">Qualification</label>
                  <input
                    type="text"
                    className="form-input"
                    placeholder="e.g. MBBS, MD"
                    value={qualification}
                    onChange={(e) => setQualification(e.target.value)}
                  />
                </div>
              </div>

              <div className="grid-2" style={{ gap: '1rem', marginBottom: '0.75rem' }}>
                <div>
                  <label className="form-label">Experience (Years)</label>
                  <input
                    type="number"
                    min="0"
                    className="form-input"
                    value={experienceYears}
                    onChange={(e) => setExperienceYears(e.target.value)}
                  />
                </div>
                <div>
                  <label className="form-label">Consultation Fee (₹)</label>
                  <input
                    type="number"
                    min="100"
                    step="50"
                    className="form-input"
                    value={consultationFee}
                    onChange={(e) => setConsultationFee(e.target.value)}
                  />
                </div>
              </div>

              <div className="form-group" style={{ marginBottom: 0 }}>
                <label className="form-label">City</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. New Delhi, Mumbai"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                />
              </div>
            </div>
          )}

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '0.5rem' }} disabled={loading}>
            {loading ? 'Creating Account...' : 'Complete Registration'}
          </button>
        </form>

        <p style={{ textAlign: 'center', fontSize: '0.875rem', marginTop: '1.5rem', color: 'var(--text-muted)' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>Sign in</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
