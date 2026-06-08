export interface CurrentUserDTO {
  idUsuario: number;
  nombre: string;
  apellido?: string;
  mail: string;
  rol: "USER" | "ADMIN" | string;
  estado:
    | "PENDIENTE_VALIDACION"
    | "VALIDADO"
    | "RECHAZADO"
    | "BLOQUEADO"
    | string;
}