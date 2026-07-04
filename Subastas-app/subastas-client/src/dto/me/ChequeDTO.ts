export interface ChequeRequestDTO {
  identificacion: string;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
}

export interface ChequeResponseDTO {
  idCheque: number;
  idMedioPago?: number;
  tipo?: "CHEQUE" | string;
  identificacion: string;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
  estado?: string;
}
