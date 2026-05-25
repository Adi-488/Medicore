import React, { useState, useEffect } from 'react';
import { AppointmentService } from '../services/api';
import { Calendar, UserPlus, Edit2, Trash2, X, Clock } from 'lucide-react';

export default function Appointments() {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');
  const [doctorFilter, setDoctorFilter] = useState('');
  const [dateFilter, setDateFilter] = useState('');
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingApp, setEditingApp] = useState(null);
  
  // Toast notifications
  const [toast, setToast] = useState(null);
  
  // Form fields
  const [formData, setFormData] = useState({
    patientName: '',
    doctorName: '',
    department: 'CARDIOLOGY',
    appointmentDate: new Date().toISOString().split('T')[0],
    appointmentTime: '10:00 AM',
    status: 'SCHEDULED',
    notes: ''
  });

  useEffect(() => {
    loadAppointments();
  }, [statusFilter, doctorFilter, dateFilter]);

  async function loadAppointments() {
    try {
      setLoading(true);
      const res = await AppointmentService.getAll(statusFilter, doctorFilter, dateFilter);
      setAppointments(res.data);
    } catch (err) {
      showToast('Error', 'Failed to retrieve appointments.');
    } finally {
      setLoading(false);
    }
  }

  function showToast(type, text) {
    setToast({ type, text });
    setTimeout(() => setToast(null), 3000);
  }

  function openCreateModal() {
    setEditingApp(null);
    setFormData({
      patientName: '',
      doctorName: '',
      department: 'CARDIOLOGY',
      appointmentDate: new Date().toISOString().split('T')[0],
      appointmentTime: '10:00 AM',
      status: 'SCHEDULED',
      notes: ''
    });
    setShowModal(true);
  }

  function openEditModal(app) {
    setEditingApp(app);
    setFormData({
      patientName: app.patientName || '',
      doctorName: app.doctorName || '',
      department: app.department || 'CARDIOLOGY',
      appointmentDate: app.appointmentDate || new Date().toISOString().split('T')[0],
      appointmentTime: app.appointmentTime || '10:00 AM',
      status: app.status || 'SCHEDULED',
      notes: app.notes || ''
    });
    setShowModal(true);
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to cancel/remove this appointment?')) return;
    try {
      await AppointmentService.delete(id);
      showToast('Success', 'Appointment deleted successfully');
      loadAppointments();
    } catch (err) {
      showToast('Error', 'Failed to delete appointment.');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      if (!formData.patientName || !formData.doctorName) {
        showToast('Error', 'Please fill in required fields (Patient Name, Doctor Name)');
        return;
      }

      if (editingApp) {
        await AppointmentService.update(editingApp.id, formData);
        showToast('Success', 'Appointment updated successfully');
      } else {
        await AppointmentService.create(formData);
        showToast('Success', 'Appointment scheduled successfully');
      }
      setShowModal(false);
      loadAppointments();
    } catch (err) {
      console.error(err);
      showToast('Error', 'Failed to save appointment reservation.');
    }
  }

  return (
    <div>
      {/* Toast Alert */}
      {toast && (
        <div className="toast">
          <span style={{ fontWeight: 'bold', color: toast.type === 'Error' ? 'var(--color-danger)' : 'var(--color-success)' }}>
            {toast.type}:
          </span>
          <span>{toast.text}</span>
        </div>
      )}

      {/* Header */}
      <div className="page-header">
        <div className="page-title">
          <h1>Clinical Consultations & Appointments</h1>
          <p>Book and schedules patient checkups with specialists</p>
        </div>
        <button className="btn btn-primary" onClick={openCreateModal}>
          <Calendar size={18} />
          <span>Book Appointment</span>
        </button>
      </div>

      {/* Filters Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'center' }}>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <label style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 'bold', textTransform: 'uppercase' }}>Filter Status</label>
            <select
              className="form-control"
              style={{ width: '160px' }}
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All Statuses</option>
              <option value="SCHEDULED">Scheduled</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <label style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 'bold', textTransform: 'uppercase' }}>Doctor Name</label>
            <input
              type="text"
              placeholder="Filter by doctor..."
              value={doctorFilter}
              onChange={(e) => setDoctorFilter(e.target.value)}
              className="form-control"
              style={{ width: '200px' }}
            />
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <label style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 'bold', textTransform: 'uppercase' }}>Consultation Date</label>
            <input
              type="date"
              value={dateFilter}
              onChange={(e) => setDateFilter(e.target.value)}
              className="form-control"
              style={{ width: '160px' }}
            />
          </div>
          
          <div style={{ alignSelf: 'flex-end' }}>
            {(statusFilter || doctorFilter || dateFilter) && (
              <button className="btn btn-secondary" onClick={() => { setStatusFilter(''); setDoctorFilter(''); setDateFilter(''); loadAppointments(); }}>
                Clear Filters
              </button>
            )}
          </div>

        </div>
      </div>

      {/* Appointments Table */}
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Patient Name</th>
              <th>Doctor</th>
              <th>Department</th>
              <th>Date & Time</th>
              <th>Status</th>
              <th>Notes / Remarks</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                  Loading appointments calendar...
                </td>
              </tr>
            ) : appointments.length === 0 ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                  No appointments scheduled.
                </td>
              </tr>
            ) : (
              appointments.map((a) => (
                <tr key={a.id}>
                  <td style={{ fontWeight: 'bold', color: 'var(--text-muted)' }}>#{a.id}</td>
                  <td style={{ fontWeight: '700' }}>{a.patientName}</td>
                  <td>{a.doctorName}</td>
                  <td>
                    <span className="badge badge-outpatient">{a.department}</span>
                  </td>
                  <td>
                    <div style={{ fontWeight: '600', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                      <Calendar size={14} style={{ color: 'var(--color-primary)' }} />
                      {a.appointmentDate}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem', marginTop: '0.2rem' }}>
                      <Clock size={12} />
                      {a.appointmentTime}
                    </div>
                  </td>
                  <td>
                    <span className={`badge badge-${a.status.toLowerCase()}`}>
                      {a.status}
                    </span>
                  </td>
                  <td style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {a.notes || 'No remarks'}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                      <button className="btn btn-secondary" style={{ padding: '0.4rem' }} onClick={() => openEditModal(a)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-danger" style={{ padding: '0.4rem' }} onClick={() => handleDelete(a.id)}>
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Add / Edit Appointment Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2 className="modal-title">{editingApp ? 'Edit Consultation Details' : 'Book New Checkup'}</h2>
              <button className="btn btn-secondary" style={{ padding: '0.3rem' }} onClick={() => setShowModal(false)}>
                <X size={18} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Patient Name *</label>
                <input
                  type="text"
                  required
                  className="form-control"
                  value={formData.patientName}
                  onChange={(e) => setFormData({ ...formData, patientName: e.target.value })}
                  placeholder="e.g. John Doe"
                />
              </div>

              <div className="form-group">
                <label>Assigned Doctor *</label>
                <input
                  type="text"
                  required
                  className="form-control"
                  value={formData.doctorName}
                  onChange={(e) => setFormData({ ...formData, doctorName: e.target.value })}
                  placeholder="e.g. Dr. Sarah Jenkins"
                />
              </div>

              <div className="form-group">
                <label>Department / Ward</label>
                <select
                  className="form-control"
                  value={formData.department}
                  onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                >
                  <option value="CARDIOLOGY">Cardiology</option>
                  <option value="PEDIATRICS">Pediatrics</option>
                  <option value="NEUROLOGY">Neurology</option>
                  <option value="ONCOLOGY">Oncology</option>
                  <option value="GENERAL_MEDICINE">General Medicine</option>
                  <option value="DERMOLOGY">Dermatology</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Appointment Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.appointmentDate}
                    onChange={(e) => setFormData({ ...formData, appointmentDate: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Time Slot</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.appointmentTime}
                    onChange={(e) => setFormData({ ...formData, appointmentTime: e.target.value })}
                    placeholder="e.g. 10:30 AM"
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Status</label>
                <select
                  className="form-control"
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                >
                  <option value="SCHEDULED">Scheduled</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              </div>

              <div className="form-group">
                <label>Notes / Prescription Details</label>
                <textarea
                  rows="3"
                  className="form-control"
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  placeholder="Add details regarding current clinical symptoms or checks needed."
                ></textarea>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Schedule Consultation</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
