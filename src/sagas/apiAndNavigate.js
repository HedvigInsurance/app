import { take, takeEvery, put, select } from 'redux-saga/effects';
import { types, chatActions } from '../../hedvig-redux';
import { CHAT_SCREEN_MODAL } from '../../src/navigation/screens/chat/modal';

export const openChat = () => {
  Navigation.showModal({
    stack: {
      children: [CHAT_SCREEN_MODAL],
    },
  });
};

const apiAndNavigateToChat = function*({ payload }) {
  if (payload) {
    yield put({
      type: types.API,
      payload,
    });
    yield take(payload.SUCCESS);
    const state = yield select();
    const intent = state.conversation.intent;
    yield put(chatActions.getMessages({ intent }));
  }

  openChat();
};

const apiAndNavigateToChatSaga = function*() {
  yield takeEvery(types.API_AND_NAVIGATE_TO_CHAT, apiAndNavigateToChat);
};

export { apiAndNavigateToChatSaga };
