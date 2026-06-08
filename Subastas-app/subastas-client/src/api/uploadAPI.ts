import api from "@/src/api/axios";

type UploadResponse = {
  url: string;
};

export async function uploadDniImage(imageUri: string): Promise<string> {
  const formData = new FormData();

  formData.append("file", {
    uri: imageUri,
    name: `dni-${Date.now()}.jpg`,
    type: "image/jpeg",
  } as any);

  const response = await api.post<UploadResponse>("/uploads/dni", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response.data.url;
}