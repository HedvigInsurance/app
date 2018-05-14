import { delay } from 'redux-saga';
import { put, select, take, takeEvery } from 'redux-saga/effects';
import { API, API_ERROR, STATUS_MESSAGE } from '../actions/types';
import { envConfig } from '../env-config';

const api = function*(action) {
  let state = yield select();
  if (!state.authentication.token) {
    yield take('RECEIVED_TOKEN');
    state = yield select();
  }
  let token = state.authentication.token;

  let data;
  let response;
  let tries = 0;
  const HTTP_NO_CONTENT = 204;
  const HTTP_BAD_REQUEST = 400;
  const HTTP_UNAUTHORIZED = 401;
  while (tries < 5) {
    try {
      const { url } = action.payload;
      const requestOptions = {
        method: action.payload.method,
        headers: Object.assign(
          { Authorization: `Bearer ${token}` },
          action.payload.headers,
        ),
      };
      if (action.payload.body) {
        requestOptions.body = action.payload.body;
      }
      response = yield fetch(envConfig.API_BASE_URL + url, requestOptions);

      if (response.status === HTTP_UNAUTHORIZED) {
        yield put({
          type: 'API/UNAUTHORIZED',
        });
        return;
      } else if (response.status >= HTTP_BAD_REQUEST) {
        data = yield response.json();
        throw new Error(
          `Network communication exception, status code: ${
            response.status
          } for action: ${JSON.stringify(
            action,
          )}, response was: ${JSON.stringify(response)}`,
        );
      } else if (response.status !== HTTP_NO_CONTENT) {
        data = yield response.json();
      } else {
        data = null;
      }

      yield put({ type: action.payload.SUCCESS, payload: data });
      if (action.statusMessage) {
        yield put({ type: STATUS_MESSAGE, message: action.statusMessage });
      }

      return;
    } catch (e) {
      tries += 1;

      if (action.payload.method !== 'GET' || tries > 5) {
        yield put({
          type: action.payload.ERROR || API_ERROR,
          payload: data || e.toString(),
        });

        // Client should gracefully handle bad requests (e.g. validation errors)
        // TODO: Extend this to all 4xx status codes
        if (!data || response.status !== HTTP_BAD_REQUEST) {
          // Exceptions are captured in Sentry
          throw e;
        } else {
          // Don't retry HTTP_BAD_REQUEST
          return;
        }
      } else {
        console.error('Was forced to retry request, tries: ', tries); // eslint-disable-line no-console
        yield delay(500);
      }
    }
  }
};

const apiSaga = function*() {
  yield takeEvery(API, api);
};

export { apiSaga };
