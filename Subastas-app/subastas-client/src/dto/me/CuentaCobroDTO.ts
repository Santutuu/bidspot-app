export interface CuentaCobroRequestDTO {
  cbu: string;
  banco: string;
  titular: string;
}

export interface CuentaCobroResponseDTO {
  idCuentaBanco: number;
  cbu: string;
  banco: string;
  titular: string;
}