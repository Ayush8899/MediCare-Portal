import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { Calendar, Clock, MapPin, Award, IndianRupee, AlertCircle, CheckCircle2, User } from 'lucide-react';

const DoctorDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated, isPatient } = useAuth();

  const [doctor, setDoctor] = useState(null);
  const [selectedDate, setSelectedDate] = useState(() => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  });
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [symptoms, setSymptoms] = useState('');

  const [loadingDoctor, setLoadingDoctor] = useState(true);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successModal, setSuccessModal] = useState(false);

  // Fetch doctor profile
  useEffect(() => {
    api.get(`/doctors/${id}`)
      .then((res) => setDoctor(res.data))
      .catch((err) => setErrorMessage('Failed to load doctor profile.'))
      .finally(() => setLoadingDoctor(false));
  }, [id]);

  // Fetch slots whenever date changes
  useEffect(() => {
    if (!id || !selectedDate) return;
    setLoadingSlots(true);
    setSelectedSlot(null);
    setErrorMessage('');

    api.get(`/doctors/${id}/slots`, { params: { date: selectedDate } })
      .then((res) => setSlots(res.data))
      .catch((err) => setErrorMessage('Failed to load slots for selected date.'))
      .finally(() => setLoadingSlots(false));
  }, [id, selectedDate]);

  const handleBooking = async (e) => {
    e.preventDefault();
    if (!selectedSlot) return;

    setBookingLoading(true);
    setErrorMessage('');

    try {
      const slotString = `${selectedSlot.startTime.substring(0, 5)} - ${selectedSlot.endTime.substring(0, 5)}`;
      await api.post('/appointments', {
        doctorId: Number(id),
        appointmentDate: selectedDate,
        timeSlot: slotString,
        symptoms: symptoms || 'General Medical Consultation'
      });

      setSuccessModal(true);
    } catch (err) {
      setErrorMessage(err.response?.data?.message || 'Could not complete booking. The slot may have just been booked.');
    } finally {
      setBookingLoading(false);
    }
  };

  if (loadingDoctor) {
    return <div style={{ textAlign: 'center', padding: '4rem' }}>Loading doctor details...</div>;
  }

  if (!doctor) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
        <p>Doctor not found.</p>
        <Link to="/doctors" className="btn btn-primary" style={{ marginTop: '1rem' }}>Back to Doctor Directory</Link>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto' }}>
      {/* Doctor Header Banner */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <span className="badge badge-role" style={{ marginBottom: '0.5rem' }}>{doctor.specialization}</span>
            <h1 style={{ fontSize: '1.85rem', fontWeight: 800 }}>{doctor.fullName}</h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginTop: '0.25rem' }}>
              {doctor.qualification} &bull; {doctor.experienceYears} Years Clinical Experience
            </p>
            <div style={{ display: 'flex', gap: '1.5rem', marginTop: '0.85rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <MapPin size={16} /> {doctor.city}
              </span>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Award size={16} /> Verified Medical Specialist
              </span>
            </div>
          </div>

          <div style={{ background: '#f8fafc', padding: '1rem 1.5rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border)', textAlign: 'right' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', display: 'block' }}>Consultation Fee</span>
            <span style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--primary)' }}>₹{doctor.consultationFee}</span>
          </div>
        </div>

        <div style={{ marginTop: '1.5rem', paddingTop: '1.25rem', borderTop: '1px solid var(--border)' }}>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '0.4rem' }}>About the Doctor</h3>
          <p style={{ color: 'var(--text-main)', fontSize: '0.925rem', lineHeight: 1.6 }}>{doctor.bio}</p>
        </div>
      </div>

      {/* Appointment Slot Booking Section */}
      <div className="card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Calendar size={22} color="var(--primary)" /> Select Appointment Date &amp; Time
        </h2>

        {errorMessage && (
          <div className="alert alert-danger">
            <AlertCircle size={18} /> {errorMessage}
          </div>
        )}

        {/* Date Selector */}
        <div className="form-group" style={{ maxWidth: '280px' }}>
          <label className="form-label">Consultation Date</label>
          <input
            type="date"
            className="form-input"
            min={new Date().toISOString().split('T')[0]}
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
          />
        </div>

        {/* Available Slots */}
        <div style={{ marginTop: '1.5rem' }}>
          <label className="form-label" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>Available Time Slots ({selectedDate})</span>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              <span style={{ display: 'inline-block', width: '10px', height: '10px', background: 'var(--primary)', borderRadius: '2px', marginRight: '4px' }}></span> Selected
              <span style={{ display: 'inline-block', width: '10px', height: '10px', background: '#f1f5f9', border: '1px solid #cbd5e1', borderRadius: '2px', margin: '0 4px 0 8px' }}></span> Booked
            </span>
          </label>

          {loadingSlots ? (
            <div style={{ padding: '1.5rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading slots...</div>
          ) : slots.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '0.5rem' }}>No slots available on this date.</p>
          ) : (
            <div className="slots-grid">
              {slots.map((slot) => {
                const isSelected = selectedSlot?.id === slot.id;
                const timeLabel = `${slot.startTime.substring(0, 5)}`;
                return (
                  <button
                    key={slot.id}
                    type="button"
                    disabled={slot.isBooked}
                    className={`slot-btn ${isSelected ? 'selected' : ''}`}
                    onClick={() => setSelectedSlot(slot)}
                  >
                    {timeLabel}
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Booking Form */}
        {selectedSlot && (
          <form onSubmit={handleBooking} style={{ marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border)' }}>
            <div className="form-group">
              <label className="form-label">Reason for Visit / Symptoms (Optional)</label>
              <textarea
                className="form-textarea"
                rows={3}
                placeholder="Briefly describe what you'd like to discuss with the doctor..."
                value={symptoms}
                onChange={(e) => setSymptoms(e.target.value)}
              />
            </div>

            {isAuthenticated ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <button type="submit" className="btn btn-primary" disabled={bookingLoading}>
                  {bookingLoading ? 'Securing Slot...' : `Confirm Booking with Dr. ${doctor.fullName}`}
                </button>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  Selected: <strong>{selectedDate}</strong> at <strong>{selectedSlot.startTime.substring(0, 5)}</strong>
                </span>
              </div>
            ) : (
              <div className="alert alert-info" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span>You must be signed in to book this appointment.</span>
                <Link to="/login" className="btn btn-primary btn-sm">Sign In to Continue</Link>
              </div>
            )}
          </form>
        )}
      </div>

      {/* Success Confirmation Modal */}
      {successModal && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ textAlign: 'center' }}>
            <div style={{ width: '56px', height: '56px', background: 'var(--accent-light)', color: 'var(--accent)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
              <CheckCircle2 size={32} />
            </div>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.5rem' }}>Appointment Confirmed!</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '1.5rem' }}>
              Your appointment with <strong>{doctor.fullName}</strong> is successfully scheduled for <strong>{selectedDate}</strong> at <strong>{selectedSlot?.startTime.substring(0, 5)}</strong>.
            </p>
            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'center' }}>
              <button onClick={() => navigate('/patient/dashboard')} className="btn btn-primary">
                Go to My Appointments
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DoctorDetail;
