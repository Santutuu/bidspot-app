import api from "@/src/api/axios";

import {
  CuentaCobroRequestDTO,
  CuentaCobroResponseDTO,
} from "@/src/dto/me/CuentaCobroDTO";

import {
  TarjetaRequestDTO,
  TarjetaResponseDTO,
} from "@/src/dto/me/TarjetaDTO";

import {
  ChequeRequestDTO,
  ChequeResponseDTO,
} from "@/src/dto/me/ChequeDTO";

import { MedioPagoResponseDTO } from "@/src/dto/me/MedioPagoDTO";
import { normalizeMoneda } from "@/src/utils/moneda";

export async function crearCuentaCobro(
  request: CuentaCobroRequestDTO
): Promise<CuentaCobroResponseDTO> {
  const response = await api.post<CuentaCobroResponseDTO>(
    "/me/cuenta-cobro",
    request
  );

  return response.data;
}

export async function obtenerCuentaCobro(): Promise<CuentaCobroResponseDTO> {
  const response = await api.get<CuentaCobroResponseDTO>("/me/cuenta-cobro");
  return response.data;
}

export async function crearTarjeta(
  request: TarjetaRequestDTO
): Promise<TarjetaResponseDTO> {
  const response = await api.post<TarjetaResponseDTO>(
    "/me/medios-pago/tarjetas",
    {
      ...request,
      moneda: normalizeMoneda(request.moneda),
    }
  );

  return response.data;
}

export async function obtenerTarjetas(): Promise<TarjetaResponseDTO[]> {
  const response = await api.get<TarjetaResponseDTO[]>(
    "/me/medios-pago/tarjetas"
  );

  return response.data;
}

export async function eliminarTarjeta(idTarjeta: number): Promise<void> {
  await api.delete(`/me/medios-pago/tarjetas/${idTarjeta}`);
}

export async function crearCheque(
  request: ChequeRequestDTO
): Promise<ChequeResponseDTO> {
  const response = await api.post<ChequeResponseDTO>(
    "/me/medios-pago/cheques",
    {
      ...request,
      moneda: normalizeMoneda(request.moneda),
    }
  );

  return response.data;
}

export async function obtenerCheques(): Promise<ChequeResponseDTO[]> {
  const response = await api.get<ChequeResponseDTO[]>(
    "/me/medios-pago/cheques"
  );

  return response.data;
}

export async function eliminarCheque(idCheque: number): Promise<void> {
  await api.delete(`/me/medios-pago/cheques/${idCheque}`);
}

export async function obtenerMediosPago(): Promise<MedioPagoResponseDTO[]> {
  const response = await api.get<MedioPagoResponseDTO[]>("/me/medios-pago");
  return response.data;
}
