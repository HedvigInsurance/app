import { NativeModules } from 'react-native';

import { pushNotificationActions, chatActions } from '../hedvig-redux';
import { Store } from './setupApp';

const handleNotificationOpened = (notificationOpen) => {
  if (!notificationOpen) return;

  const { notification } = notificationOpen;

  if (notification) {
    if (notification.data.TYPE === 'NEW_MESSAGE') {
      NativeModules.NativeRouting.openChat();
      return;
    }
  }
};

export const setupPushNotifications = () => {};
