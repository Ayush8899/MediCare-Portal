import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import { Calendar, FileText, AlertCircle, XCircle, CheckCircle, Clock, Stethoscope, Eye } from 'lucide-react';

const PatientDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [activeTab, setActiveTab] = useState('appointments'); // 'appointments' | 'prescriptions'
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState('');
  const [selectedPrescription, setSelectedPrescription] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [apptRes, prescRes] = await Promise.all([
        api.get('/appointments/my-appointments'),
        api.get('/prescriptions/my-prescriptions')
      ]);
      setAppointments(apptRes.data);
      setPrescriptions(prescRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCancel = async (appointmentId) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.patch(`/appointments/${appointmentId}/status`, { status: 'CANCELLED' });
      fetchData();
    } catch (err) {
      setActionError(err.response?.data?.message || 'Failed to cancel appointment.');
    }
  };

  const handleViewPrescription = async (appointmentId) => {
    try {
      const res = await api.get(`/prescriptions/appointment/${appointmentId}`);
      setSelectedPrescription(res.data);
    } catch (err) {
      alert('Prescription details not available.');
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800 }}>Patient Dashboard</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage your healthcare consultations and prescriptions</p>
        </div>
        <Link to="/doctors" className="btn btn-primary">
          <Calendar size={18} /> Book New Appointment
        </Link>
      </div>

      {actionError && (
        <div className="alert alert-danger">
          <AlertCircle size={18} /> {actionError}
        </div>
      )}

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '1rem', borderBottom: '1px solid var(--border)', marginBottom: '1.5rem' }}>
        <button
          onClick={() => setActiveTab('appointments')}
          style={{
            padding: '0.75rem 1rem',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'appointments' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'appointments' ? 'var(--primary)' : 'var(--text-muted)',
            fontWeight: 700,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '0.4rem'
          }}
        >
          <Calendar size={18} /> My Appointments ({appointments.length})
        </button>
        <button
          onClick={() => setActiveTab('prescriptions')}
          style={{
            padding: '0.75rem 1rem',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'prescriptions' ? '2px solid var(--primary)' : '2px solid transparent',
            color: activeTab === 'prescriptions' ? 'var(--primary)' : 'var(--text-muted)',
            fontWeight: 700,
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '0.4rem'
          }}
        >
          <FileText size={18} /> Medical Prescriptions ({prescriptions.length})
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>Loading records...</div>
      ) : activeTab === 'appointments' ? (
        appointments.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
            <Calendar size={40} color="var(--text-muted)" style={{ margin: '0 auto 1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>No Appointments Scheduled</h3>
            <p style={{ color: 'var(--text-muted)', marginBottom: '1.5rem' }}>You haven't booked any medical appointments yet.</p>
            <Link to="/doctors" className="btn btn-primary">Find a Doctor</Link>
          </div>
        ) : (
          <div className="table-responsive card" style={{ padding: 0 }}>
            <table>
              <thead>
                <tr>
                  <th>Doctor</th>
                  <th>Specialty</th>
                  <th>Date &amp; Time</th>
                  <th>Symptoms</th>
                  <th>Fee</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {appointments.map((appt) => (
                  <tr key={appt.id}>
                    <td>
                      <div style={{ fontWeight: 700 }}>{appt.doctorName}</div>
                    </td>
                    <td><span className="badge badge-role">{appt.specialization}</span></td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{appt.appointmentDate}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{appt.timeSlot}</div>
                    </td>
                    <td style={{ maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {appt.symptoms || 'General Checkup'}
                    </td>
                    <td>₹{appt.consultationFee}</td>
                    <td>
                      <span className={`badge badge-${appt.status}`}>{appt.status}</span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        {appt.hasPrescription && (
                          <button
                            onClick={() => handleViewPrescription(appt.id)}
                            className="btn btn-secondary btn-sm"
                            title="View Prescription"
                          >
                            <FileText size={14} /> Prescription
                          </button>
                        )}
                        {appt.status !== 'CANCELLED' && appt.status !== 'COMPLETED' && (
                          <button
                            onClick={() => handleCancel(appt.id)}
                            className="btn btn-danger btn-sm"
                            title="Cancel Booking"
                          >
                            Cancel
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      ) : (
        /* Prescriptions Tab */
        prescriptions.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
            <FileText size={40} color="var(--text-muted)" style={{ margin: '0 auto 1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>No Prescriptions Found</h3>
            <p style={{ color: 'var(--text-muted)' }}>Completed appointments with medical advice will show here.</p>
          </div>
        ) : (
          <div className="grid-2">
            {prescriptions.map((presc) => (
              <div key={presc.id} className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.15rem', fontWeight: 700 }}>{presc.doctorName}</h3>
                    <span className="badge badge-role">{presc.doctorSpecialization}</span>
                  </div>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{presc.appointmentDate}</span>
                </div>

                <div style={{ marginTop: '1rem' }}>
                  <p style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Diagnosis</p>
                  <p style={{ fontWeight: 600, color: 'var(--text-main)', marginBottom: '0.75rem' }}>{presc.diagnosis}</p>

                  <p style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Prescribed Medicines</p>
                  <p style={{ fontSize: '0.9rem', color: 'var(--text-main)', background: '#f8fafc', padding: '0.65rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border)', whiteSpace: 'pre-wrap', marginBottom: '0.75rem' }}>
                    {presc.medicines}
                  </p>

                  {presc.advice && (
                    <>
                      <p style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Doctor Advice</p>
                      <p style={{ fontSize: '0.875rem', color: 'var(--text-main)' }}>{presc.advice}</p>
                    </>
                  )}
                </div>
              </div>
            ))}
          </div>
        )
      )}

      {/* Prescription Viewer Modal */}
      {selectedPrescription && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border)', paddingBottom: '1rem', marginBottom: '1.25rem' }}>
              <div>
                <h2 style={{ fontSize: '1.35rem', fontWeight: 800 }}>Medical Prescription</h2>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Issued by {selectedPrescription.doctorName}</p>
              </div>
              <button onClick={() => setSelectedPrescription(null)} className="btn btn-secondary btn-sm">Close</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Consultation Date</span>
                <p style={{ fontWeight: 600 }}>{selectedPrescription.appointmentDate}</p>
              </div>

              <div>
                <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Clinical Diagnosis</span>
                <p style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--primary)' }}>{selectedPrescription.diagnosis}</p>
              </div>

              <div>
                <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Prescribed Medicines</span>
                <div style={{ background: '#f8fafc', padding: '0.85rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border)', marginTop: '0.25rem', whiteSpace: 'pre-wrap' }}>
                  {selectedPrescription.medicines}
                </div>
              </div>

              {selectedPrescription.advice && (
                <div>
                  <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>Doctor's Advice &amp; Diet</span>
                  <p style={{ marginTop: '0.25rem', fontSize: '0.925rem' }}>{selectedPrescription.advice}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PatientDashboard;
