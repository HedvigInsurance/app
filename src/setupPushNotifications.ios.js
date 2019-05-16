import firebase from 'react-native-firebase';
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

export const setupPushNotifications = () => {
  const handleNotification = (notification) => {
    const state = Store.getState();
    Store.dispatch(
      chatActions.getMessages({
        intent: state.conversation.intent,
      }),
    );

    firebase.notifications().displayNotification(notification);
  };

  firebase.messaging().onTokenRefresh((token) => {
    Store.dispatch(pushNotificationActions.registerPushToken(token));
  });

  firebase.notifications().onNotification(handleNotification);

  firebase
    .notifications()
    .getInitialNotification()
    .then(handleNotificationOpened);

  firebase.notifications().onNotificationOpened(handleNotificationOpened);
};
