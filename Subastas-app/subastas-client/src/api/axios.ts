import axios from "axios";

const api = axios.create({
  baseURL: "http://192.168.1.43:8083",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;