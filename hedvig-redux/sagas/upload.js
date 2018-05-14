import { put, select, takeEvery } from 'redux-saga/effects';
import uuidv4 from 'uuid/v4';
import { UPLOAD, UPLOAD_STARTED, UPLOAD_SUCCEEDED } from '../actions/types';
import { envConfig } from '../env-config';
import { upload } from '../services/Upload';

const { UPLOAD_URL } = envConfig;

const uploadHandler = function*(action) {
  if (action.payload.addToken) {
    const state = yield select();
    const token = state.authentication.token;
    action.payload.headers.Authorization = `Bearer ${token}`;
  }
  if (action.payload.body) {
    // REACT NATIVE
    let {
      body: { uri, type, fileExtension = 'jpg' },
    } = action.payload;
    let formData = new FormData();
    formData.append('key', `${uuidv4()}.${fileExtension}`);
    formData.append('file', {
      uri,
      type,
    });
    action.payload.body = formData;
  } else if (action.payload.fileList) {
    // REACT WEB
    let fileExtension = action.payload.fileList[0].name.split('.')[1];
    let formData = new FormData();
    formData.append('key', `${uuidv4()}.${fileExtension}`); // This has to come BEFORE `file` because xhr is lol
    formData.append('file', action.payload.fileList[0]);
    action.payload.body = formData;
  } else {
    throw Error(
      'Either action.payload.body or action.payload.fileList must be specified',
    );
  }
  try {
    yield put({ type: UPLOAD_STARTED, payload: action.payload });
    let response = yield upload(
      action.payload.uploadUrl || UPLOAD_URL,
      action.payload,
      () => {}, // Upload progress report function
    );

    let uploadedUrl = response.target.getResponseHeader('Location'); // <-- This works on both Web and Native

    yield put({
      type: UPLOAD_SUCCEEDED,
      payload: Object.assign({}, action.payload, { uploadedUrl }),
    });
    if (action.payload.successActionCreator) {
      yield put(action.payload.successActionCreator(uploadedUrl));
    }
  } catch (e) {
    // TODO: Make this report an error to the user and to Sentry
    console.error('Upload failed', e); // eslint-disable-line no-console
  }
};

const uploadSaga = function*() {
  yield takeEvery(UPLOAD, uploadHandler);
};

export { uploadSaga };
