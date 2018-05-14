import { createStore, applyMiddleware, compose } from 'redux';
import rootReducer from './reducers/index';
import mockMiddleware from './middleware/mock';
import { composeWithDevTools } from 'redux-devtools-extension';
import helloActions from './actions/hello';
import * as insuranceActions from './actions/insurance';
import * as chatActions from './actions/chat';
import * as uploadActions from './actions/upload';
import * as cashbackActions from './actions/cashback';
import * as userActions from './actions/user';
import * as pushNotificationActions from './actions/pushNotification';
import * as dialogActions from './actions/dialog';
import * as eventActions from './actions/events';
import * as listenerActions from './actions/listener';
import * as types from './actions/types';

import createSagaMiddleware from 'redux-saga';
import { rootSaga } from './sagas/index';

import mockChatActions from './actions/mock/chat';

function configureStore({
  initialState,
  additionalReducers,
  additionalMiddleware = [],
  additionalSagas,
  raven = null,
} = {}) {
  let sagaMiddleware = createSagaMiddleware();
  let middlewares;
  if (process.env.NODE_ENV === 'development') {
    middlewares = composeWithDevTools({})(
      applyMiddleware(mockMiddleware, sagaMiddleware, ...additionalMiddleware),
    );
  } else {
    middlewares = compose(
      applyMiddleware(mockMiddleware, sagaMiddleware, ...additionalMiddleware),
    );
  }
  let store = createStore(
    rootReducer(additionalReducers),
    initialState,
    middlewares,
  );
  sagaMiddleware.run(rootSaga(additionalSagas, raven));
  return store;
}

export {
  helloActions,
  types,
  configureStore,
  insuranceActions,
  chatActions,
  mockChatActions,
  uploadActions,
  cashbackActions,
  userActions,
  pushNotificationActions,
  dialogActions,
  eventActions,
  listenerActions,
};
