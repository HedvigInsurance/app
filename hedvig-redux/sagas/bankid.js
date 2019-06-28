import { call, take, takeLatest, put, select } from 'redux-saga/effects';
import { delay } from 'redux-saga';
import gql from 'graphql-tag';
import {
  API,
  DEPRECATED_BANKID_COLLECT,
  DEPRECATED_BANKID_COLLECT_RESPONSE,
  DEPRECATED_BANKID_COLLECT_COMPLETE,
} from '../actions/types';
import { getMessages } from '../actions/chat';
import { Platform, NativeModules } from 'react-native';

const COLLECT_DELAY_MS = 1000;

const MAX_TRIES = 100000000;
const isDone = (collectResponseBody, tryCount) => {
  if (
    collectResponseBody.bankIdStatus === 'COMPLETE' ||
    tryCount >= MAX_TRIES
  ) {
    return true;
  } else {
    return false;
  }
};

const collectHandler = function*() {
  let state = yield select();
  if (state.deprecatedBankId.referenceId) {
    yield put({
      type: API,
      payload: {
        url: `/hedvig/collect?referenceToken=${
          state.deprecatedBankId.referenceId
        }`,
        method: 'POST',
        body: null,
        SUCCESS: DEPRECATED_BANKID_COLLECT_RESPONSE,
        ERROR: DEPRECATED_BANKID_COLLECT_RESPONSE,
      },
    });
    yield take(DEPRECATED_BANKID_COLLECT_RESPONSE);
    state = yield select();
    if (
      isDone(state.deprecatedBankId.response, state.deprecatedBankId.tryCount)
    ) {
      yield put({ type: DEPRECATED_BANKID_COLLECT_COMPLETE });
      if (Platform.OS === 'android') {
        NativeModules.ActivityStarter.doIsLoggedInProcedure();
      } else {
        NativeModules.NativeRouting.restoreState();
      }
      yield put(getMessages());
    } else {
      yield call(delay, COLLECT_DELAY_MS);
      yield put({
        type: DEPRECATED_BANKID_COLLECT,
        payload: { referenceId: state.deprecatedBankId.referenceId },
      });
    }
  } else {
    throw new Error(
      'No referenceId found in `state.deprecatedBankId.referenceId`',
    );
  }
};

const collectSaga = function*() {
  yield takeLatest(DEPRECATED_BANKID_COLLECT, collectHandler);
};

export { collectSaga };
