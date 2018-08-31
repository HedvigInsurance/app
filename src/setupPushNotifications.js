import firebase from 'react-native-firebase';
import { Platform } from 'react-native';

import { pushNotificationActions, chatActions } from '../hedvig-redux';
import { openChat } from './sagas/apiAndNavigate';
import { Store } from './setupApp';

const handleNotificationOpened = (notificationOpen) => {
  if (!notificationOpen) return;

  const { notification } = notificationOpen;

  if (notification) {
    if (notification.data.TYPE === 'NEW_MESSAGE') {
      setTimeout(() => openChat(), 500);
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

    if (Platform.OS === 'android') {
      const channel = new firebase.notifications.Android.Channel(
        'hedvig-push',
        'Hedvig Push',
        firebase.notifications.Android.Importance.Max,
      );

      firebase
        .notifications()
        .android.createChannel(channel)
        .then(() => {
          notification.android.setChannelId(channel.channelId);
          notification.android.setAutoCancel(true);
          firebase.notifications().displayNotification(notification);
        });
    } else {
      firebase.notifications().displayNotification(notification);
    }
  };

  firebase.messaging().onTokenRefresh((token) => {
    Store.dispatch(pushNotificationActions.registerPushToken(token));
  });

  firebase.notifications().getToken();

  firebase.notifications().onNotification(handleNotification);

  firebase
    .notifications()
    .getInitialNotification()
    .then(handleNotificationOpened);

  firebase.notifications().onNotificationOpened(handleNotificationOpened);
};
