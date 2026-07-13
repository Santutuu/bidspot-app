import axios from "axios";

type ApiErrorBody = {
  message?: string;
  error?: string;
};

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiErrorBody | string>(error)) {
    const data = error.response?.data;
    const backendMessage =
      typeof data === "string" ? data : (data?.message ?? data?.error);

    if (backendMessage) return backendMessage;
  }

  return fallback;
}
