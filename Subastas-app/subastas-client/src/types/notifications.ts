export type NotificationKind =
  | "PUBLICACION_ACEPTADA"
  | "PUBLICACION_RECHAZADA"
  | "PUJA_SUPERADA"
  | "SUBASTA_GANADA";

export type AppNotification = {
  id: string;
  solicitudId?: number;
  subastaId?: number;
  kind: NotificationKind;
  title: string;
  body: string;
  createdAt: string;
  read: boolean;
  actionLabel?: string;
  actionPath?: string;
};
