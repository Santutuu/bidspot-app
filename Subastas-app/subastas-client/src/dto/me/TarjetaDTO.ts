export interface TarjetaRequestDTO {
  numero: string;
  nombre: string;
  fechaVto: string;
  cvv: string;
  principal?: boolean;
}

export interface TarjetaResponseDTO {
  idTarjeta: number;
  idMedioPago?: number;
  tipo?: "TARJETA" | string;
  numeroEnmascarado: string;
  numero?: string;
  nombre: string;
  fechaVto: string;
  principal?: boolean;
}
