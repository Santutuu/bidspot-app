export interface TarjetaRequestDTO {
  numero: string;
  nombre: string;
  fechaVto: string;
  cvv: string;
}

export interface TarjetaResponseDTO {
  idMedioPago: number;
  tipo: "TARJETA" | string;
  numero: string;
  nombre: string;
  fechaVto: string;
}