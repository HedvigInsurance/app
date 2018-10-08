import { AppState, AsyncStorage } from 'react-native';
import { Sentry } from 'react-native-sentry';
import createRavenMiddleware from 'raven-for-redux';
import { persistReducer, persistStore } from 'redux-persist';
import { createMiddleware } from 'redux-beacon';
import logger from '@redux-beacon/logger';
import uuidv4 from 'uuid/v4';
import Branch from 'react-native-branch';
import Config from '@hedviginsurance/react-native-config';
import { Navigation } from 'react-native-navigation';
import { openMockLayout } from 'src/navigation/mock';

import * as hedvigRedux from '../hedvig-redux';

import { apiAndNavigateToChatSaga } from './sagas/apiAndNavigate';
import { tokenStorageSaga } from './sagas/TokenStorage';
import { logoutSaga } from './sagas/logout';
import { appStateChange } from './actions/appState';
import appStateChangeReducer from './reducers/appState';
import statusMessageReducer from './reducers/statusMessage';
import routerReducer from './reducers/router';
import conversationReducer from './reducers/conversation';
import { bankIdReducer } from './features/bankid/reducer';
import { offerReducer } from './features/offer/state/reducer';
import { marketingReducer } from './features/marketing/reducer';
import { appStateSaga } from './sagas/appState';
import { keyboardSaga } from './sagas/keyboard';
import { handleCheckoutSaga } from './features/offer/state/saga';
import {
  bankIdSignSaga,
  bankIdCollectSaga,
  bankIdSignCancelSaga,
  bankIdCollectCompleteSaga,
  bankIdAppStateChangeSaga,
} from './features/bankid/saga';
import { requestPushSaga, registerPushSaga } from './sagas/pushNotifications';
import { chatStartSaga, chatLoginSaga } from './features/marketing/saga';
import { DEEP_LINK_OPENED } from './features/deep-linking/actions';
import { getOrLoadToken } from './services/TokenStorage';
import {
  setTrackingIdentitySaga,
  trackDeepLinkOpenedSaga,
} from './features/analytics/saga';
import analyticsReducer from './features/analytics/reducer';
import {
  trackEvent,
  trackScreenView,
  resetSession,
  identify,
  SegmentReduxTarget,
} from './features/analytics/SegmentRedux';
import {
  SegmentTracker,
  SemanticEvents,
} from './features/analytics/SegmentTracker';
import {
  TRACK_SET_IDENTITY,
  TRACK_SET_ORDER_ID,
  TRACK_INSTALL_ATTRIBUTED,
  TRACK_DEEP_LINK_OPENED,
  TRACK_OFFER_OPENED,
  TRACK_OFFER_CLOSED,
  TRACK_OFFER_STEP_VIEWED,
  TRACK_OFFER_STEP_COMPLETED,
  TRACK_OFFER_SIGNED,
  TRACK_PAYMENT_ADDED,
} from './features/analytics/actions';
import {
  MARKETING_SET_ACTIVE_SCREEN,
  MARKETING_CHAT_LOGIN,
  MARKETING_CHAT_START,
} from './features/marketing/actions';

let SentryInstance = Sentry;
let ravenMiddleware;

if (!__DEV__) {
  SentryInstance.config(Config.SENTRY_DSN, {
    environment: Config.ENVIRONMENT,
    deactivateStacktraceMerging: false,
  }).install();

  ravenMiddleware = createRavenMiddleware(SentryInstance, {
    stateTransformer: (state) => ({ user: state.user.currentUser }),
  });
} else {
  SentryInstance = {
    captureException: (e) => console.error(e), // eslint-disable-line no-console
  };
}

const eventsMap = {
  'Navigation/NAVIGATE': trackScreenView(({ payload: { screenName } }) => ({
    screenName,
  })),
  [hedvigRedux.types.DELETE_TRACKING_ID]: resetSession(),
  [TRACK_SET_IDENTITY]: identify(({ payload }) => ({
    userId: payload.userId,
    customTraits: payload.customTraits,
  })),
  [TRACK_INSTALL_ATTRIBUTED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Mobile.InstallAttributed,
    customProperties: payload,
  })),
  [TRACK_DEEP_LINK_OPENED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Mobile.DeepLinkOpened,
    customProperties: payload,
  })),
  [TRACK_OFFER_OPENED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Ecommerce.CheckoutStarted,
    customProperties: payload,
  })),
  [TRACK_OFFER_CLOSED]: trackEvent(({ payload }) => ({
    eventName: 'Checkout Closed',
    customProperties: payload,
  })),
  [TRACK_OFFER_STEP_VIEWED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Ecommerce.CheckoutStepViewed,
    customProperties: payload,
  })),
  [TRACK_OFFER_STEP_COMPLETED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Ecommerce.CheckoutStepCompleted,
    customProperties: payload,
  })),
  [TRACK_OFFER_SIGNED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Ecommerce.OrderCompleted,
    customProperties: payload,
  })),
  [TRACK_PAYMENT_ADDED]: trackEvent(({ payload }) => ({
    eventName: SemanticEvents.Ecommerce.PaymentInfoEntered,
    customProperties: payload,
  })),
  [MARKETING_SET_ACTIVE_SCREEN]: trackEvent(({ payload, analytics }) => ({
    eventName: SemanticEvents.Ecommerce.PromotionViewed,
    customProperties: {
      step: payload.index,
      ...analytics,
    },
  })),
  [MARKETING_CHAT_LOGIN]: trackEvent(({ analytics }) => ({
    eventName: 'Log in clicked',
    customProperties: {
      ...analytics,
    },
  })),
  [MARKETING_CHAT_START]: trackEvent(({ analytics }) => ({
    eventName: 'Start chat clicked',
    customProperties: { ...analytics },
  })),
};

const segmentMiddleware = createMiddleware(
  eventsMap,
  SegmentReduxTarget(
    Config.SEGMENT_ANDROID_WRITE_KEY,
    Config.SEGMENT_IOS_WRITE_KEY,
    SegmentTracker,
  ),
  __DEV__ && {
    logger,
  },
);

const conversationPersistConfig = {
  key: 'conversation',
  storage: AsyncStorage,
  whitelist: ['intent'],
};
const analyticsPersistConfig = {
  key: 'analytics',
  storage: AsyncStorage,
  whitelist: ['orderId'],
};
const additionalMiddleware = [segmentMiddleware];
if (ravenMiddleware) {
  additionalMiddleware.push(ravenMiddleware);
}

let store = hedvigRedux.configureStore({
  additionalReducers: {
    conversation: persistReducer(
      conversationPersistConfig,
      conversationReducer,
    ),
    analytics: persistReducer(analyticsPersistConfig, analyticsReducer),
    appState: appStateChangeReducer,
    status: statusMessageReducer,
    router: routerReducer,
    bankid: bankIdReducer,
    offer: offerReducer,
    marketing: marketingReducer,
  },
  additionalSagas: [
    apiAndNavigateToChatSaga,
    appStateSaga,
    keyboardSaga,
    tokenStorageSaga,
    logoutSaga,
    chatStartSaga,
    chatLoginSaga,
    requestPushSaga,
    registerPushSaga,
    handleCheckoutSaga,
    bankIdSignSaga,
    bankIdCollectSaga,
    bankIdSignCancelSaga,
    bankIdCollectCompleteSaga,
    bankIdAppStateChangeSaga,
    setTrackingIdentitySaga,
    trackDeepLinkOpenedSaga,
  ],
  additionalMiddleware,
  raven: SentryInstance,
});

Navigation.events().registerComponentDidAppearListener(({ componentName }) => {
  store.dispatch({
    type: 'Navigation/NAVIGATE',
    payload: {
      screenName: componentName,
    },
  });
});

let persistor = persistStore(store);

Branch.subscribe(({ error, params }) => {
  if (error) {
    return;
  }

  const url = params['+non_branch_link'] || '';

  if (url.includes('mock')) {
    openMockLayout(url);
  }

  store.dispatch({
    type: DEEP_LINK_OPENED,
    payload: {
      branchParams: params,
    },
  });
});

const state = store.getState();

store.dispatch({ type: 'PUSH_NOTIFICATIONS/REGISTER_PUSH' });

// Persist analytics orderId for reliable ecommerce event tracking
// Storing in AsyncStorage so you get a new id each time you re-install the app
if (!state.analytics.orderId) {
  store.dispatch({
    type: TRACK_SET_ORDER_ID,
    payload: {
      orderId: uuidv4(),
    },
  });
}

getOrLoadToken(store.dispatch);

const handleAppStateChanged = (nextAppState) =>
  store.dispatch(appStateChange(nextAppState));

AppState.addEventListener('change', handleAppStateChanged);

export { SentryInstance as Raven, store as Store, persistor as Persistor };
