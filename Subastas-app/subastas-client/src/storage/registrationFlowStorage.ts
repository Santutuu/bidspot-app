import AsyncStorage from "@react-native-async-storage/async-storage";

const PENDING_REGISTRATION_MAIL_KEY = "pending_registration_mail";

export async function savePendingRegistrationMail(mail: string) {
  await AsyncStorage.setItem(PENDING_REGISTRATION_MAIL_KEY, mail);
}

export async function getPendingRegistrationMail(): Promise<string | null> {
  return AsyncStorage.getItem(PENDING_REGISTRATION_MAIL_KEY);
}

export async function clearPendingRegistrationMail() {
  await AsyncStorage.removeItem(PENDING_REGISTRATION_MAIL_KEY);
}