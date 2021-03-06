import { AsyncStorage } from 'react-native';
import { API, EVENT, LOADED_MESSAGES } from '../actions/types';
import { take, takeEvery, put, call } from 'redux-saga/effects';
import * as chatActions from '../actions/chat';

const handleEvent = function*(action) {
  yield put({
    type: API,
    payload: {
      url: '/hedvig/onboarding/offerClosed',
      method: 'POST',
      headers: {
        Accept: 'application/json; charset=utf-8',
        'Content-Type': 'application/json; charset=utf-8',
      },
      SUCCESS: 'POST_EVENT_SUCCESS',
    },
  });
  yield take('POST_EVENT_SUCCESS');

  if (action.payload.getMessagesAfter) {
    yield put(chatActions.getMessages());
    if (action.payload.showLoadingIndicator) {
      yield take(LOADED_MESSAGES);
    }
  }

  yield call(AsyncStorage.removeItem, '@hedvig:isViewingOffer');
};

// Warning: Expected to only be used for the offer modal close event
// Only Offer closeModal sends a type MODAL_CLOSED event
const handleEventSaga = function*() {
  yield takeEvery(EVENT, handleEvent);
};

export { handleEventSaga };
