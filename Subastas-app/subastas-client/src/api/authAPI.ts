import api from "@/src/api/axios";

import { AuthResponseDTO } from "@/src/dto/auth/AuthResponseDTO";
import { AuthUser } from "@/src/dto/auth/AuthUser";
import { CompleteRegistrationRequestDTO } from "@/src/dto/auth/CompleteRegistrationRequestDTO";
import { CurrentUserDTO } from "@/src/dto/auth/CurrentUserDTO";
import { LoginRequestDTO } from "@/src/dto/auth/LoginRequestDTO";
import { PreRegisterRequestDTO } from "@/src/dto/auth/PreRegisterRequestDTO";
import { PreRegisterResponseDTO } from "@/src/dto/auth/PreRegisterResponseDTO";
import { RegistrationStatusDTO } from "@/src/dto/auth/RegistrationStatusDTO";

export async function preRegisterUser(
  request: PreRegisterRequestDTO
): Promise<PreRegisterResponseDTO> {
  const response = await api.post<PreRegisterResponseDTO>(
    "/auth/pre-register",
    request
  );

  return response.data;
}

export async function getRegistrationStatus(
  mail: string
): Promise<RegistrationStatusDTO> {
  const response = await api.get<RegistrationStatusDTO>(
    "/auth/registration-status",
    {
      params: { mail },
    }
  );

  return response.data;
}

export async function completeRegistration(
  request: CompleteRegistrationRequestDTO
): Promise<AuthResponseDTO> {
  const response = await api.post<AuthResponseDTO>(
    "/auth/complete-registration",
    request
  );

  return response.data;
}

export async function loginUser(
  request: LoginRequestDTO
): Promise<AuthResponseDTO> {
  const response = await api.post<AuthResponseDTO>("/auth/login", request);

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
    categoria: response.data.categoria ?? null,
    claveGenerada: response.data.claveGenerada,
    requiereMedioDePago: response.data.requiereMedioDePago,
  };
}