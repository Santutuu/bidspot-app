export interface AuthUser {
  idUsuario: number;
  nombre: string;
  mail: string;
  rol: string;
  estado: string;
  categoria: string | null;
  claveGenerada?: boolean;
  requiereMedioDePago?: boolean;
}