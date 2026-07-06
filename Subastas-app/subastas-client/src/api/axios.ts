import { clearAuthData, getStoredToken } from "@/src/storage/authStorage";
import axios from "axios";

export const API_BASE_URL = "http://192.168.1.43:8083";

const api = axios.create({
  baseURL: API_BASE_URL,
});

api.interceptors.request.use(async (config) => {
  const token = await getStoredToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await clearAuthData();
    }

    return Promise.reject(error);
  },
);

export default api;