export interface CurrentUserDTO {
  idUsuario: number;
  nombre: string;
  apellido: string;
  mail: string;
  rol: string;
  estado: string;
  categoria: string | null;
  claveGenerada: boolean;
  requiereMedioDePago: boolean;
}