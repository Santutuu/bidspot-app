export interface CurrentUserDTO {
  idUsuario: number;
  nombre: string;
  apellido?: string;
  mail: string;
  rol: string;
  estado: string;
}