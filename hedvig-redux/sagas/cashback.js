import { API, UPDATE_CASHBACK } from '../actions/types';
import * as cashbackActions from '../actions/cashback';
import * as userActions from '../actions/user';
import { take, takeEvery, put } from 'redux-saga/effects';
import { LOADED_USER } from '../actions/types';

const updateCashback = function*({ payload: selectedItem, continuation }) {
  // Use this to return the whole cashbackAlternatives list with selected: true
  // only one the selected item
  // let state = yield select()
  // let cashbackAlternatives = state.cashback.alternatives.map(alternative => {
  //   alternative.selected = selectedItem.id === alternative.id
  //   return alternative
  // })
  yield put({
    type: API,
    payload: {
      method: 'POST',
      url: `/cashback?optionId=${selectedItem.id}`,
      // headers: {
      //   Accept: 'application/json; charset=utf-8',
      //   'Content-Type': 'application/json; charset=utf-8',
      // },
      // body: JSON.stringify(cashbackAlternatives, null, 4),
      SUCCESS: 'UPDATED_CASHBACK',
    },
  });
  yield take('UPDATED_CASHBACK');
  yield put(cashbackActions.getCashbackAlternatives());
  // Also get user profile as this includes some selected cashback info
  yield put(userActions.getCurrentUser());
  if (continuation) {
    yield take(LOADED_USER);
    yield put(continuation());
  }
};

const updateCashbackSaga = function*() {
  yield takeEvery(UPDATE_CASHBACK, updateCashback);
};

export { updateCashbackSaga };
