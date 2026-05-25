import axios from 'axios';

// During development, Vite proxy maps /api to http://localhost:8080.
// In production, we default to the live deployed Google Cloud Run API URL.
const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://medicore-api-573092816496.asia-south1.run.app/api';
const AUTH_API_BASE_URL = import.meta.env.VITE_AUTH_API_URL || 'https://auth-service-573092816496.asia-south1.run.app/api/auth';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to inject JWT token automatically for secure monolith API calls
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

const authApi = axios.create({
  baseURL: AUTH_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const AuthService = {
  login: (username, password) => authApi.post('/login', { username, password }),
  register: (username, password, email, role) => authApi.post('/register', { username, password, email, role }),
  validate: (token) => authApi.get('/validate', { params: { token } }),
};

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
