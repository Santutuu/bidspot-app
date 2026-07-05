export type BackendMoneda = "PESOS" | "DOLARES";
export type FrontendMoneda = BackendMoneda | "ARS" | "USD" | string;

export function normalizeMoneda(moneda: FrontendMoneda | null | undefined) {
  if (moneda === "ARS" || moneda === "PESOS") return "PESOS";
  if (moneda === "USD" || moneda === "DOLARES") return "DOLARES";
  return moneda ?? "";
}

export function getCurrencyCode(moneda: FrontendMoneda | null | undefined) {
  return normalizeMoneda(moneda) === "DOLARES" ? "USD" : "ARS";
}

export function getMonedaLabel(moneda: FrontendMoneda | null | undefined) {
  return getCurrencyCode(moneda);
}
