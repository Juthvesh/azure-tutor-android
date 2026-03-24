import axios from 'axios';

const api = axios.create({
  baseURL: 'https://klntfd5w-5050.inc1.devtunnels.ms/',
});

// Add response interceptor for debugging
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export default api;
