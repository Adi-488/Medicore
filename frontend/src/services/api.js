import axios from 'axios';

// During development, Vite proxy maps /api to http://localhost:8080.
// In production, we default to the live deployed Google Cloud Run API URL.
const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://medicore-api-573092816496.asia-south1.run.app/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const DashboardService = {
  getStats: () => api.get('/dashboard/stats'),
};

export const PatientService = {
  getAll: (status, type) => {
    const params = {};
    if (status) params.status = status;
    if (type) params.type = type;
    return api.get('/patients', { params });
  },
  getById: (id) => api.get(`/patients/${id}`),
  search: (name) => api.get('/patients/search', { params: { name } }),
  create: (patientData) => api.post('/patients', patientData),
  update: (id, patientData) => api.put(`/patients/${id}`, patientData),
  delete: (id) => api.delete(`/patients/${id}`),
};

export const AppointmentService = {
  getAll: (status, doctor, date) => {
    const params = {};
    if (status) params.status = status;
    if (doctor) params.doctor = doctor;
    if (date) params.date = date;
    return api.get('/appointments', { params });
  },
  getById: (id) => api.get(`/appointments/${id}`),
  create: (appointmentData) => api.post('/appointments', appointmentData),
  update: (id, appointmentData) => api.put(`/appointments/${id}`, appointmentData),
  delete: (id) => api.delete(`/appointments/${id}`),
};

export const BillService = {
  getAll: (status, patient) => {
    const params = {};
    if (status) params.status = status;
    if (patient) params.patient = patient;
    return api.get('/bills', { params });
  },
  getById: (id) => api.get(`/bills/${id}`),
  create: (billData) => api.post('/bills', billData),
  update: (id, billData) => api.put(`/bills/${id}`, billData),
  delete: (id) => api.delete(`/bills/${id}`),
};

export default api;
