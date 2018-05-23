import { takeEvery, put, take } from 'redux-saga/effects';
import { getMessages } from '../actions/chat';
import { TRACK_PAYMENT_ADDED } from '../../src/features/analytics/actions';

const startPayment = function*(action) {
  yield put({
    type: 'PAYMENT/RESET_PAYMENT_STATE',
  });
  yield put({
    type: 'API',
    payload: {
      url: `/hedvig/trigger/${action.payload.id}`,
      method: 'POST',
      body: null,
      SUCCESS: 'PAYMENT/RETRIEVE_IFRAME_URL',
    },
  });
  const res = yield take(['PAYMENT/RETRIEVE_IFRAME_URL', 'API_ERROR']);
  yield put({
    type: 'PAYMENT/RECEIVED_IFRAME_URL',
    payload: { url: res.payload.url },
  });
};

const finalizePayment = function*(action) {
  yield put({
    type: 'API',
    payload: {
      url: '/hedvig/trustlyClosed',
      method: 'POST',
      SUCCESS: 'PAYMENT/FINALIZE_PAYMENT_SUCCESS',
    },
  });

  yield take(['PAYMENT/FINALIZE_PAYMENT_SUCCESS', 'API_ERROR']);
  yield put({
    type: TRACK_PAYMENT_ADDED,
    payload: {
      payment_method: 'Trustly',
    },
  });
  yield put(getMessages());
  return action.payload.onFinish();
};

export const startPaymentSaga = function*() {
  yield takeEvery('PAYMENT/REQUEST_PAYMENT_REGISTRATION', startPayment);
};

export const finalizePaymentSaga = function*() {
  yield takeEvery(
    [
      'PAYMENT/ON_PAYMENT_SUCCESS',
      'PAYMENT/ON_PAYMENT_FAILURE',
      'PAYMENT/CANCEL_PAYMENT',
    ],
    finalizePayment,
  );
};
