import { EstadoVenta, Moneda } from "@/src/dto/CompraDTO";

export function normalizarMoneda(moneda: string | null | undefined) {
  if (!moneda) return null;
  const valor = moneda.toUpperCase();
  if (valor === "PESOS") return "ARS";
  return valor;
}

export function monedasCompatibles(
  origen: string | null | undefined,
  destino: string | null | undefined,
) {
  return normalizarMoneda(origen) === normalizarMoneda(destino);
}

export function formatCurrency(value: number, moneda: Moneda) {
  const normalized = normalizarMoneda(moneda);
  const prefix = normalized === "DOLARES" || normalized === "USD" ? "USD" : "$";
  return `${prefix} ${value}`;
}

export function formatDate(value: string | null | undefined) {
  if (!value) return "No informado";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("es-AR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function getEstadoVentaLabel(estado: EstadoVenta | string) {
  const labels: Partial<Record<EstadoVenta, string>> = {
    PENDIENTE_PAGO: "Pago pendiente",
    PAGO_CONFIRMADO: "Pago confirmado",
    PREPARANDO_ENVIO: "Preparando envio",
    ENVIADO: "Enviado",
    EN_CAMINO: "En camino",
    ENTREGADO: "Entregado",
    PREPARANDO_RETIRO: "Preparando retiro",
    LISTO_PARA_RETIRAR: "Listo para retirar",
    RETIRADO: "Retirado",
    INCUMPLIDA: "Plazo de pago incumplido",
    CANCELADA: "Cancelada",
  };

  return labels[estado as EstadoVenta] ?? `Estado: ${estado}`;
}

export function getEstadoVentaTone(
  estado: EstadoVenta | string,
): "warning" | "success" | "info" | "danger" | "neutral" {
  if (estado === "PENDIENTE_PAGO") return "warning";
  if (estado === "PAGO_CONFIRMADO") return "success";
  if (estado === "ENTREGADO" || estado === "RETIRADO") return "success";
  if (estado === "INCUMPLIDA" || estado === "CANCELADA") return "danger";
  if (
    [
      "PREPARANDO_ENVIO",
      "ENVIADO",
      "EN_CAMINO",
      "PREPARANDO_RETIRO",
      "LISTO_PARA_RETIRAR",
    ].includes(estado)
  ) {
    return "info";
  }
  return "neutral";
}

export function esCompraPendiente(estado: EstadoVenta | string) {
  return estado === "PENDIENTE_PAGO";
}

export function esCompraFinalizada(estado: EstadoVenta | string) {
  return ["ENTREGADO", "RETIRADO", "CANCELADA", "INCUMPLIDA"].includes(estado);
}

export function esDireccionTecnica(value: string | null | undefined) {
  return !!value && value.includes("com.subastas.") && value.includes("@");
}
