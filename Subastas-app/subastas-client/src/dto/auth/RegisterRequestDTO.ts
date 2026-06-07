export interface RegisterRequestDTO {
  nombre: string;
  apellido: string;
  mail: string;
  password: string;

  frenteDNIUrl: string;
  dorsoDNIUrl: string;

  domicilio: {
    provincia: string;
    ciudad: string;
    cp: string;
    direccion: string;
  };
}