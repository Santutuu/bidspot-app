import AsyncStorage from "@react-native-async-storage/async-storage";

import { AppNotification } from "@/src/types/notifications";

export interface WatchedBid {
  subastaId: number;
  amount: number;
  title: string;
  createdAt: string;
}

function getNotificationsKey(userId: number | string) {
  return `notifications_${userId}`;
}

function getDismissedNotificationsKey(userId: number | string) {
  return `notifications_dismissed_${userId}`;
}

function getWatchedBidsKey(userId: number | string) {
  return `notifications_watched_bids_${userId}`;
}

export async function getStoredNotifications(
  userId: number | string,
): Promise<AppNotification[]> {
  const raw = await AsyncStorage.getItem(getNotificationsKey(userId));

  if (!raw) {
    return [];
  }

  try {
    return JSON.parse(raw) as AppNotification[];
  } catch {
    return [];
  }
}

export async function saveNotifications(
  userId: number | string,
  notifications: AppNotification[],
) {
  await AsyncStorage.setItem(
    getNotificationsKey(userId),
    JSON.stringify(notifications),
  );
}

export async function clearStoredNotifications(userId: number | string) {
  await AsyncStorage.removeItem(getNotificationsKey(userId));
}

export async function getDismissedNotificationIds(
  userId: number | string,
): Promise<string[]> {
  const raw = await AsyncStorage.getItem(getDismissedNotificationsKey(userId));

  if (!raw) {
    return [];
  }

  try {
    return JSON.parse(raw) as string[];
  } catch {
    return [];
  }
}

export async function saveDismissedNotificationIds(
  userId: number | string,
  notificationIds: string[],
) {
  await AsyncStorage.setItem(
    getDismissedNotificationsKey(userId),
    JSON.stringify(notificationIds),
  );
}

export async function addDismissedNotificationId(
  userId: number | string,
  notificationId: string,
) {
  const dismissed = await getDismissedNotificationIds(userId);

  if (dismissed.includes(notificationId)) {
    return dismissed;
  }

  const next = [...dismissed, notificationId];
  await saveDismissedNotificationIds(userId, next);
  return next;
}

export async function getWatchedBids(
  userId: number | string,
): Promise<WatchedBid[]> {
  const raw = await AsyncStorage.getItem(getWatchedBidsKey(userId));

  if (!raw) {
    return [];
  }

  try {
    return JSON.parse(raw) as WatchedBid[];
  } catch {
    return [];
  }
}

export async function saveWatchedBids(
  userId: number | string,
  watchedBids: WatchedBid[],
) {
  await AsyncStorage.setItem(
    getWatchedBidsKey(userId),
    JSON.stringify(watchedBids),
  );
}

export async function upsertWatchedBid(
  userId: number | string,
  watchedBid: WatchedBid,
) {
  const watchedBids = await getWatchedBids(userId);
  const next = [
    watchedBid,
    ...watchedBids.filter((item) => item.subastaId !== watchedBid.subastaId),
  ];

  await saveWatchedBids(userId, next);
  return next;
}
