export interface PreRegisterRequestDTO {
  nombre: string;
  apellido: string;
  documento: string;
  mail: string;

  frenteDNIUrl: string;
  dorsoDNIUrl: string;

  domicilio: {
    pais: string;
    provincia: string;
    ciudad: string;
    cp: string;
    direccion: string;
  };
}
