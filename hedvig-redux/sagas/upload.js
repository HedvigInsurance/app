import Config from '@hedviginsurance/react-native-config';
import { put, select, takeEvery } from 'redux-saga/effects';
import uuidv4 from 'uuid/v4';
import { UPLOAD, UPLOAD_STARTED, UPLOAD_SUCCEEDED } from '../actions/types';
import { upload } from '../services/Upload';

const uploadHandler = function*(action) {
  if (action.payload.addToken) {
    const state = yield select();
    const token = state.authentication.token;
    action.payload.headers.Authorization = `Bearer ${token}`;
  }
  const {
    body: { uri, type, fileExtension = 'jpg' },
  } = action.payload;
  const formData = new FormData();
  formData.append('key', `${uuidv4()}.${fileExtension}`);

  formData.append('file', {
    uri,
    type,
  });
  action.payload.body = formData;
  yield put({ type: UPLOAD_STARTED, payload: action.payload });
  const response = yield upload(
    action.payload.uploadUrl || Config.UPLOAD_URL,
    action.payload,
    () => {}, // Upload progress report function
  );

  const uploadedUrl = response.target.getResponseHeader('Location');

  yield put({
    type: UPLOAD_SUCCEEDED,
    payload: Object.assign({}, action.payload, { uploadedUrl }),
  });
  if (action.payload.successActionCreator) {
    yield put(action.payload.successActionCreator(uploadedUrl));
  }
};

const uploadSaga = function*() {
  yield takeEvery(UPLOAD, uploadHandler);
};

export { uploadSaga };
