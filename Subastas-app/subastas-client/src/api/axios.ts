import { clearAuthData, getStoredToken } from "@/src/storage/authStorage";
import axios from "axios";

const api = axios.create({
  baseURL: "https://1c74-2800-250a-d4-d27-d5f8-cffa-f161-7f4e.ngrok-free.app",
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