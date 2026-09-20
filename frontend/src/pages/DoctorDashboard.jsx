import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Stethoscope, Calendar, Clock, Check, X, FileEdit, CheckCircle2, AlertCircle } from 'lucide-react';

const DoctorDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Prescription modal state
  const [activeAppointment, setActiveAppointment] = useState(null);
  const [diagnosis, setDiagnosis] = useState('');
  const [medicines, setMedicines] = useState('');
  const [advice, setAdvice] = useState('');
  const [submittingPrescription, setSubmittingPrescription] = useState(false);

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      const res = await api.get('/appointments/my-appointments');
      setAppointments(res.data);
    } catch (err) {
      setError('Failed to fetch appointments.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleStatusUpdate = async (id, status) => {
    try {
      await api.patch(`/appointments/${id}/status`, { status });
      setSuccess(`Appointment status updated to ${status}.`);
      fetchAppointments();
      setTimeout(() => setSuccess(''), 3500);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update status.');
    }
  };

  const handleOpenPrescriptionModal = (appt) => {
    setActiveAppointment(appt);
    setDiagnosis('');
    setMedicines('');
    setAdvice('');
    setError('');
  };

  const handleSavePrescription = async (e) => {
    e.preventDefault();
    if (!activeAppointment) return;

    setSubmittingPrescription(true);
    try {
      await api.post('/prescriptions', {
        appointmentId: activeAppointment.id,
        diagnosis,
        medicines,
        advice
      });

      setSuccess('Prescription issued and appointment marked as COMPLETED!');
      setActiveAppointment(null);
      fetchAppointments();
      setTimeout(() => setSuccess(''), 3500);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit prescription.');
    } finally {
      setSubmittingPrescription(false);
    }
  };

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800 }}>Doctor Portal</h1>
        <p style={{ color: 'var(--text-muted)' }}>Manage patient consultations, schedules, and write digital prescriptions</p>
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          <CheckCircle2 size={18} /> {success}
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>Loading appointments...</div>
      ) : appointments.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <Stethoscope size={40} color="var(--text-muted)" style={{ margin: '0 auto 1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No Consultations Scheduled</h3>
          <p style={{ color: 'var(--text-muted)' }}>Patients who book time slots with you will appear here.</p>
        </div>
      ) : (
        <div className="table-responsive card" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr>
                <th>Patient Name</th>
                <th>Date &amp; Time</th>
                <th>Reported Symptoms</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((appt) => (
                <tr key={appt.id}>
                  <td>
                    <div style={{ fontWeight: 700 }}>{appt.patientName}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{appt.patientEmail} &bull; {appt.patientPhone}</div>
                  </td>
                  <td>
                    <div style={{ fontWeight: 600 }}>{appt.appointmentDate}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{appt.timeSlot}</div>
                  </td>
                  <td style={{ maxWidth: '240px' }}>
                    {appt.symptoms || 'General Checkup'}
                  </td>
                  <td>
                    <span className={`badge badge-${appt.status}`}>{appt.status}</span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                      {appt.status === 'PENDING' && (
                        <button
                          onClick={() => handleStatusUpdate(appt.id, 'CONFIRMED')}
                          className="btn btn-primary btn-sm"
                        >
                          <Check size={14} /> Confirm
                        </button>
                      )}

                      {appt.status === 'CONFIRMED' && (
                        <button
                          onClick={() => handleOpenPrescriptionModal(appt)}
                          className="btn btn-primary btn-sm"
                          style={{ background: 'var(--accent)' }}
                        >
                          <FileEdit size={14} /> Prescribe
                        </button>
                      )}

                      {appt.status !== 'COMPLETED' && appt.status !== 'CANCELLED' && (
                        <button
                          onClick={() => handleStatusUpdate(appt.id, 'CANCELLED')}
                          className="btn btn-danger btn-sm"
                        >
                          <X size={14} /> Cancel
                        </button>
                      )}

                      {appt.status === 'COMPLETED' && (
                        <span style={{ fontSize: '0.8rem', color: 'var(--accent)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                          <CheckCircle2 size={15} /> Completed
                        </span>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Digital Prescription Modal */}
      {activeAppointment && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border)', paddingBottom: '1rem', marginBottom: '1.25rem' }}>
              <div>
                <h2 style={{ fontSize: '1.35rem', fontWeight: 800 }}>Issue Digital Prescription</h2>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Patient: <strong>{activeAppointment.patientName}</strong></p>
              </div>
              <button onClick={() => setActiveAppointment(null)} className="btn btn-secondary btn-sm">Close</button>
            </div>

            <form onSubmit={handleSavePrescription}>
              <div className="form-group">
                <label className="form-label">Clinical Diagnosis *</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  placeholder="e.g. Acute Pharyngitis / Seasonal Allergies"
                  value={diagnosis}
                  onChange={(e) => setDiagnosis(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Medicines Prescribed *</label>
                <textarea
                  className="form-textarea"
                  rows={4}
                  required
                  placeholder="e.g.&#10;1. Paracetamol 500mg - 1 tab after meals (3 days)&#10;2. Cetirizine 10mg - 1 tab at bedtime (5 days)"
                  value={medicines}
                  onChange={(e) => setMedicines(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Advice &amp; Follow-up Instructions</label>
                <textarea
                  className="form-textarea"
                  rows={2}
                  placeholder="e.g. Drink plenty of warm water. Rest for 2 days. Follow up if fever persists."
                  value={advice}
                  onChange={(e) => setAdvice(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1.5rem' }}>
                <button type="button" onClick={() => setActiveAppointment(null)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary" disabled={submittingPrescription}>
                  {submittingPrescription ? 'Saving...' : 'Save & Complete Consultation'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DoctorDashboard;
