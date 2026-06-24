export interface AuthResponseDTO {
  token: string;
  idUsuario: number;
  nombre: string;
  mail: string;
  rol: string;
  estado: string;
  categoria: string | null;
}