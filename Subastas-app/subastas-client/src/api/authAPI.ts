import api from "@/src/api/axios";

import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";
import { AuthUser } from "@/src/dto/auth/AuthUser";
import { CurrentUserDTO } from "@/src/dto/auth/CurrentUserDTO";
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

export async function getCurrentUser(): Promise<AuthUser> {
  const response = await api.get<CurrentUserDTO>("/auth/me");

  return {
    idUsuario: response.data.idUsuario,
    nombre: response.data.nombre,
    mail: response.data.mail,
    rol: response.data.rol,
    estado: response.data.estado,
  };
}