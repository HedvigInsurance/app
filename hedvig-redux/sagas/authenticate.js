import Config from '@hedviginsurance/react-native-config';
import * as R from 'ramda';
import { put, takeEvery } from 'redux-saga/effects';
import { AUTHENTICATE, RECEIVED_TOKEN, VALIDATE_TOKEN } from '../actions/types';
import { TRACK_SET_IDENTITY } from 'src/features/analytics/actions';
import { getCurrentUser } from '../actions/user';

const authenticate = function*(action) {
  let requestOpts = {
    method: 'POST',
  };
  if (!R.isNil(action.payload) && !R.isEmpty(action.payload)) {
    requestOpts.body = JSON.stringify(action.payload);
    requestOpts.headers = {
      Accept: 'application/json; charset=utf-8',
      'Content-Type': 'application/json; charset=utf-8',
    };
  }
  let authResponse = yield fetch(
    `${Config.API_BASE_URL}/helloHedvig`,
    requestOpts,
  );
  if (authResponse.status === 200) {
    let token = yield authResponse.text();
    yield put({ type: RECEIVED_TOKEN, payload: token });
    yield put(getCurrentUser());
  } else {
    throw new Error('Failed to receive token', authResponse);
  }
};

const authenticateSaga = function*() {
  yield takeEvery(AUTHENTICATE, authenticate);
};

const validateToken = function*(action) {
  let token = action.payload;
  let validateResponse = yield fetch(`${Config.API_BASE_URL}/member/me`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      Accept: 'application/json; charset=utf-8',
    },
  });
  if (validateResponse.status === 200 && token !== null) {
    yield put({ type: RECEIVED_TOKEN, payload: token });
    const validateResponseData = yield validateResponse.json();
    yield put({
      type: TRACK_SET_IDENTITY,
      payload: { userId: validateResponseData.memberId },
    });
  } else {
    yield put({ type: AUTHENTICATE, payload: {} });
  }
};

const validateTokenSaga = function*() {
  yield takeEvery(VALIDATE_TOKEN, validateToken);
};

export { authenticateSaga, validateTokenSaga };
