import { cerrarLoteDemo } from "@/src/api/subastaAPI";
import { useAuth } from "@/src/context/authContext";
import { useNotifications } from "@/src/context/notificationsContext";
import { useRealtime } from "@/src/context/realtimeContext";
import { useSubastasGuardadas } from "@/src/context/subastasGuardadasContext";
import { ItemActualDTO, LoteCatalogoDTO } from "@/src/dto/DetalleSubastaDTO";
import { SubastaHomeDTO } from "@/src/dto/SubastaHomeDTO";
import { useDetalleSubasta } from "@/src/hooks/useDetalleSubasta";
import { useEstadoPuja } from "@/src/hooks/useEstadoPuja";
import { useMediosPago } from "@/src/hooks/useMediosPago";
import { useRealizarPuja } from "@/src/hooks/useRealizarPuja";
import {
    getCurrencyCode,
    getMonedaLabel,
    normalizeMoneda,
} from "@/src/utils/moneda";
import Ionicons from "@expo/vector-icons/Ionicons";
import { router, useFocusEffect, useLocalSearchParams } from "expo-router";
import { useCallback, useEffect, useRef, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    FlatList,
    Image,
    Pressable,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    View,
} from "react-native";

const defaultImage = require("@/assets/images/white-old-vehicle.jpg");
const IMAGE_WIDTH = 360;
const LOCAL_CLOSE_DELAY_MS = 80000;

function getEstadoTexto(estado: string) {
  switch (estado) {
    case "PROGRAMADA":
      return "Subasta programada";
    case "ACTIVA":
      return "Subasta en vivo";
    case "FINALIZADA":
      return "Subasta finalizada";
    case "CANCELADA":
      return "Subasta cancelada";
    default:
      return estado;
  }
}

function getPrecioLabel(tipoPrecio: string) {
  if (tipoPrecio === "PRECIO_ACTUAL") return "Precio actual";
  if (tipoPrecio === "PRECIO_INICIAL") return "Precio inicial";
  return "Precio final";
}

function formatPrice(moneda: string, precio: number | null) {
  if (precio === null) return "Precio no disponible";

  return `${getCurrencyCode(moneda)} ${precio}`;
}

function formatRematador(rematador: unknown) {
  if (!rematador) return "";
  if (typeof rematador === "string") return rematador;

  const value = rematador as { nombre?: string; apellido?: string };
  return [value.nombre, value.apellido].filter(Boolean).join(" ");
}

function getDescripcionItem(item: unknown) {
  if (item && typeof item === "object" && "descripcion" in item) {
    return String((item as { descripcion?: string }).descripcion ?? "");
  }

  return "";
}

function getImagenLote(lote: LoteCatalogoDTO) {
  return lote.imagenUrl && lote.imagenUrl.trim().length > 0
    ? { uri: lote.imagenUrl }
    : defaultImage;
}

function getImagenesItemActual(item: ItemActualDTO | null) {
  if (!item) return [null];

  const imagenes = item.imagenesUrl?.length ? item.imagenesUrl : item.imagenes;
  const validas =
    imagenes?.filter((image) => image && image.trim().length > 0) ?? [];

  return validas.length > 0 ? validas : [null];
}

function splitFechaHora(fechaInicio: string | null) {
  if (!fechaInicio) return { fecha: null, hora: null };

  const fecha = new Date(fechaInicio);
  if (Number.isNaN(fecha.getTime())) return { fecha: fechaInicio, hora: null };

  return {
    fecha: fecha.toLocaleDateString("es-AR"),
    hora: fecha.toLocaleTimeString("es-AR", {
      hour: "2-digit",
      minute: "2-digit",
    }),
  };
}

export default function DetalleSubastaScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();

  const {
    loadingAuth,
    isAuthenticated,
    isValidated,
    isBlocked,
    isRejected,
    requiresPaymentSetup,
    pendingRegistrationMail,
  } = useAuth();

  const canLoadDetail =
    isAuthenticated && isValidated && !isBlocked && !isRejected;

  const {
    detalle,
    loading,
    error,
    subastaFinalizada: subastaFinalizadaDetalle,
    recargar,
    recargarSilencioso,
  } = useDetalleSubasta(canLoadDetail ? id : undefined);
  const {
    estadoPuja,
    loadingEstadoPuja,
    errorEstadoPuja,
    subastaFinalizada: subastaFinalizadaEstado,
    cargarEstadoPuja,
  } = useEstadoPuja(canLoadDetail ? id : undefined);
  const {
    submittingPuja,
    errorPuja,
    successPuja,
    setErrorPuja,
    setSuccessPuja,
    enviarPuja,
  } = useRealizarPuja(canLoadDetail ? id : undefined);
  const { subscribeToAuctionBids, onReconnect } = useRealtime();
  const { mediosPago, loadingMediosPago, errorMediosPago, cargarMediosPago } =
    useMediosPago(canLoadDetail);
  const { addLocalNotification, watchBidNotification } = useNotifications();
  const {
    subastasGuardadas,
    pendingIds: pendingGuardadasIds,
    recargar: recargarGuardadas,
    toggleGuardada,
    estaGuardada,
  } = useSubastasGuardadas();

  const listRef = useRef<FlatList>(null);
  const closeTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const finalizadaAlertShownRef = useRef(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [montoOferta, setMontoOferta] = useState("");

  const clearLocalCloseTimer = useCallback(() => {
    if (closeTimerRef.current) {
      clearTimeout(closeTimerRef.current);
      closeTimerRef.current = null;
    }
  }, []);

  const scheduleLocalClose = useCallback(
    (idItemCatalogo: number, tituloItem: string) => {
      if (!id) return;

      clearLocalCloseTimer();

      closeTimerRef.current = setTimeout(async () => {
        try {
          const cierre = await cerrarLoteDemo(id, idItemCatalogo);

          await recargarSilencioso();
          await cargarEstadoPuja();
          await recargarGuardadas();

          if (cierre.idVenta) {
            await addLocalNotification({
              kind: "SUBASTA_GANADA",
              title: "Subasta ganada",
              body: `Felicitaciones, ganaste el artículo ${tituloItem}. Revisá mensajería para completar la compra.`,
              subastaId: Number(id),
              actionLabel: "Ir a mensajería",
            });
          }
        } catch (error) {
          console.error("No pudimos cerrar el lote demo:", error);
        } finally {
          closeTimerRef.current = null;
        }
      }, LOCAL_CLOSE_DELAY_MS);
    },
    [
      addLocalNotification,
      cargarEstadoPuja,
      clearLocalCloseTimer,
      id,
      recargarGuardadas,
      recargarSilencioso,
    ],
  );

  useFocusEffect(
    useCallback(() => {
      if (canLoadDetail) {
        void cargarEstadoPuja();
        void recargarSilencioso();
        void recargarGuardadas();
        void cargarMediosPago();
      }
      return onReconnect(() => {
        if (canLoadDetail) {
          void cargarEstadoPuja();
          void recargarSilencioso();
        }
      });
    }, [
      canLoadDetail,
      cargarEstadoPuja,
      cargarMediosPago,
      onReconnect,
      recargarGuardadas,
      recargarSilencioso,
    ]),
  );

  useEffect(() => {
    if (!canLoadDetail || !id) return;

    return subscribeToAuctionBids(Number(id), (event) => {
      if (
        detalle?.itemActual?.idItemCatalogo &&
        event.idItemCatalogo !== detalle.itemActual.idItemCatalogo
      ) {
        clearLocalCloseTimer();
        void recargarSilencioso();
      }

      if (
        detalle?.itemActual?.idItemCatalogo &&
        event.idItemCatalogo === detalle.itemActual.idItemCatalogo
      ) {
        scheduleLocalClose(
          event.idItemCatalogo,
          detalle.itemActual.titulo ?? detalle.subasta.titulo,
        );
      }

      void cargarEstadoPuja();
    });
  }, [
    canLoadDetail,
    cargarEstadoPuja,
    clearLocalCloseTimer,
    detalle?.itemActual?.idItemCatalogo,
    detalle?.itemActual?.titulo,
    detalle?.subasta?.titulo,
    id,
    recargarSilencioso,
    scheduleLocalClose,
    subscribeToAuctionBids,
  ]);

  useEffect(() => {
    setCurrentIndex(0);
    listRef.current?.scrollToOffset({ offset: 0, animated: false });
    clearLocalCloseTimer();
  }, [clearLocalCloseTimer, detalle?.itemActual?.idItemCatalogo]);

  useEffect(() => clearLocalCloseTimer, [clearLocalCloseTimer]);

  useEffect(() => {
    if (
      !canLoadDetail ||
      !detalle?.subasta?.idSubasta ||
      estadoPuja?.miMejorOferta === null ||
      estadoPuja?.miMejorOferta === undefined
    ) {
      return;
    }

    void watchBidNotification({
      subastaId: detalle.subasta.idSubasta,
      amount: estadoPuja.miMejorOferta,
      title: detalle.itemActual?.titulo ?? detalle.subasta.titulo,
    });
  }, [
    canLoadDetail,
    detalle?.itemActual?.titulo,
    detalle?.subasta?.idSubasta,
    detalle?.subasta?.titulo,
    estadoPuja?.miMejorOferta,
    watchBidNotification,
  ]);

  useEffect(() => {
    const subastaFinalizada =
      subastaFinalizadaDetalle || subastaFinalizadaEstado;

    if (!subastaFinalizada) {
      finalizadaAlertShownRef.current = false;
      return;
    }

    if (finalizadaAlertShownRef.current) {
      return;
    }

    finalizadaAlertShownRef.current = true;

    Alert.alert(
      "Subasta finalizada",
      "Esta subasta finalizó o ya no está disponible.",
      [
        { text: "Cerrar", style: "cancel" },
        {
          text: "Volver al Home",
          onPress: () => router.replace("/(tabs)/home" as any),
        },
      ],
    );
  }, [subastaFinalizadaDetalle, subastaFinalizadaEstado]);

  useEffect(() => {
    if (loadingAuth) return;

    if (pendingRegistrationMail && !isAuthenticated) {
      router.replace({
        pathname: "/(tabs)/auth/registration-status" as any,
        params: { mail: pendingRegistrationMail },
      });
      return;
    }

    if (!isAuthenticated) {
      Alert.alert(
        "Iniciá sesión",
        "Necesitás iniciar sesión para ver el detalle de la subasta.",
        [
          { text: "Volver", style: "cancel", onPress: () => router.back() },
          {
            text: "Iniciar sesión",
            onPress: () => router.replace("/auth/login"),
          },
        ],
      );
      return;
    }

    if (!isValidated || isBlocked || isRejected) {
      router.replace("/(tabs)/profile");
    }
  }, [
    loadingAuth,
    pendingRegistrationMail,
    isAuthenticated,
    isValidated,
    isBlocked,
    isRejected,
  ]);

  if (loadingAuth || !canLoadDetail) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Verificando cuenta...</Text>
      </View>
    );
  }

  if (loading) {
    return (
      <View style={styles.stateContainer}>
        <ActivityIndicator size="large" color="#2F63F6" />
        <Text style={styles.stateText}>Cargando detalle...</Text>
      </View>
    );
  }

  if (error || !detalle) {
    const subastaFinalizada =
      subastaFinalizadaDetalle || subastaFinalizadaEstado;

    return (
      <View style={styles.stateContainer}>
        <Text style={styles.errorText}>
          {subastaFinalizada
            ? "Esta subasta finalizó o ya no está disponible."
            : (error ?? "No pudimos cargar el detalle de la subasta.")}
        </Text>

        {subastaFinalizada ? (
          <Pressable
            style={styles.retryButton}
            onPress={() => router.replace("/(tabs)/home" as any)}
          >
            <Text style={styles.retryText}>Volver al Home</Text>
          </Pressable>
        ) : (
          <Pressable style={styles.retryButton} onPress={() => recargar()}>
            <Text style={styles.retryText}>Reintentar</Text>
          </Pressable>
        )}
      </View>
    );
  }

  const subasta = detalle.subasta;
  const esActiva = subasta.estadoSubasta === "ACTIVA";
  const esProgramada = subasta.estadoSubasta === "PROGRAMADA";
  const esFinalizada = subasta.estadoSubasta === "FINALIZADA";
  const catalogo = detalle.catalogo ?? [];
  const proximosLotes = detalle.proximosLotes ?? [];
  const itemActual = esActiva ? detalle.itemActual : null;
  const tituloSubasta = subasta.titulo;
  const tituloItemActual = itemActual?.titulo ?? subasta.titulo;
  const precioMostrado =
    esActiva && estadoPuja?.mejorOferta
      ? estadoPuja.mejorOferta
      : esActiva && detalle.itemActual?.precioActual
        ? detalle.itemActual.precioActual
        : (itemActual?.precioBase ?? null);
  const tipoPrecio = esActiva ? "PRECIO_ACTUAL" : "PRECIO_INICIAL";
  const puedeOfertar = esActiva && !!detalle.itemActual;
  const lotesMostrados = esProgramada ? catalogo : proximosLotes;
  const { fecha, hora } = splitFechaHora(subasta.fechaInicio);
  const guardada =
    estaGuardada(subasta.idSubasta) ||
    (!!subasta.guardada && subastasGuardadas.length === 0);
  const guardadaLoading = pendingGuardadasIds.has(subasta.idSubasta);

  const baseImages = getImagenesItemActual(itemActual);

  const carouselImages = Array.from(
    { length: 4 },
    (_, index) => baseImages[index] ?? baseImages[0],
  );

  function goToImage(direction: "prev" | "next") {
    const nextIndex =
      direction === "next"
        ? Math.min(currentIndex + 1, carouselImages.length - 1)
        : Math.max(currentIndex - 1, 0);

    setCurrentIndex(nextIndex);

    listRef.current?.scrollToIndex({
      index: nextIndex,
      animated: true,
    });
  }

  function handleAmountChange(value: string) {
    setMontoOferta(value.replace(/[^\d.]/g, ""));
    setErrorPuja(null);
    setSuccessPuja(null);
  }

  async function handleOffer() {
    if (requiresPaymentSetup) {
      Alert.alert(
        "Registro financiero pendiente",
        "Para ofertar tenés que cargar una cuenta de cobro y al menos un medio de pago.",
        [
          { text: "Cancelar", style: "cancel" },
          {
            text: "Completar",
            onPress: () => router.push("/(tabs)/financial-setup" as any),
          },
        ],
      );
      return;
    }

    if (!montoOferta.trim()) {
      Alert.alert("Monto obligatorio", "Ingresá el monto que querés ofertar.");
      return;
    }

    const monto = Number(montoOferta);

    if (Number.isNaN(monto) || monto <= 0) {
      Alert.alert("Monto inválido", "Ingresá un monto válido.");
      return;
    }

    if (estadoPuja && monto < estadoPuja.ofertaMinimaPermitida) {
      setErrorPuja(
        `La oferta debe ser al menos ${formatPrice(estadoPuja.moneda, estadoPuja.ofertaMinimaPermitida)}.`,
      );
      return;
    }

    if (
      estadoPuja?.ofertaMaximaPermitida !== null &&
      estadoPuja?.ofertaMaximaPermitida !== undefined &&
      monto > estadoPuja.ofertaMaximaPermitida
    ) {
      setErrorPuja(
        `La oferta no puede superar ${formatPrice(estadoPuja.moneda, estadoPuja.ofertaMaximaPermitida)}.`,
      );
      return;
    }

    if (errorMediosPago) {
      Alert.alert("Medios de pago", errorMediosPago);
      return;
    }

    if (!loadingMediosPago && !tieneGarantiaAparente) {
      Alert.alert(
        "Garantia insuficiente",
        tieneMediosPago
          ? `No tenes un medio de pago en ${getMonedaLabel(monedaPuja)} con garantia suficiente para este monto.`
          : "Necesitas cargar un medio de pago para pujar.",
        !tieneMediosPago
          ? [
              { text: "Cancelar", style: "cancel" },
              {
                text: "Ir a medios de pago",
                onPress: () =>
                  router.push("/(tabs)/financial-setup/medios-pago" as any),
              },
            ]
          : [{ text: "OK" }],
      );
      return;
    }

    Alert.alert(
      "Confirmar puja",
      `¿Estás seguro de ofertar ${formatPrice(monedaPuja, monto)}?`,
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Confirmar",
          onPress: async () => {
            const response = await enviarPuja(monto);
            await cargarEstadoPuja();

            if (!response) return;

            setMontoOferta("");
            await watchBidNotification({
              subastaId: subasta.idSubasta,
              amount: response.monto,
              title: itemActual?.titulo ?? subasta.titulo,
            });
            scheduleLocalClose(
              response.idItemCatalogo,
              itemActual?.titulo ?? subasta.titulo,
            );
            await recargar();

            if (response.estado === "SUPERADA") {
              await addLocalNotification({
                kind: "PUJA_SUPERADA",
                title: "Oferta superada",
                body: `Tu oferta en ${itemActual?.titulo ?? subasta.titulo} fue sobrepasada por otra persona. Realizá otra puja antes de que se acabe el tiempo.`,
                subastaId: subasta.idSubasta,
                actionLabel: "Volver a subasta",
              });
            }

            if (response.estado === "GANADORA") {
              await addLocalNotification({
                kind: "SUBASTA_GANADA",
                title: "Subasta ganada",
                body: `Felicitaciones, ganaste el artículo ${itemActual?.titulo ?? subasta.titulo}. Por favor, revisá los detalles en mensajería y completá el pago.`,
                subastaId: subasta.idSubasta,
                actionLabel: "Ir a mensajería",
              });
            }
          },
        },
      ],
    );
  }

  function handleStreaming() {
    Alert.alert(
      "Streaming",
      subasta.linkVivo ?? "Después abrimos el link de streaming.",
    );
  }

  function buildSubastaGuardada(): SubastaHomeDTO {
    const imagenPrincipal = getImagenesItemActual(itemActual)[0];
    const imagenUrl = imagenPrincipal ?? null;
    const precio =
      esActiva && itemActual?.precioActual
        ? itemActual.precioActual
        : (itemActual?.precioBase ?? null);

    return {
      idSubasta: subasta.idSubasta,
      titulo: tituloItemActual,
      imagenUrl,
      precio,
      moneda: subasta.moneda,
      estadoSubasta: subasta.estadoSubasta,
      categoriaMin: subasta.categoriaMin,
      fechaInicio: subasta.fechaInicio,
    };
  }

  async function handleGuardarSubasta() {
    await toggleGuardada(buildSubastaGuardada());
  }

  const amountNumber = Number(montoOferta);
  const monedaPuja = estadoPuja?.moneda ?? subasta.moneda;
  const monedaPujaNormalizada = normalizeMoneda(monedaPuja);
  const montoIngresadoValido =
    montoOferta.trim().length > 0 &&
    !Number.isNaN(amountNumber) &&
    amountNumber > 0;
  const mediosCompatibles = mediosPago.filter(
    (medio) => normalizeMoneda(medio.moneda) === monedaPujaNormalizada,
  );
  const tieneMediosPago = mediosPago.length > 0;
  const tieneGarantiaAparente =
    montoIngresadoValido &&
    mediosCompatibles.some((medio) => medio.capacidad >= amountNumber);
  const errorPujaEsGarantia =
    !!errorPuja &&
    errorPuja.toLowerCase().includes("medio de pago") &&
    errorPuja.toLowerCase().includes("garant");
  const bidAmountInvalid =
    !montoOferta.trim() ||
    Number.isNaN(amountNumber) ||
    amountNumber <= 0 ||
    (estadoPuja ? amountNumber < estadoPuja.ofertaMinimaPermitida : false) ||
    (estadoPuja?.ofertaMaximaPermitida !== null &&
    estadoPuja?.ofertaMaximaPermitida !== undefined
      ? amountNumber > estadoPuja.ofertaMaximaPermitida
      : false);
  const bidPersonalStatus =
    estadoPuja?.miMejorOferta === null ||
    estadoPuja?.miMejorOferta === undefined
      ? {
          title: "Todavia no realizaste una puja en este lote.",
          tone: "neutral" as const,
        }
      : estadoPuja.soyMejorPostor
        ? {
            title: "Vas ganando",
            tone: "winning" as const,
          }
        : {
            title: "Te superaron",
            detail: `Tu mejor oferta: ${formatPrice(estadoPuja.moneda, estadoPuja.miMejorOferta)}`,
            tone: "outbid" as const,
          };

  return (
    <ScrollView style={styles.screen} contentContainerStyle={styles.content}>
      <View style={styles.topBar}>
        <Pressable onPress={() => router.back()} style={styles.backButton}>
          <Ionicons name="chevron-back" size={34} color="#111827" />
        </Pressable>

        <Pressable
          onPress={handleGuardarSubasta}
          style={styles.saveButton}
          disabled={guardadaLoading}
        >
          {guardadaLoading ? (
            <ActivityIndicator size="small" color="#111827" />
          ) : (
            <Ionicons
              name={guardada ? "bookmark" : "bookmark-outline"}
              size={28}
              color="#111827"
            />
          )}
        </Pressable>
      </View>

      <View style={styles.auctionHeader}>
        <Text style={styles.auctionTitle}>{tituloSubasta}</Text>
        <Text style={styles.status}>
          {getEstadoTexto(subasta.estadoSubasta)}
        </Text>
        <Text style={styles.dateText}>
          Fecha: {fecha ?? "Sin fecha"} {hora ? `- ${hora}` : ""}
        </Text>
        {subasta.ubicacion && (
          <Text style={styles.dateText}>Dirección: {subasta.ubicacion}</Text>
        )}
        <Text style={styles.dateText}>Categoría: {subasta.categoriaMin}</Text>
        <Text style={styles.dateText}>
          Martillero: {formatRematador(subasta.rematador)}
        </Text>
        <Text style={styles.dateText}>Subasta en {subasta.moneda}</Text>
      </View>

      {esFinalizada && !detalle.itemActual && (
        <View style={styles.closedCard}>
          <Ionicons
            name="checkmark-done-circle-outline"
            size={28}
            color="#64748B"
          />
          <View style={styles.closedCopy}>
            <Text style={styles.closedTitle}>Subasta finalizada</Text>
            <Text style={styles.closedText}>
              No hay un lote en remate actualmente.
            </Text>
          </View>
        </View>
      )}

      {esActiva && itemActual && (
        <>
          <View style={styles.carouselContainer}>
            <FlatList
              ref={listRef}
              data={carouselImages}
              horizontal
              pagingEnabled
              showsHorizontalScrollIndicator={false}
              keyExtractor={(_, index) => index.toString()}
              onMomentumScrollEnd={(event) => {
                const index = Math.round(
                  event.nativeEvent.contentOffset.x / IMAGE_WIDTH,
                );
                setCurrentIndex(index);
              }}
              renderItem={({ item }) => (
                <Image
                  source={item ? { uri: item } : defaultImage}
                  style={styles.mainImage}
                  resizeMode="cover"
                />
              )}
            />

            <Pressable
              style={[styles.arrowButton, styles.leftArrow]}
              onPress={() => goToImage("prev")}
            >
              <Text style={styles.arrow}>‹</Text>
            </Pressable>

            <Pressable
              style={[styles.arrowButton, styles.rightArrow]}
              onPress={() => goToImage("next")}
            >
              <Text style={styles.arrow}>›</Text>
            </Pressable>
          </View>

          <View style={styles.thumbnailRow}>
            {carouselImages.map((image, index) => (
              <Pressable
                key={index}
                style={styles.thumbnailButton}
                onPress={() => {
                  setCurrentIndex(index);
                  listRef.current?.scrollToIndex({ index, animated: true });
                }}
              >
                <Image
                  source={image ? { uri: image } : defaultImage}
                  style={[
                    styles.thumbnail,
                    currentIndex === index && styles.thumbnailActive,
                  ]}
                  resizeMode="cover"
                />
              </Pressable>
            ))}
          </View>

          <View style={styles.infoCard}>
            <Text style={styles.title}>{tituloItemActual}</Text>

            <Text style={styles.label}>{getPrecioLabel(tipoPrecio)}</Text>

            <Text style={styles.price}>
              {formatPrice(subasta.moneda, precioMostrado)}
            </Text>

            <Text style={styles.description}>
              {getDescripcionItem(itemActual)}
            </Text>
          </View>

          <View style={styles.separator} />
        </>
      )}

      {puedeOfertar && (
        <View style={styles.offerCard}>
          <View style={styles.bidHeaderRow}>
            <View>
              <Text style={styles.bidLabel}>Pujar</Text>
              <Text style={styles.bidSubtitle}>Precio actual</Text>
            </View>

            <Text style={styles.bidCurrentPrice}>
              {formatPrice(
                estadoPuja?.moneda ?? subasta.moneda,
                estadoPuja?.mejorOferta ?? precioMostrado,
              )}
            </Text>
          </View>

          {loadingEstadoPuja ? (
            <Text style={styles.bidHelperText}>Actualizando valores...</Text>
          ) : errorEstadoPuja ? (
            <Text style={styles.bidErrorText}>{errorEstadoPuja}</Text>
          ) : estadoPuja ? (
            <>
              <View style={styles.bidStatusRow}>
                <Text
                  style={[
                    styles.bidStatusText,
                    bidPersonalStatus.tone === "winning" &&
                      styles.bidStatusTextWinning,
                    bidPersonalStatus.tone === "outbid" &&
                      styles.bidStatusTextOutbid,
                  ]}
                >
                  {bidPersonalStatus.title}
                </Text>
                {"detail" in bidPersonalStatus && (
                  <Text style={styles.bidPersonalStatusDetail}>
                    {bidPersonalStatus.detail}
                  </Text>
                )}
              </View>

              <View style={styles.bidLimitsRow}>
                <Text style={styles.bidRangeText}>
                  <Text style={styles.bidRangeLabel}>Puja mín:</Text>{" "}
                  {formatPrice(
                    estadoPuja.moneda,
                    estadoPuja.ofertaMinimaPermitida,
                  )}
                </Text>
                {estadoPuja.ofertaMaximaPermitida !== null && (
                  <Text style={styles.bidRangeText}>
                    <Text style={styles.bidRangeLabel}>Puja máx:</Text>{" "}
                    {formatPrice(
                      estadoPuja.moneda,
                      estadoPuja.ofertaMaximaPermitida,
                    )}
                  </Text>
                )}
              </View>
            </>
          ) : null}

          {requiresPaymentSetup && (
            <Text style={styles.warningText}>
              Para ofertar, primero completá cuenta de cobro y medios de pago.
            </Text>
          )}

          <TextInput
            style={styles.input}
            placeholder="Ingresá tu oferta"
            placeholderTextColor="#888"
            keyboardType="numeric"
            value={montoOferta}
            onChangeText={handleAmountChange}
          />
          {successPuja && (
            <Text style={styles.bidSuccessText}>{successPuja}</Text>
          )}
          {errorPuja && <Text style={styles.bidErrorText}>{errorPuja}</Text>}
          {errorPujaEsGarantia && (
            <Pressable
              style={styles.inlinePaymentButton}
              onPress={() =>
                router.push("/(tabs)/financial-setup/medios-pago" as any)
              }
            >
              <Text style={styles.inlinePaymentButtonText}>
                Ir a medios de pago
              </Text>
            </Pressable>
          )}

          <Pressable
            style={[
              styles.bidButton,
              (submittingPuja || bidAmountInvalid) && styles.bidButtonDisabled,
            ]}
            onPress={handleOffer}
            disabled={submittingPuja || bidAmountInvalid}
          >
            {submittingPuja ? (
              <View style={styles.bidLoadingContent}>
                <ActivityIndicator color="#FFFFFF" />
                <Text style={styles.bidButtonText}>Procesando puja...</Text>
              </View>
            ) : (
              <Text style={styles.bidButtonText}>Pujar</Text>
            )}
          </Pressable>
        </View>
      )}

      {subasta.estadoSubasta === "ACTIVA" && subasta.linkVivo && (
        <Pressable onPress={handleStreaming} style={styles.streamingCard}>
          <Ionicons name="play-circle-outline" size={24} color="#2563EB" />
          <Text style={styles.streamingLink}>Ver en vivo</Text>
        </Pressable>
      )}

      {lotesMostrados.length > 0 && (
        <View style={styles.metaCard}>
          <Text style={styles.sectionLabel}>
            {subasta.estadoSubasta === "PROGRAMADA"
              ? "Catálogo"
              : "Próximos lotes"}
          </Text>

          {lotesMostrados.map((lote) => (
            <View key={lote.idItemCatalogo} style={styles.loteRow}>
              {esProgramada && (
                <Image
                  source={getImagenLote(lote)}
                  style={styles.loteImage}
                  resizeMode="cover"
                />
              )}

              <View style={styles.loteInfo}>
                <Text style={styles.dateText}>Lote #{lote.numeroLote}</Text>
                <Text style={styles.loteTitle}>{lote.titulo}</Text>
                <Text style={styles.dateText}>
                  {formatPrice(subasta.moneda, lote.precioBase)}
                </Text>
              </View>
            </View>
          ))}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: "#FFFFFF" },

  content: {
    paddingHorizontal: 18,
    paddingTop: 18,
    paddingBottom: 52,
  },

  topBar: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 22,
  },

  backButton: {
    width: 42,
    height: 42,
    justifyContent: "center",
  },

  saveButton: {
    width: 42,
    height: 42,
    alignItems: "center",
    justifyContent: "center",
  },

  auctionHeader: {
    marginBottom: 26,
  },

  auctionTitle: {
    fontSize: 22,
    fontWeight: "800",
    color: "#111827",
    lineHeight: 30,
    marginBottom: 12,
  },

  carouselContainer: {
    width: IMAGE_WIDTH,
    height: 320,
    alignSelf: "center",
    borderRadius: 28,
    overflow: "hidden",
    borderWidth: 1.4,
    borderColor: "#222",
    backgroundColor: "#F3F3F3",
    position: "relative",
  },

  mainImage: {
    width: IMAGE_WIDTH,
    height: 320,
  },

  arrowButton: {
    position: "absolute",
    top: "43%",
    width: 34,
    height: 46,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255,255,255,0.7)",
    borderRadius: 18,
  },

  leftArrow: { left: 8 },
  rightArrow: { right: 8 },

  arrow: {
    fontSize: 36,
    color: "#222",
    fontWeight: "300",
  },

  thumbnailRow: {
    width: IMAGE_WIDTH,
    alignSelf: "center",
    flexDirection: "row",
    justifyContent: "space-between",
    gap: 8,
    marginTop: 12,
    marginBottom: 30,
  },

  thumbnailButton: {
    width: 82,
    height: 70,
  },

  thumbnail: {
    width: 82,
    height: 70,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#C7D2FE",
    backgroundColor: "#F3F4F6",
  },

  thumbnailActive: {
    borderWidth: 2,
    borderColor: "#2F63F6",
  },

  infoCard: {
    marginTop: 10,
    marginBottom: 8,
  },

  metaCard: {
    marginTop: 10,
    marginBottom: 8,
  },

  auctioneerCard: {
    marginBottom: 26,
  },

  offerCard: {
    marginTop: 6,
    marginBottom: 14,
    paddingBottom: 2,
  },

  separator: {
    height: 1,
    backgroundColor: "#E5E7EB",
    marginVertical: 26,
  },

  title: {
    fontSize: 30,
    fontWeight: "800",
    color: "#111827",
    lineHeight: 40,
    marginBottom: 28,
  },

  label: {
    fontSize: 12,
    color: "#6B7280",
    textTransform: "uppercase",
    letterSpacing: 1.2,
    marginBottom: 10,
  },

  price: {
    fontSize: 36,
    fontWeight: "800",
    color: "#111827",
    marginBottom: 30,
  },

  description: {
    fontSize: 16,
    color: "#374151",
    lineHeight: 28,
    marginBottom: 10,
  },

  sectionLabel: {
    fontSize: 12,
    color: "#9CA3AF",
    textTransform: "uppercase",
    letterSpacing: 1.3,
    marginBottom: 18,
  },

  status: {
    fontSize: 18,
    fontWeight: "700",
    color: "#2563EB",
    marginBottom: 14,
  },

  dateText: {
    fontSize: 15,
    color: "#4B5563",
    lineHeight: 24,
  },

  closedCard: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#F8FAFC",
    borderRadius: 16,
    padding: 14,
    marginTop: -10,
    marginBottom: 26,
  },

  closedCopy: {
    flex: 1,
  },

  closedTitle: {
    fontSize: 16,
    color: "#0F172A",
    fontWeight: "800",
    marginBottom: 4,
  },

  closedText: {
    fontSize: 13,
    color: "#64748B",
    lineHeight: 19,
    fontWeight: "600",
  },

  loteRow: {
    flexDirection: "row",
    gap: 12,
    marginBottom: 18,
  },

  loteImage: {
    width: 86,
    height: 74,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#C7D2FE",
    backgroundColor: "#F3F4F6",
  },

  loteInfo: {
    flex: 1,
  },

  loteTitle: {
    fontSize: 16,
    color: "#111827",
    fontWeight: "700",
    lineHeight: 22,
  },

  auctioneer: {
    fontSize: 17,
    color: "#111827",
    fontWeight: "600",
  },

  bidLabel: {
    fontSize: 17,
    color: "#111827",
    fontWeight: "700",
    marginBottom: 8,
  },

  bidHeaderRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: 12,
    marginBottom: 10,
  },

  bidSubtitle: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "700",
  },

  bidCurrentPrice: {
    fontSize: 20,
    color: "#111827",
    fontWeight: "900",
  },

  bidLimitsRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 14,
    marginBottom: 22,
  },

  bidRangeText: {
    fontSize: 13,
    color: "#334155",
    fontWeight: "700",
  },

  bidRangeLabel: {
    fontWeight: "900",
    color: "#0F172A",
  },

  bidStatusRow: {
    marginBottom: 26,
  },

  bidStatusText: {
    fontSize: 14,
    color: "#475569",
    fontWeight: "800",
  },

  bidStatusTextWinning: {
    color: "#15803D",
  },

  bidStatusTextOutbid: {
    color: "#B45309",
  },

  bidPersonalStatusDetail: {
    marginTop: 4,
    fontSize: 12,
    color: "#4B5563",
    fontWeight: "700",
  },

  bidHelperText: {
    fontSize: 12,
    color: "#6B7280",
    fontWeight: "700",
    marginBottom: 12,
  },

  bidSuccessText: {
    fontSize: 13,
    color: "#15803D",
    backgroundColor: "#DCFCE7",
    padding: 10,
    borderRadius: 10,
    marginBottom: 12,
    fontWeight: "800",
  },

  bidErrorText: {
    fontSize: 13,
    color: "#B91C1C",
    backgroundColor: "#FEE2E2",
    padding: 10,
    borderRadius: 10,
    marginBottom: 12,
    fontWeight: "800",
  },

  warningText: {
    fontSize: 14,
    color: "#B45309",
    backgroundColor: "#FEF3C7",
    padding: 12,
    borderRadius: 12,
    marginBottom: 14,
    lineHeight: 20,
  },

  input: {
    borderWidth: 1.2,
    borderColor: "#CBD5E1",
    borderRadius: 12,
    backgroundColor: "#FFFFFF",
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 17,
    marginBottom: 14,
    color: "#111827",
  },

  inlinePaymentButton: {
    alignSelf: "flex-start",
    marginTop: -4,
    marginBottom: 12,
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 10,
    backgroundColor: "#111827",
  },

  inlinePaymentButtonText: {
    color: "#FFFFFF",
    fontSize: 13,
    fontWeight: "800",
  },

  bidButton: {
    backgroundColor: "#111827",
    paddingVertical: 15,
    alignItems: "center",
    borderRadius: 14,
    marginBottom: 4,
  },

  bidButtonDisabled: {
    opacity: 0.55,
  },

  bidLoadingContent: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },

  bidButtonText: {
    color: "#FFFFFF",
    fontSize: 16,
    fontWeight: "700",
  },

  streamingCard: {
    backgroundColor: "#EFF6FF",
    borderRadius: 14,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    paddingVertical: 12,
    paddingHorizontal: 14,
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "row",
    gap: 8,
    marginTop: 2,
    marginBottom: 36,
  },

  streamingLink: {
    color: "#2563EB",
    fontSize: 15,
    fontWeight: "700",
    textAlign: "center",
  },

  stateContainer: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    justifyContent: "center",
    alignItems: "center",
    padding: 24,
  },

  stateText: {
    marginTop: 10,
    fontSize: 15,
    color: "#555",
  },

  errorText: {
    fontSize: 15,
    color: "#B91C1C",
    textAlign: "center",
    marginBottom: 14,
  },

  retryButton: {
    backgroundColor: "#2F63F6",
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: 10,
  },

  retryText: {
    color: "white",
    fontWeight: "700",
  },
});
