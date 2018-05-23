import { AsyncStorage } from 'react-native';
import { types } from '../../hedvig-redux';
import { takeEvery, put, call } from 'redux-saga/effects';
import { NavigationActions } from 'react-navigation';
import { SEEN_MARKETING_CAROUSEL_KEY } from '../constants';

const handleLogout = function*() {
  // Delete session from backend, then create a new one
  yield put({
    type: types.API,
    payload: {
      method: 'POST',
      url: '/logout',
      body: null,
      SUCCESS: 'LOGOUT_REQUESTED',
    },
  });

  // Delete existing token from frontend
  yield put({ type: types.DELETE_TOKEN, payload: {} });

  // Delete trackingId from currentUser and reset session in Segment
  yield put({ type: types.DELETE_TRACKING_ID });

  // Get a new token
  yield put({ type: types.AUTHENTICATE, payload: {} });

  // Make sure the onboarding/marketing screens show after you restart app
  yield call(AsyncStorage.removeItem, SEEN_MARKETING_CAROUSEL_KEY);

  yield put(
    NavigationActions.reset({
      index: 0,
      key: null,
      actions: [
        NavigationActions.navigate({
          routeName: 'Marketing',
        }),
      ],
    }),
  );
};

const logoutSaga = function*() {
  yield takeEvery(types.LOGOUT, handleLogout);
};

export { logoutSaga };
