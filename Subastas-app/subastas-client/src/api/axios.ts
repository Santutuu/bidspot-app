import { clearAuthData, getStoredToken } from "@/src/storage/authStorage";
import axios from "axios";

const api = axios.create({
  baseURL: "http://192.168.1.43:8083",
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
  }
);

export default api;