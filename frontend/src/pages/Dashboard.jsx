import React, { useState, useEffect } from 'react';
import { DashboardService, PatientService, AppointmentService } from '../services/api';
import { Users, Calendar, AlertCircle, IndianRupee, UserPlus, PlusSquare, Receipt, ExternalLink } from 'lucide-react';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [recentPatients, setRecentPatients] = useState([]);
  const [upcomingAppointments, setUpcomingAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadDashboardData() {
      try {
        setLoading(true);
        const [statsRes, patientsRes, appointmentsRes] = await Promise.all([
          DashboardService.getStats(),
          PatientService.getAll(),
          AppointmentService.getAll()
        ]);
        
        setStats(statsRes.data);
        // Take the 5 most recently admitted patients
        setRecentPatients(patientsRes.data.slice(-5).reverse());
        // Take the 5 upcoming appointments (e.g. status SCHEDULED)
        setUpcomingAppointments(
          appointmentsRes.data
            .filter(app => app.status === 'SCHEDULED')
            .slice(0, 5)
        );
        setError(null);
      } catch (err) {
        console.error('Failed to load dashboard data:', err);
        setError('Could not connect to the backend server. Please verify it is running.');
      } finally {
        setLoading(false);
      }
    }

    loadDashboardData();
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <p style={{ fontSize: '1.2rem', color: 'var(--text-secondary)' }}>Loading dashboard analytics...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: '2rem', textAlign: 'center', backgroundColor: 'rgba(239, 68, 68, 0.1)', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-danger)', marginTop: '2rem' }}>
        <h3 style={{ color: 'var(--color-danger)', marginBottom: '0.5rem' }}>Connection Error</h3>
        <p style={{ color: 'var(--text-primary)', marginBottom: '1.5rem' }}>{error}</p>
        <button className="btn btn-primary" onClick={() => window.location.reload()}>Retry Connection</button>
      </div>
    );
  }

  const pStats = stats?.patients || { total: 0, inpatient: 0, outpatient: 0 };
  const aStats = stats?.appointments || { total: 0, scheduled: 0, completed: 0 };
  const bStats = stats?.billing || { totalBills: 0, totalRevenue: 0, pendingAmount: 0 };

  return (
    <div>
      <div className="page-header">
        <div className="page-title">
          <h1>Clinical Dashboard</h1>
          <p>Real-time hospital operations & resource overview</p>
        </div>
        <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
          {new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </div>
      </div>

      {/* Stats Cards Grid */}
      <div className="dashboard-grid">
        <div className="card">
          <div className="stat-card-header">
            <div className="stat-card-icon icon-blue">
              <Users size={20} />
            </div>
            <span className="stat-card-title">Total Patients</span>
          </div>
          <div className="stat-card-value">{pStats.total}</div>
          <div className="stat-card-detail">
            {pStats.inpatient} inpatient · {pStats.outpatient} outpatient
          </div>
        </div>

        <div className="card">
          <div className="stat-card-header">
            <div className="stat-card-icon icon-green">
              <Calendar size={20} />
            </div>
            <span className="stat-card-title">Appointments</span>
          </div>
          <div className="stat-card-value">{aStats.total}</div>
          <div className="stat-card-detail">
            {aStats.scheduled} scheduled · {aStats.completed} completed
          </div>
        </div>

        <div className="card">
          <div className="stat-card-header">
            <div className="stat-card-icon icon-orange">
              <AlertCircle size={20} />
            </div>
            <span className="stat-card-title">Pending Bills</span>
          </div>
          <div className="stat-card-value">{bStats.pendingCount || 0}</div>
          <div className="stat-card-detail">
            Amount: ₹{(bStats.pendingAmount || 0).toLocaleString('en-IN')}
          </div>
        </div>

        <div className="card">
          <div className="stat-card-header">
            <div className="stat-card-icon icon-purple">
              <IndianRupee size={20} />
            </div>
            <span className="stat-card-title">Total Revenue</span>
          </div>
          <div className="stat-card-value">₹{(bStats.totalRevenue || 0).toLocaleString('en-IN')}</div>
          <div className="stat-card-detail">
            Paid: ₹{(bStats.paidRevenue || 0).toLocaleString('en-IN')}
          </div>
        </div>
      </div>

      {/* Quick Actions Card */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h3 style={{ marginBottom: '1rem', fontSize: '1.1rem' }}>Quick Actions</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem' }}>
          <a href="/patients" className="btn btn-secondary" style={{ justifyContent: 'center', padding: '1rem' }}>
            <UserPlus size={18} />
            <span>New Patient</span>
          </a>
          <a href="/appointments" className="btn btn-secondary" style={{ justifyContent: 'center', padding: '1rem' }}>
            <PlusSquare size={18} />
            <span>Schedule</span>
          </a>
          <a href="/billing" className="btn btn-secondary" style={{ justifyContent: 'center', padding: '1rem' }}>
            <Receipt size={18} />
            <span>New Bill</span>
          </a>
          <a href="/swagger-ui.html" target="_blank" rel="noreferrer" className="btn btn-secondary" style={{ justifyContent: 'center', padding: '1rem' }}>
            <ExternalLink size={18} />
            <span>API Docs</span>
          </a>
        </div>
      </div>

      {/* Double Column Table Layout */}
      <div className="section-grid">
        {/* Recent Patients */}
        <div className="table-container">
          <div className="table-header-bar">
            <h3 className="table-header-title">Recent Admitted Patients</h3>
            <a href="/patients" style={{ fontSize: '0.85rem', color: 'var(--color-primary)', textDecoration: 'none', fontWeight: '600' }}>View All</a>
          </div>
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Status</th>
                <th>Diagnosis</th>
              </tr>
            </thead>
            <tbody>
              {recentPatients.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>No patients found</td>
                </tr>
              ) : (
                recentPatients.map(patient => (
                  <tr key={patient.id}>
                    <td>
                      <div style={{ fontWeight: '600' }}>{patient.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{patient.gender}, {patient.age} yrs</div>
                    </td>
                    <td>
                      <span className={`badge badge-${patient.patientType.toLowerCase()}`}>
                        {patient.patientType}
                      </span>
                    </td>
                    <td>
                      <span className={`badge badge-${patient.status.toLowerCase()}`}>
                        {patient.status}
                      </span>
                    </td>
                    <td style={{ color: 'var(--text-secondary)' }}>{patient.diagnosis || 'None'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Upcoming Appointments */}
        <div className="table-container">
          <div className="table-header-bar">
            <h3 className="table-header-title">Upcoming Appointments</h3>
            <a href="/appointments" style={{ fontSize: '0.85rem', color: 'var(--color-primary)', textDecoration: 'none', fontWeight: '600' }}>View All</a>
          </div>
          <table>
            <thead>
              <tr>
                <th>Patient</th>
                <th>Doctor</th>
                <th>Time</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {upcomingAppointments.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>No upcoming appointments</td>
                </tr>
              ) : (
                upcomingAppointments.map(app => (
                  <tr key={app.id}>
                    <td>
                      <div style={{ fontWeight: '600' }}>{app.patientName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{app.department}</div>
                    </td>
                    <td>{app.doctorName}</td>
                    <td>
                      <div style={{ fontWeight: '500' }}>{app.appointmentDate}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{app.appointmentTime}</div>
                    </td>
                    <td>
                      <span className={`badge badge-${app.status.toLowerCase()}`}>
                        {app.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
