import {
  obtenerDetalleSolicitudPublicacion,
  obtenerMisSolicitudesPublicacion,
} from "@/src/api/solicitudesPublicacionAPI";
import { getEstadoPuja } from "@/src/api/subastaAPI";
import { useAuth } from "@/src/context/authContext";
import { BidUpdateEvent, useRealtime } from "@/src/context/realtimeContext";
import {
  addDismissedNotificationId,
  getDismissedNotificationIds,
  getStoredNotifications,
  getWatchedBids,
  saveNotifications,
  saveWatchedBids,
  upsertWatchedBid,
  WatchedBid,
} from "@/src/storage/notificationsStorage";
import { AppNotification } from "@/src/types/notifications";
import { SolicitudPublicacionResumen } from "@/src/types/solicitudesPublicacion";
import { usePathname } from "expo-router";
import {
    createContext,
    ReactNode,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useState,
} from "react";

type NotificationsContextType = {
  notifications: AppNotification[];
  unreadCount: number;
  isPanelOpen: boolean;
  syncNotifications: () => Promise<void>;
  addLocalNotification: (
    notification: Omit<AppNotification, "id" | "createdAt" | "read"> & {
      id?: string;
    },
  ) => Promise<void>;
  watchBidNotification: (
    bid: Omit<WatchedBid, "createdAt">,
  ) => Promise<void>;
  markAllAsRead: () => Promise<void>;
  dismissNotification: (notificationId: string) => Promise<void>;
  togglePanel: () => Promise<void>;
  closePanel: () => void;
};

const NotificationsContext = createContext<NotificationsContextType>(
  {} as NotificationsContextType,
);

function buildAcceptedMessage(
  title: string,
  ubicacionDeposito?: string | null,
) {
  const ubicacion =
    ubicacionDeposito?.trim() || "la ubicación informada por la empresa";

  return `Nos complace informarle que su artículo [${title}] ha sido aceptado.\n\nPor favor, envíelo a ${ubicacion} para su inspección física. Tenga en cuenta que, si el bien no supera la revisión, será devuelto con costos de envío a su cargo.`;
}

function buildRejectedMessage(title: string, motivoRechazo?: string | null) {
  if (motivoRechazo?.trim()) {
    return `Lamentamos informarle que su artículo [${title}] no ha sido aceptado.\n\nMotivo: ${motivoRechazo}`;
  }

  return `Lamentamos informarle que su artículo [${title}] no ha sido aceptado.`;
}

async function createAcceptedNotification(
  solicitud: SolicitudPublicacionResumen,
): Promise<AppNotification> {
  let ubicacionDeposito: string | null = null;

  try {
    const detalle = await obtenerDetalleSolicitudPublicacion(
      solicitud.idSolicitud,
    );
    ubicacionDeposito = detalle.ubicacionDeposito;
  } catch {
    ubicacionDeposito = null;
  }

  return {
    id: `solicitud-${solicitud.idSolicitud}-ACEPTADA`,
    solicitudId: solicitud.idSolicitud,
    kind: "PUBLICACION_ACEPTADA",
    title: "Solicitud aceptada",
    body: buildAcceptedMessage(solicitud.titulo, ubicacionDeposito),
    createdAt: new Date().toISOString(),
    read: false,
  };
}

async function createRejectedNotification(
  solicitud: SolicitudPublicacionResumen,
): Promise<AppNotification> {
  let motivoRechazo: string | null = null;

  try {
    const detalle = await obtenerDetalleSolicitudPublicacion(
      solicitud.idSolicitud,
    );
    motivoRechazo = detalle.motivoRechazo;
  } catch {
    motivoRechazo = null;
  }

  return {
    id: `solicitud-${solicitud.idSolicitud}-RECHAZADA`,
    solicitudId: solicitud.idSolicitud,
    kind: "PUBLICACION_RECHAZADA",
    title: "Solicitud rechazada",
    body: buildRejectedMessage(solicitud.titulo, motivoRechazo),
    createdAt: new Date().toISOString(),
    read: false,
  };
}

export function NotificationsProvider({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const { user, isAuthenticated } = useAuth();
  const { subscribeToAuctionBids, onReconnect } = useRealtime();
  const [notifications, setNotifications] = useState<AppNotification[]>([]);
  const [watchedBids, setWatchedBids] = useState<WatchedBid[]>([]);
  const [isPanelOpen, setIsPanelOpen] = useState(false);

  useEffect(() => {
    setIsPanelOpen(false);
    setNotifications([]);

    async function loadStored() {
      if (!isAuthenticated || !user?.idUsuario) {
        return;
      }

      const stored = await getStoredNotifications(user.idUsuario);
      const storedWatchedBids = await getWatchedBids(user.idUsuario);
      setNotifications(stored);
      setWatchedBids(storedWatchedBids);
    }

    void loadStored();
  }, [isAuthenticated, user?.idUsuario]);

  const syncNotifications = useCallback(async () => {
    if (!isAuthenticated || !user?.idUsuario) {
      return;
    }

    try {
      const solicitudes = await obtenerMisSolicitudesPublicacion();
      const stored = await getStoredNotifications(user.idUsuario);
      const dismissedIds = new Set(
        await getDismissedNotificationIds(user.idUsuario),
      );
      const next = [...stored];
      const existingIds = new Set(stored.map((item) => item.id));

      for (const solicitud of solicitudes) {
        if (solicitud.estado === "ACEPTADA") {
          const id = `solicitud-${solicitud.idSolicitud}-ACEPTADA`;

          if (!existingIds.has(id) && !dismissedIds.has(id)) {
            next.unshift(await createAcceptedNotification(solicitud));
            existingIds.add(id);
          }
        }

        if (solicitud.estado === "RECHAZADA") {
          const id = `solicitud-${solicitud.idSolicitud}-RECHAZADA`;

          if (!existingIds.has(id) && !dismissedIds.has(id)) {
            next.unshift(await createRejectedNotification(solicitud));
            existingIds.add(id);
          }
        }
      }

      next.sort((left, right) => right.createdAt.localeCompare(left.createdAt));

      setNotifications(next);
      await saveNotifications(user.idUsuario, next);
    } catch {
      return;
    }
  }, [isAuthenticated, user?.idUsuario]);

  const addLocalNotification = useCallback(
    async (
      notification: Omit<AppNotification, "id" | "createdAt" | "read"> & {
        id?: string;
      },
    ) => {
      if (!user?.idUsuario) {
        return;
      }

      const nextNotification: AppNotification = {
        ...notification,
        id:
          notification.id ??
          `${notification.kind}-${Date.now()}-${Math.random().toString(16).slice(2)}`,
        createdAt: new Date().toISOString(),
        read: false,
      };

      const next = [
        nextNotification,
        ...notifications.filter((item) => item.id !== nextNotification.id),
      ];

      setNotifications(next);
      await saveNotifications(user.idUsuario, next);
    },
    [notifications, user?.idUsuario],
  );

  const watchBidNotification = useCallback(
    async (bid: Omit<WatchedBid, "createdAt">) => {
      if (!user?.idUsuario) {
        return;
      }

      await upsertWatchedBid(user.idUsuario, {
        ...bid,
        createdAt: new Date().toISOString(),
      });
      setWatchedBids((current) => [
        { ...bid, createdAt: new Date().toISOString() },
        ...current.filter((item) => item.subastaId !== bid.subastaId),
      ]);
    },
    [user?.idUsuario],
  );

  const handleWatchedBidEvent = useCallback(
    async (event: BidUpdateEvent) => {
      if (!isAuthenticated || !user?.idUsuario) return;

      const storedWatchedBids = await getWatchedBids(user.idUsuario);
      const watchedBid = storedWatchedBids.find(
        (item) => item.subastaId === event.idSubasta,
      );

      if (!watchedBid) return;

      try {
        const estadoPuja = await getEstadoPuja(event.idSubasta);
        if (
          estadoPuja.miMejorOferta === null ||
          estadoPuja.miMejorOferta === undefined
        ) {
          return;
        }

        if (estadoPuja.soyMejorPostor) {
          const nextWatchedBids = storedWatchedBids.map((item) =>
            item.subastaId === watchedBid.subastaId
              ? { ...item, amount: estadoPuja.miMejorOferta ?? item.amount }
              : item,
          );
          setWatchedBids(nextWatchedBids);
          await saveWatchedBids(user.idUsuario, nextWatchedBids);
          return;
        }
      } catch {
        if (event.monto <= watchedBid.amount) {
          return;
        }
      }

      const stored = await getStoredNotifications(user.idUsuario);
      const dismissedIds = new Set(
        await getDismissedNotificationIds(user.idUsuario),
      );
      const notificationId = `puja-superada-${watchedBid.subastaId}-${watchedBid.amount}`;
      const nextNotifications = [...stored];

      if (
        !dismissedIds.has(notificationId) &&
        !nextNotifications.some((item) => item.id === notificationId)
      ) {
        nextNotifications.unshift({
          id: notificationId,
          kind: "PUJA_SUPERADA",
          title: "Oferta superada",
          body: `Tu oferta en ${watchedBid.title} fue sobrepasada por otra persona. Realizá otra puja antes de que se acabe el tiempo.`,
          createdAt: new Date().toISOString(),
          read: false,
          subastaId: watchedBid.subastaId,
          actionLabel: "Volver a subasta",
        });
        setIsPanelOpen(true);
      }

      nextNotifications.sort((left, right) =>
        right.createdAt.localeCompare(left.createdAt),
      );

      const remainingBids = storedWatchedBids.filter(
        (item) => item.subastaId !== watchedBid.subastaId,
      );

      setNotifications(nextNotifications);
      setWatchedBids(remainingBids);
      await saveNotifications(user.idUsuario, nextNotifications);
      await saveWatchedBids(user.idUsuario, remainingBids);
    },
    [isAuthenticated, user?.idUsuario],
  );

  const syncWatchedBidNotifications = useCallback(async () => {
    if (!isAuthenticated || !user?.idUsuario) {
      return;
    }

    try {
      const watchedBids = await getWatchedBids(user.idUsuario);

      if (watchedBids.length === 0) {
        return;
      }

      const stored = await getStoredNotifications(user.idUsuario);
      const dismissedIds = new Set(
        await getDismissedNotificationIds(user.idUsuario),
      );
      const nextNotifications = [...stored];
      const remainingBids: WatchedBid[] = [];
      let addedNotification = false;

      for (const watchedBid of watchedBids) {
        try {
          const estadoPuja = await getEstadoPuja(watchedBid.subastaId);

          if (
            estadoPuja.miMejorOferta !== null &&
            estadoPuja.miMejorOferta !== undefined &&
            !estadoPuja.soyMejorPostor
          ) {
            const notificationId = `puja-superada-${watchedBid.subastaId}-${watchedBid.amount}`;

            if (
              !dismissedIds.has(notificationId) &&
              !nextNotifications.some((item) => item.id === notificationId)
            ) {
              nextNotifications.unshift({
                id: notificationId,
                kind: "PUJA_SUPERADA",
                title: "Oferta superada",
                body: `Tu oferta en ${watchedBid.title} fue sobrepasada por otra persona. Realizá otra puja antes de que se acabe el tiempo.`,
                createdAt: new Date().toISOString(),
                read: false,
                subastaId: watchedBid.subastaId,
                actionLabel: "Volver a subasta",
              });
              addedNotification = true;
            }
          } else if (
            estadoPuja.miMejorOferta !== null &&
            estadoPuja.miMejorOferta !== undefined
          ) {
            remainingBids.push({
              ...watchedBid,
              amount: estadoPuja.miMejorOferta,
            });
          } else {
            remainingBids.push(watchedBid);
          }
        } catch {
          remainingBids.push(watchedBid);
        }
      }

      nextNotifications.sort((left, right) =>
        right.createdAt.localeCompare(left.createdAt),
      );

      setNotifications(nextNotifications);
      setWatchedBids(remainingBids);
      if (addedNotification) {
        setIsPanelOpen(true);
      }
      await saveNotifications(user.idUsuario, nextNotifications);
      await saveWatchedBids(user.idUsuario, remainingBids);
    } catch {
      return;
    }
  }, [isAuthenticated, user?.idUsuario]);

  useEffect(() => {
    void syncNotifications();
  }, [syncNotifications, pathname]);

  useEffect(() => {
    if (!isAuthenticated || !user?.idUsuario || watchedBids.length === 0) {
      return;
    }

    void syncWatchedBidNotifications();
    const intervalId = setInterval(() => {
      void syncWatchedBidNotifications();
    }, 5000);

    return () => clearInterval(intervalId);
  }, [
    isAuthenticated,
    syncWatchedBidNotifications,
    user?.idUsuario,
    watchedBids.length,
  ]);

  useEffect(() => {
    if (!isAuthenticated || !user?.idUsuario) {
      return;
    }

    return onReconnect(() => {
      void syncWatchedBidNotifications();
    });
  }, [isAuthenticated, onReconnect, syncWatchedBidNotifications, user?.idUsuario]);

  useEffect(() => {
    if (!isAuthenticated || !user?.idUsuario) {
      return;
    }

    const unsubscribers = watchedBids.map((watchedBid) =>
      subscribeToAuctionBids(watchedBid.subastaId, (event) => {
        void handleWatchedBidEvent(event);
      }),
    );

    return () => {
      unsubscribers.forEach((unsubscribe) => unsubscribe());
    };
  }, [
    handleWatchedBidEvent,
    isAuthenticated,
    subscribeToAuctionBids,
    user?.idUsuario,
    watchedBids,
  ]);

  const markAllAsRead = useCallback(async () => {
    if (!user?.idUsuario) {
      return;
    }

    const next = notifications.map((item) => ({ ...item, read: true }));
    setNotifications(next);
    await saveNotifications(user.idUsuario, next);
  }, [notifications, user?.idUsuario]);

  const dismissNotification = useCallback(
    async (notificationId: string) => {
      if (!user?.idUsuario) {
        return;
      }

      const next = notifications.filter((item) => item.id !== notificationId);
      setNotifications(next);
      await addDismissedNotificationId(user.idUsuario, notificationId);
      await saveNotifications(user.idUsuario, next);
    },
    [notifications, user?.idUsuario],
  );

  const closePanel = useCallback(() => {
    setIsPanelOpen(false);
  }, []);

  const togglePanel = useCallback(async () => {
    if (!isAuthenticated || !user?.idUsuario) {
      setIsPanelOpen(false);
      setNotifications([]);
      return;
    }

    const nextOpen = !isPanelOpen;
    setIsPanelOpen(nextOpen);

    if (nextOpen) {
      await markAllAsRead();
    }
  }, [isAuthenticated, isPanelOpen, markAllAsRead, user?.idUsuario]);

  const value = useMemo(
    () => ({
      notifications,
      unreadCount: notifications.filter((item) => !item.read).length,
      isPanelOpen,
      syncNotifications,
      addLocalNotification,
      watchBidNotification,
      markAllAsRead,
      dismissNotification,
      togglePanel,
      closePanel,
    }),
    [
      closePanel,
      dismissNotification,
      isPanelOpen,
      addLocalNotification,
      watchBidNotification,
      markAllAsRead,
      notifications,
      syncNotifications,
      togglePanel,
    ],
  );

  return (
    <NotificationsContext.Provider value={value}>
      {children}
    </NotificationsContext.Provider>
  );
}

export function useNotifications() {
  return useContext(NotificationsContext);
}
