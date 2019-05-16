import firebase from 'react-native-firebase';

export const hasNotificationsPermission = async () =>
  await firebase.messaging().hasPermission();
