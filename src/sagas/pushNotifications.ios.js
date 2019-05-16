import { takeLatest, call, put } from 'redux-saga/effects';
import firebase from 'react-native-firebase';
import { pushNotificationActions } from '../../hedvig-redux';
import { Platform } from 'react-native';

const requestPush = function*() {
  if (Platform.OS === 'android') {
    return;
  }
  const error = yield call([firebase.messaging(), 'requestPermission']);
  if (error) {
    return yield put({ type: 'PUSH_NOTIFICATIONS/REQUEST_NOT_GRANTED' });
  }
  return yield call(registerPush);
};

const registerPush = function*() {
  if (Platform.OS === 'android') {
    return;
  }
  const token = yield call([firebase.messaging(), 'getToken']);
  if (!token) {
    return;
  }
  return yield put(pushNotificationActions.registerPushToken(token));
};

export const requestPushSaga = function*() {
  yield takeLatest('PUSH_NOTIFICATIONS/REQUEST_PUSH', requestPush);
};

export const registerPushSaga = function*() {
  yield takeLatest('PUSH_NOTIFICATIONS/REGISTER_PUSH', registerPush);
};
