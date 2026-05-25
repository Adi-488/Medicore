import React, { useState, useEffect } from 'react';
import { BillService } from '../services/api';
import { Receipt, Plus, Edit2, Trash2, X, IndianRupee } from 'lucide-react';

export default function Billing() {
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');
  const [patientFilter, setPatientFilter] = useState('');
  
  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingBill, setEditingBill] = useState(null);
  
  // Toast notifications
  const [toast, setToast] = useState(null);
  
  // Form fields
  const [formData, setFormData] = useState({
    patientName: '',
    description: '',
    amount: '',
    billDate: new Date().toISOString().split('T')[0],
    status: 'PENDING',
    paymentMethod: 'CASH'
  });

  useEffect(() => {
    loadBills();
  }, [statusFilter, patientFilter]);

  async function loadBills() {
    try {
      setLoading(true);
      const res = await BillService.getAll(statusFilter, patientFilter);
      setBills(res.data);
    } catch (err) {
      showToast('Error', 'Failed to retrieve billing invoices.');
    } finally {
      setLoading(false);
    }
  }

  function showToast(type, text) {
    setToast({ type, text });
    setTimeout(() => setToast(null), 3000);
  }

  function openCreateModal() {
    setEditingBill(null);
    setFormData({
      patientName: '',
      description: '',
      amount: '',
      billDate: new Date().toISOString().split('T')[0],
      status: 'PENDING',
      paymentMethod: 'CASH'
    });
    setShowModal(true);
  }

  function openEditModal(bill) {
    setEditingBill(bill);
    setFormData({
      patientName: bill.patientName || '',
      description: bill.description || '',
      amount: bill.amount || '',
      billDate: bill.billDate || new Date().toISOString().split('T')[0],
      status: bill.status || 'PENDING',
      paymentMethod: bill.paymentMethod || 'CASH'
    });
    setShowModal(true);
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to delete this invoice?')) return;
    try {
      await BillService.delete(id);
      showToast('Success', 'Invoice deleted successfully');
      loadBills();
    } catch (err) {
      showToast('Error', 'Failed to delete invoice.');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      if (!formData.patientName || !formData.amount) {
        showToast('Error', 'Please fill in required fields (Patient Name, Amount)');
        return;
      }

      const payload = {
        ...formData,
        amount: parseFloat(formData.amount)
      };

      if (editingBill) {
        await BillService.update(editingBill.id, payload);
        showToast('Success', 'Invoice updated successfully');
      } else {
        await BillService.create(payload);
        showToast('Success', 'Invoice created successfully');
      }
      setShowModal(false);
      loadBills();
    } catch (err) {
      console.error(err);
      showToast('Error', 'Failed to save billing record.');
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
          <h1>Billing & Financial Invoices</h1>
          <p>Generate bills, record check payments, and track revenue streams</p>
        </div>
        <button className="btn btn-primary" onClick={openCreateModal}>
          <Plus size={18} />
          <span>New Invoice</span>
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
              <option value="PAID">Paid</option>
              <option value="PENDING">Pending</option>
              <option value="UNPAID">Unpaid</option>
            </select>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <label style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 'bold', textTransform: 'uppercase' }}>Patient Search</label>
            <input
              type="text"
              placeholder="Search by patient name..."
              value={patientFilter}
              onChange={(e) => setPatientFilter(e.target.value)}
              className="form-control"
              style={{ width: '250px' }}
            />
          </div>
          
          <div style={{ alignSelf: 'flex-end' }}>
            {(statusFilter || patientFilter) && (
              <button className="btn btn-secondary" onClick={() => { setStatusFilter(''); setPatientFilter(''); loadBills(); }}>
                Clear Filters
              </button>
            )}
          </div>

        </div>
      </div>

      {/* Bills Table */}
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Patient Name</th>
              <th>Description / Particulars</th>
              <th>Date</th>
              <th>Method</th>
              <th>Amount</th>
              <th>Status</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                  Loading billing records...
                </td>
              </tr>
            ) : bills.length === 0 ? (
              <tr>
                <td colSpan="8" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
                  No billing transactions found.
                </td>
              </tr>
            ) : (
              bills.map((b) => (
                <tr key={b.id}>
                  <td style={{ fontWeight: 'bold', color: 'var(--text-muted)' }}>#{b.id}</td>
                  <td style={{ fontWeight: '700' }}>{b.patientName}</td>
                  <td style={{ color: 'var(--text-secondary)' }}>{b.description || 'General Treatment'}</td>
                  <td>{b.billDate}</td>
                  <td>
                    <span className="badge badge-outpatient" style={{ fontSize: '0.7rem' }}>{b.paymentMethod}</span>
                  </td>
                  <td style={{ fontWeight: '700', color: 'var(--text-primary)' }}>
                    ₹{b.amount.toLocaleString('en-IN')}
                  </td>
                  <td>
                    <span className={`badge badge-${b.status.toLowerCase()}`}>
                      {b.status}
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                      <button className="btn btn-secondary" style={{ padding: '0.4rem' }} onClick={() => openEditModal(b)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-danger" style={{ padding: '0.4rem' }} onClick={() => handleDelete(b.id)}>
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

      {/* Add / Edit Bill Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2 className="modal-title">{editingBill ? 'Edit Invoice Record' : 'Create Patient Invoice'}</h2>
              <button className="btn btn-secondary" style={{ padding: '0.3rem' }} onClick={() => setShowModal(false)}>
                <X size={18} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Patient Full Name *</label>
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
                <label>Invoice Description *</label>
                <input
                  type="text"
                  required
                  className="form-control"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="e.g. General Ward charges, Surgery fee"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Total Amount (₹) *</label>
                  <div style={{ position: 'relative' }}>
                    <input
                      type="number"
                      required
                      className="form-control"
                      value={formData.amount}
                      onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                      placeholder="e.g. 5000"
                    />
                  </div>
                </div>
                <div className="form-group">
                  <label>Billing Date</label>
                  <input
                    type="date"
                    className="form-control"
                    value={formData.billDate}
                    onChange={(e) => setFormData({ ...formData, billDate: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Payment Status</label>
                  <select
                    className="form-control"
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="PENDING">Pending</option>
                    <option value="PAID">Paid</option>
                    <option value="UNPAID">Unpaid</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Payment Method</label>
                  <select
                    className="form-control"
                    value={formData.paymentMethod}
                    onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
                  >
                    <option value="CASH">Cash</option>
                    <option value="CARD">Debit / Credit Card</option>
                    <option value="UPI">UPI Transfer</option>
                    <option value="INSURANCE">Medical Insurance</option>
                  </select>
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Generate Invoice</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
