import axios from 'axios';

const API_BASE = '/'; // proxied by Vite to Spring Boot localhost:8080

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ── Auth ─────────────────────────────────────────────────────────────────────
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

// ── Farms ────────────────────────────────────────────────────────────────────
export const farmAPI = {
  create: (data) => api.post('/api/farms', data),
  getAll: () => api.get('/api/farms'),
  getById: (id) => api.get(`/api/farms/${id}`),
};

// ── Plots ────────────────────────────────────────────────────────────────────
export const plotAPI = {
  create: (data) => api.post('/api/plots', data),
  getByFarm: (farmId) => api.get(`/api/plots?farmId=${farmId}`),
  getById: (id) => api.get(`/api/plots/${id}`),
};

// ── Field Conditions ─────────────────────────────────────────────────────────
export const conditionAPI = {
  add: (plotId, data) => api.post(`/api/plots/${plotId}/conditions`, data),
  getAll: (plotId) => api.get(`/api/plots/${plotId}/conditions`),
};

// ── Predictions ──────────────────────────────────────────────────────────────
export const predictionAPI = {
  predict: (plotId) => api.post(`/api/plots/${plotId}/predict-yield`),
  getAll: (plotId) => api.get(`/api/plots/${plotId}/predictions`),
};

// ── Recommendations ──────────────────────────────────────────────────────────
export const recommendationAPI = {
  generate: (plotId) => api.post(`/api/plots/${plotId}/recommendations`),
  getAll: (plotId) => api.get(`/api/plots/${plotId}/recommendations`),
};

// ── Weather ──────────────────────────────────────────────────────────────────
export const weatherAPI = {
  getCurrent: (lat, lon) => api.get(`/api/weather/current?lat=${lat}&lon=${lon}`),
};

export default api;
