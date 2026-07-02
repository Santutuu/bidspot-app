import { PublicacionMock } from "@/src/types/publicaciones";

const obra = require("@/assets/images/obras_arte.jpg");
const vehiculo = require("@/assets/images/white-old-vehicle.jpg");
const moto = require("@/assets/images/moto.webp");

export const publicacionesMock: PublicacionMock[] = [
  {
    id: "pub-001",
    titulo: "Guitarra antigua de colección",
    descripcion:
      "Pieza única de 1910, conservada en estado original y con detalles de madera tallada.",
    categoria: "Arte",
    estado: "EN_REVISION",
    explicacionEstado:
      "La empresa está revisando documentación y condiciones para publicar el item.",
    imagenes: [obra, vehiculo, moto],
    ubicacionDeposito: "Las Heras 2233, CABA",
    fechaRecepcion: "12/04/2026",
    acciones: [
      {
        id: "act-001",
        tipo: "ENVIAR_ITEM",
        titulo: "Enviar item",
        descripcion: "Se requiere enviar el item a depósito para inspección.",
        estado: "PENDIENTE",
      },
      {
        id: "act-002",
        tipo: "COMPROBAR_ORIGEN_LICITO",
        titulo: "Comprobar origen lícito",
        descripcion: "Adjuntá documentación respaldatoria del origen del item.",
        estado: "PENDIENTE",
      },
    ],
  },
  {
    id: "pub-002",
    titulo: "Reloj antiguo de bolsillo",
    descripcion:
      "Reloj mecánico con caja de plata, funcionando y listo para inspección.",
    categoria: "Joyas",
    estado: "ACEPTADA",
    explicacionEstado:
      "El item fue aprobado y puede avanzar a condiciones de venta.",
    imagenes: [vehiculo, obra],
    precioInicial: 1200,
    ubicacionDeposito: "Depósito central - CABA",
    fechaRecepcion: "08/05/2026",
    subasta: {
      titulo: "Subasta de objetos históricos",
      fecha: "15/07/2026",
      hora: "19:00",
      lugar: "Salón Don Felipe",
      valorBase: 1200,
      comision: "12%",
    },
    poliza: {
      empresa: "Río Seguros",
      numero: "POL-88201",
      cobertura: 4000,
      prima: 180,
      estado: "Recomendada",
    },
    acciones: [
      {
        id: "act-003",
        tipo: "PROPUESTA_COLECCION",
        titulo: "Propuesta colección",
        descripcion: "La empresa sugiere agrupar este producto como colección.",
        estado: "PENDIENTE",
      },
      {
        id: "act-004",
        tipo: "ACEPTAR_POLIZA",
        titulo: "Póliza de subasta",
        descripcion: "Revisá y aceptá la póliza recomendada para el item.",
        estado: "PENDIENTE",
      },
      {
        id: "act-005",
        tipo: "ACEPTAR_CONDICIONES_VENTA",
        titulo: "Condiciones de venta",
        descripcion: "Aceptá valor base y comisión sugeridos para la subasta.",
        estado: "PENDIENTE",
      },
    ],
  },
  {
    id: "pub-003",
    titulo: "Cámara vintage",
    descripcion: "Cámara analógica con lente original y estuche.",
    categoria: "Otros",
    estado: "RECHAZADA",
    explicacionEstado:
      "La publicación fue rechazada por no cumplir las condiciones mínimas.",
    imagenes: [moto],
    motivoRechazo: "No se pudo validar la procedencia del item.",
    acciones: [],
  },
  {
    id: "pub-004",
    titulo: "Escultura de bronce",
    descripcion: "Escultura firmada, base de mármol y certificado de origen.",
    categoria: "Arte",
    estado: "VENDIDA",
    explicacionEstado: "La publicación ya finalizó su recorrido.",
    imagenes: [obra],
    precioFinal: 2800,
    acciones: [],
  },
  {
    id: "pub-005",
    titulo: "Campera de cuero",
    descripcion: "Campera de cuero de los años 20.",
    categoria: "Ropa",
    estado: "PENDIENTE",
    explicacionEstado: "La empresa todavía no revisó esta publicación.",
    imagenes: [vehiculo],
    acciones: [],
  },
  {
    id: "pub-006",
    titulo: "Moto de colección",
    descripcion: "Moto restaurada y en condiciones de exhibición.",
    categoria: "Vehículos",
    estado: "CANCELADA",
    explicacionEstado: "La publicación fue cancelada.",
    imagenes: [moto],
    acciones: [],
  },
];

export function getPublicacionMock(id?: string) {
  return publicacionesMock.find((publicacion) => publicacion.id === id);
}
