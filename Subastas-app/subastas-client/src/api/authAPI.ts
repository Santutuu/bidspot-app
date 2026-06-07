import api from "./axios";

import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";
import { LoginRequestDTO } from "@/src/dto/auth/LoginRequestDTO";
import { RegisterRequestDTO } from "@/src/dto/auth/RegisterRequestDTO";

export async function loginUser(
  request: LoginRequestDTO
): Promise<AuthResponseDTO> {
  const response = await api.post<AuthResponseDTO>("/auth/login", request);

  return response.data;
}

export async function registerUser(
  request: RegisterRequestDTO
): Promise<AuthResponseDTO> {
  const response = await api.post<AuthResponseDTO>("/auth/register", request);

  return response.data;
}