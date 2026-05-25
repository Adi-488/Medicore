import React, { useState, useEffect } from 'react';
import { PatientService } from '../services/api';
import { Search, UserPlus, Edit2, Trash2, X } from 'lucide-react';

export default function Patients() {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingPatient, setEditingPatient] = useState(null);
  
  // Toast notifications
  const [toast, setToast] = useState(null);
  
  // Form fields
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'MALE',
    phone: '',
    email: '',
    address: '',
    patientType: 'OUTPATIENT',
    bedNumber: '',
    status: 'ACTIVE',
    diagnosis: '',
    admittedDate: new Date().toISOString().split('T')[0],
    dischargedDate: ''
  });

  useEffect(() => {
    loadPatients();
  }, [statusFilter, typeFilter]);

  async function loadPatients() {
    try {
      setLoading(true);
      const res = await PatientService.getAll(statusFilter, typeFilter);
      setPatients(res.data);
    } catch (err) {
      showToast('Error', 'Failed to retrieve patient listings.');
    } finally {
      setLoading(false);
    }
  }

  function showToast(type, text) {
    setToast({ type, text });
    setTimeout(() => setToast(null), 3000);
  }

  async function handleSearch(e) {
    e.preventDefault();
    if (!searchQuery.trim()) {
      loadPatients();
      return;
    }
    try {
      setLoading(true);
      const res = await PatientService.search(searchQuery);
      setPatients(res.data);
    } catch (err) {
      showToast('Error', 'Search request failed.');
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingPatient(null);
    setFormData({
      name: '',
      age: '',
      gender: 'MALE',
      phone: '',
      email: '',
      address: '',
      patientType: 'OUTPATIENT',
      bedNumber: '',
      status: 'ACTIVE',
      diagnosis: '',
      admittedDate: new Date().toISOString().split('T')[0],
      dischargedDate: ''
    });
    setShowModal(true);
  }

  function openEditModal(patient) {
    setEditingPatient(patient);
    setFormData({
      name: patient.name || '',
      age: patient.age || '',
      gender: patient.gender || 'MALE',
      phone: patient.phone || '',
      email: patient.email || '',
      address: patient.address || '',
      patientType: patient.patientType || 'OUTPATIENT',
      bedNumber: patient.bedNumber || '',
      status: patient.status || 'ACTIVE',
      diagnosis: patient.diagnosis || '',
      admittedDate: patient.admittedDate || new Date().toISOString().split('T')[0],
      dischargedDate: patient.dischargedDate || ''
    });
    setShowModal(true);
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to remove this patient record?')) return;
    try {
      await PatientService.delete(id);
      showToast('Success', 'Patient deleted successfully');
      loadPatients();
    } catch (err) {
      showToast('Error', 'Failed to delete patient. Ensure there are no dependent records.');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      // Basic validation
      if (!formData.name || !formData.age || !formData.phone) {
        showToast('Error', 'Please fill in all required fields (Name, Age, Phone)');
        return;
      }
      
      const parsedAge = parseInt(formData.age, 10);
      if (isNaN(parsedAge) || parsedAge < 0) {
        showToast('Error', 'Age must be a positive number (0 or greater)');
        return;
      }
      
      if (parsedAge > 150) {
        showToast('Error', 'Age must be realistic (150 or less)');
        return;
      }

      if (!formData.phone.trim()) {
        showToast('Error', 'Phone number is required');
        return;
      }
      
      const payload = {
        ...formData,
        age: parsedAge,
      };

      if (editingPatient) {
        await PatientService.update(editingPatient.id, payload);
        showToast('Success', 'Patient updated successfully');
      } else {
        await PatientService.create(payload);
        showToast('Success', 'Patient registered successfully');
      }
      setShowModal(false);
      loadPatients();
    } catch (err) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        showToast('Error', err.response.data.message);
      } else {
        showToast('Error', 'Failed to save patient database entry.');
      }
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
          <h1>Patient Admissions & Records</h1>
          <p>Register, update, and manage patients registered in HMS</p>
        </div>
        <button className="btn btn-primary" onClick={openCreateModal}>
          <UserPlus size={18} />
          <span>Register Patient</span>
        </button>
      </div>

      {/* Filters Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'center', justifyContent: 'space-between' }}>
          
          {/* Search */}
          <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem', flex: 1, minWidth: '280px' }}>
            <div style={{ position: 'relative', flex: 1 }}>
              <Search size={16} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input
                type="text"
                placeholder="Search patient name..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="form-control"
                style={{ paddingLeft: '2.5rem' }}
              />
            </div>
            <button type="submit" className="btn btn-secondary">Search</button>
          </form>

          {/* Filter Types */}
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <select
              className="form-control"
              style={{ width: '150px' }}
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
            >
              <option value="">All Types</option>
              <option value="INPATIENT">Inpatient</option>
              <option value="OUTPATIENT">Outpatient</option>
            </select>

            <select
              className="form-control"
              style={{ width: '150px' }}
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="ADMITTED">Admitted</option>
              <option value="DISCHARGED">Discharged</option>
            </select>
            
            {(typeFilter || statusFilter || searchQuery) && (
              <button className="btn btn-secondary" onClick={() => { setTypeFilter(''); setStatusFilter(''); setSearchQuery(''); loadPatients(); }}>
                Clear Filters
              </button>
            )}
          </div>

        </div>
      </div>

      {/* Patients Table */}
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Gender / Age</th>
              <th>Contact</th>
              <th>Type</th>
              <th>Status</th>
              <th>Bed</th>
              <th>Admitted</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                  Loading patient listings...
                </td>
              </tr>
            ) : patients.length === 0 ? (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                  No patients found matching the criteria.
                </td>
              </tr>
            ) : (
              patients.map((p) => (
                <tr key={p.id}>
                  <td style={{ fontWeight: 'bold', color: 'var(--text-muted)' }}>#{p.id}</td>
                  <td>
                    <div style={{ fontWeight: '700' }}>{p.name}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{p.diagnosis || 'No diagnosis'}</div>
                  </td>
                  <td>{p.gender} / {p.age} yrs</td>
                  <td>
                    <div>{p.phone}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{p.email}</div>
                  </td>
                  <td>
                    <span className={`badge badge-${p.patientType.toLowerCase()}`}>
                      {p.patientType}
                    </span>
                  </td>
                  <td>
                    <span className={`badge badge-${p.status.toLowerCase()}`}>
                      {p.status}
                    </span>
                  </td>
                  <td>{p.bedNumber || 'N/A'}</td>
                  <td>{p.admittedDate}</td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                      <button className="btn btn-secondary" style={{ padding: '0.4rem' }} onClick={() => openEditModal(p)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-danger" style={{ padding: '0.4rem' }} onClick={() => handleDelete(p.id)}>
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

      {/* Add / Edit Patient Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2 className="modal-title">{editingPatient ? 'Edit Patient Record' : 'Register New Patient'}</h2>
              <button className="btn btn-secondary" style={{ padding: '0.3rem' }} onClick={() => setShowModal(false)}>
                <X size={18} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Full Name *</label>
                <input
                  type="text"
                  required
                  className="form-control"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g. John Doe"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Age *</label>
                  <input
                    type="number"
                    required
                    className="form-control"
                    value={formData.age}
                    onChange={(e) => setFormData({ ...formData, age: e.target.value })}
                    placeholder="e.g. 35"
                  />
                </div>
                <div className="form-group">
                  <label>Gender</label>
                  <select
                    className="form-control"
                    value={formData.gender}
                    onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                  >
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Phone Number *</label>
                  <input
                    type="text"
                    required
                    className="form-control"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    placeholder="e.g. 9876543210"
                  />
                </div>
                <div className="form-group">
                  <label>Email Address</label>
                  <input
                    type="email"
                    className="form-control"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    placeholder="e.g. email@hospital.com"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Patient Type</label>
                  <select
                    className="form-control"
                    value={formData.patientType}
                    onChange={(e) => setFormData({ ...formData, patientType: e.target.value })}
                  >
                    <option value="INPATIENT">Inpatient</option>
                    <option value="OUTPATIENT">Outpatient</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Bed / Room Number</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.bedNumber}
                    onChange={(e) => setFormData({ ...formData, bedNumber: e.target.value })}
                    placeholder="e.g. ICU-3"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="ACTIVE">Active</option>
                    <option value="ADMITTED">Admitted</option>
                    <option value="DISCHARGED">Discharged</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Admission Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.admittedDate}
                    onChange={(e) => setFormData({ ...formData, admittedDate: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Diagnosis / Clinical Complaint</label>
                <input
                  type="text"
                  className="form-control"
                  value={formData.diagnosis}
                  onChange={(e) => setFormData({ ...formData, diagnosis: e.target.value })}
                  placeholder="e.g. Acute hypertension"
                />
              </div>

              <div className="form-group">
                <label>Residential Address</label>
                <textarea
                  rows="2"
                  className="form-control"
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  placeholder="Address details"
                ></textarea>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save Patient</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
