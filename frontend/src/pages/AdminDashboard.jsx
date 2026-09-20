import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { Users, Stethoscope, Calendar, IndianRupee, ShieldCheck, CheckCircle2, Clock } from 'lucide-react';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [doctors, setDoctors] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({fullName:'',email:'',phone:'',specialization:'',qualification:'MBBS',experienceYears:0,consultationFee:500,city:'New Delhi'});
  const [message, setMessage] = useState('');

  useEffect(() => {
    const fetchAdminData = async () => {
      try {
        const [statsRes, docRes, userRes] = await Promise.all([
          api.get('/admin/stats'),
          api.get('/admin/doctors'),
          api.get('/admin/users')
        ]);
        setStats(statsRes.data);
        setDoctors(docRes.data);
        setUsers(userRes.data);
      } catch (err) {
        console.error('Failed to load admin data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchAdminData();
  }, []);

  const createDoctor = async (e) => { e.preventDefault(); try { const r = await api.post('/admin/doctors', {...form, experienceYears:Number(form.experienceYears), consultationFee:Number(form.consultationFee)}); setMessage(r.data.message || 'Doctor created'); setForm({fullName:'',email:'',phone:'',specialization:'',qualification:'MBBS',experienceYears:0,consultationFee:500,city:'New Delhi'}); const d=await api.get('/admin/doctors'); setDoctors(d.data); } catch(err){ setMessage(err.response?.data?.message || 'Unable to create doctor'); } };
  const toggleDoctor = async (id, active) => { try { await api.patch(`/admin/doctors/${id}/status?active=${!active}`); const d=await api.get('/admin/doctors'); setDoctors(d.data); } catch(err){setMessage('Unable to update doctor status');} };


  if (loading) {
    return <div style={{ textAlign: 'center', padding: '4rem' }}>Loading platform metrics...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800 }}>System Administration</h1>
        <p style={{ color: 'var(--text-muted)' }}>Real-time platform metrics, registered doctors, and user accounts</p>
      </div>

      {/* KPI Stats Cards */}
      {stats && (
        <div className="grid-4" style={{ marginBottom: '2.5rem' }}>
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Total Patients</p>
                <h3 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem' }}>{stats.totalPatients}</h3>
              </div>
              <div style={{ padding: '0.6rem', background: 'var(--primary-light)', color: 'var(--primary)', borderRadius: '10px' }}>
                <Users size={22} />
              </div>
            </div>
          </div>

          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Active Doctors</p>
                <h3 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem' }}>{stats.totalDoctors}</h3>
              </div>
              <div style={{ padding: '0.6rem', background: 'var(--accent-light)', color: 'var(--accent)', borderRadius: '10px' }}>
                <Stethoscope size={22} />
              </div>
            </div>
          </div>

          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Appointments</p>
                <h3 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem' }}>{stats.totalAppointments}</h3>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {stats.completedAppointments} completed &bull; {stats.pendingAppointments} pending
                </span>
              </div>
              <div style={{ padding: '0.6rem', background: 'var(--warning-light)', color: '#b45309', borderRadius: '10px' }}>
                <Calendar size={22} />
              </div>
            </div>
          </div>

          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>Completed Revenue</p>
                <h3 style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.25rem', color: 'var(--accent)' }}>₹{stats.totalRevenue}</h3>
              </div>
              <div style={{ padding: '0.6rem', background: 'var(--accent-light)', color: 'var(--accent)', borderRadius: '10px' }}>
                <IndianRupee size={22} />
              </div>
            </div>
          </div>
        </div>
      )}


      <div className="card" style={{marginBottom:'2.5rem'}}>
        <h2 style={{fontSize:'1.25rem',fontWeight:800}}>Add Doctor to Hospital</h2>
        <p style={{color:'var(--text-muted)'}}>Admin creates the account. A secure temporary password is generated and emailed to the doctor.</p>
        <form onSubmit={createDoctor} className="grid-4">
          {['fullName','email','phone','specialization','qualification','city','experienceYears','consultationFee'].map(k=><input key={k} required={['fullName','email','specialization','qualification'].includes(k)} className="input" placeholder={k} value={form[k]} onChange={e=>setForm({...form,[k]:e.target.value})}/>)}
          <button className="btn btn-primary" type="submit">Create Doctor Account</button>
        </form>
        {message && <p style={{marginTop:'1rem',fontWeight:600}}>{message}</p>}
      </div>


      {/* Doctors Table */}
      <div style={{ marginBottom: '2.5rem' }}>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1rem' }}>Registered Doctor Specialists</h2>
        <div className="table-responsive card" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr>
                <th>Doctor Name</th>
                <th>Specialization</th>
                <th>City</th>
                <th>Experience</th>
                <th>Fee</th>
                <th>Account</th>
              </tr>
            </thead>
            <tbody>
              {doctors.map((doc) => (
                <tr key={doc.id}>
                  <td>
                    <div style={{ fontWeight: 700 }}>{doc.fullName}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{doc.email}</div>
                  </td>
                  <td><span className="badge badge-role">{doc.specialization}</span></td>
                  <td>{doc.city}</td>
                  <td>{doc.experienceYears} Years</td>
                  <td style={{ fontWeight: 700, color: 'var(--primary)' }}>₹{doc.consultationFee}</td>
                  <td><button className="btn" onClick={()=>toggleDoctor(doc.id, doc.active !== false)}>{doc.active === false ? 'Activate' : 'Deactivate'}</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Users Table */}
      <div>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1rem' }}>Platform User Accounts</h2>
        <div className="table-responsive card" style={{ padding: 0 }}>
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>System Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td style={{ fontWeight: 600 }}>{u.fullName}</td>
                  <td>{u.email}</td>
                  <td>{u.phone || 'N/A'}</td>
                  <td>
                    <span className={`badge badge-${u.role === 'ROLE_ADMIN' ? 'CANCELLED' : u.role === 'ROLE_DOCTOR' ? 'active' : 'role'}`}>
                      {u.role.replace('ROLE_', '')}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
