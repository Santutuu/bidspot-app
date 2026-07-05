import { API_BASE_URL } from "@/src/api/axios";
import { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";

export interface BidUpdateEvent {
  idSubasta: number;
  idItemCatalogo: number;
  monto: number;
  moneda: "DOLARES" | "ARS" | "PESOS" | string;
  fechaHora: string;
}

type BidUpdateHandler = (event: BidUpdateEvent) => void;
type ReconnectHandler = () => void;

type RealtimeContextType = {
  connected: boolean;
  reconnectVersion: number;
  subscribeToAuctionBids: (
    idSubasta: number,
    handler: BidUpdateHandler,
  ) => () => void;
  onReconnect: (handler: ReconnectHandler) => () => void;
};

const RealtimeContext = createContext<RealtimeContextType>(
  {} as RealtimeContextType,
);

function getWebSocketUrl() {
  return API_BASE_URL.replace(/^https:/, "wss:")
    .replace(/^http:/, "ws:")
    .replace(/\/$/, "")
    .concat("/ws");
}

export function RealtimeProvider({ children }: { children: ReactNode }) {
  const clientRef = useRef<Client | null>(null);
  const subscriptionsRef = useRef<Map<number, StompSubscription>>(new Map());
  const handlersRef = useRef<Map<number, Set<BidUpdateHandler>>>(new Map());
  const reconnectHandlersRef = useRef<Set<ReconnectHandler>>(new Set());
  const hasConnectedRef = useRef(false);
  const [connected, setConnected] = useState(false);
  const [reconnectVersion, setReconnectVersion] = useState(0);

  const subscribeStompTopic = useCallback((idSubasta: number) => {
    const client = clientRef.current;

    if (!client?.connected || subscriptionsRef.current.has(idSubasta)) {
      return;
    }

    const subscription = client.subscribe(
      `/topic/subastas/${idSubasta}/pujas`,
      (message: IMessage) => {
        const handlers = handlersRef.current.get(idSubasta);
        if (!handlers || handlers.size === 0) return;

        try {
          const payload = JSON.parse(message.body) as BidUpdateEvent;
          handlers.forEach((handler) => handler(payload));
        } catch (error) {
          console.error("No pudimos procesar el evento de puja:", error);
        }
      },
    );

    subscriptionsRef.current.set(idSubasta, subscription);
  }, []);

  const resubscribeAll = useCallback(() => {
    handlersRef.current.forEach((_, idSubasta) => {
      subscribeStompTopic(idSubasta);
    });
  }, [subscribeStompTopic]);

  useEffect(() => {
    const client = new Client({
      brokerURL: getWebSocketUrl(),
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        subscriptionsRef.current.clear();
        resubscribeAll();

        if (hasConnectedRef.current) {
          setReconnectVersion((current) => current + 1);
          reconnectHandlersRef.current.forEach((handler) => handler());
        }

        hasConnectedRef.current = true;
      },
      onWebSocketClose: () => {
        setConnected(false);
        subscriptionsRef.current.clear();
      },
      onStompError: () => {
        setConnected(false);
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      subscriptionsRef.current.forEach((subscription) =>
        subscription.unsubscribe(),
      );
      subscriptionsRef.current.clear();
      void client.deactivate();
      clientRef.current = null;
    };
  }, [resubscribeAll]);

  const subscribeToAuctionBids = useCallback(
    (idSubasta: number, handler: BidUpdateHandler) => {
      const handlers = handlersRef.current.get(idSubasta) ?? new Set();
      handlers.add(handler);
      handlersRef.current.set(idSubasta, handlers);
      subscribeStompTopic(idSubasta);

      return () => {
        const currentHandlers = handlersRef.current.get(idSubasta);
        currentHandlers?.delete(handler);

        if (!currentHandlers || currentHandlers.size === 0) {
          handlersRef.current.delete(idSubasta);
          subscriptionsRef.current.get(idSubasta)?.unsubscribe();
          subscriptionsRef.current.delete(idSubasta);
        }
      };
    },
    [subscribeStompTopic],
  );

  const onReconnect = useCallback((handler: ReconnectHandler) => {
    reconnectHandlersRef.current.add(handler);

    return () => {
      reconnectHandlersRef.current.delete(handler);
    };
  }, []);

  const value = useMemo(
    () => ({
      connected,
      reconnectVersion,
      subscribeToAuctionBids,
      onReconnect,
    }),
    [connected, onReconnect, reconnectVersion, subscribeToAuctionBids],
  );

  return (
    <RealtimeContext.Provider value={value}>
      {children}
    </RealtimeContext.Provider>
  );
}

export function useRealtime() {
  return useContext(RealtimeContext);
}
