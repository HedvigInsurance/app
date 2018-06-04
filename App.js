import 'babel-polyfill';
import React from 'react';
import { AppState, Platform, AsyncStorage } from 'react-native';
import { Provider } from 'react-redux';
import { Notifications, DangerZone } from 'expo';
import Sentry from 'sentry-expo';
import createRavenMiddleware from 'raven-for-redux';
import {
  ActionSheetProvider,
  connectActionSheet,
} from '@expo/react-native-action-sheet';
import { persistReducer, persistStore } from 'redux-persist';
import { PersistGate } from 'redux-persist/es/integration/react';
import { createMiddleware } from 'redux-beacon';
import logger from '@redux-beacon/logger';
import uuidv4 from 'uuid/v4';

import * as hedvigRedux from './hedvig-redux';
import { envConfig } from './hedvig-redux/env-config';

import { theme } from './src/style-theme';
import nav from './src/reducers/nav';
import { apiAndNavigateToChatSaga } from './src/sagas/apiAndNavigate';
import { tokenStorageSaga } from './src/sagas/TokenStorage';
import { logoutSaga } from './src/sagas/logout';
import { ThemeProvider } from 'styled-components';
import { Router } from './src/components/navigation/Router';
import { ErrorBoundary } from './src/components/ErrorBoundary';
import WithAssets from './src/components/WithAssets';
import { Loader } from './src/components/Loader';
import { appStateChange } from './src/actions/appState';
import appStateChangeReducer from './src/reducers/appState';
import statusMessageReducer from './src/reducers/statusMessage';
import routerReducer from './src/reducers/router';
import conversationReducer from './src/reducers/conversation';
import { bankIdReducer } from './src/features/bankid/reducer';
import { offerReducer } from './src/features/offer/reducer';
import { marketingReducer } from './src/features/marketing/reducer';
import { appStateSaga } from './src/sagas/appState';
import { keyboardSaga } from './src/sagas/keyboard';
import { navigationSaga } from './src/sagas/navigation';
import { handleCheckoutSaga } from './src/features/offer/saga';
import {
  bankIdSignSaga,
  bankIdCollectSaga,
  bankIdSignCancelSaga,
  bankIdCollectCompleteSaga,
  bankIdAppStateChangeSaga,
} from './src/features/bankid/saga';
import {
  requestPushSaga,
  registerPushSaga,
} from './src/sagas/pushNotifications';
import { chatStartSaga, chatLoginSaga } from './src/features/marketing/saga';
import { DEEP_LINK_OPENED } from './src/features/deep-linking/actions';
import { getOrLoadToken } from './src/services/TokenStorage';
import navigationMiddleware from './src/middleware/navigation';
import {
  setTrackingIdentitySaga,
  trackDeepLinkOpenedSaga,
} from './src/features/analytics/saga';
import analyticsReducer from './src/features/analytics/reducer';
import {
  trackEvent,
  trackScreenView,
  resetSession,
  identify,
  SegmentReduxTarget,
} from './src/features/analytics/SegmentRedux';
import {
  SegmentTracker,
  SemanticEvents,
} from './src/features/analytics/SegmentTracker';
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
} from './src/features/analytics/actions';

const { Branch } = DangerZone;

// Fix HMR
let SentryInstance = Sentry;
let ravenMiddleware;
if (!__DEV__) {
  SentryInstance.config(envConfig.SENTRY_DSN, {
    environment: envConfig.ENVIRONMENT,
  }).install();

  ravenMiddleware = createRavenMiddleware(SentryInstance, {
    stateTransformer: (state) => ({ user: state.user.currentUser }),
  });
} else {
  SentryInstance = {
    captureException() {},
  };
}

// Map Redux actions to Segment analytics event
const eventsMap = {
  'Navigation/NAVIGATE': trackScreenView(({ routeName }) => ({
    screenName: routeName,
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
};

const segmentMiddleware = createMiddleware(
  eventsMap,
  SegmentReduxTarget(
    envConfig.SEGMENT_ANDROID_WRITE_KEY,
    envConfig.SEGMENT_IOS_WRITE_KEY,
    SegmentTracker,
  ),
  {
    logger,
  },
);

export class App extends React.Component {
  constructor() {
    super();
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
    const additionalMiddleware = [segmentMiddleware, navigationMiddleware];
    if (ravenMiddleware) {
      additionalMiddleware.push(ravenMiddleware);
    }
    this.store = hedvigRedux.configureStore({
      additionalReducers: {
        nav,
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
        navigationSaga,
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
    window.store = this.store;
    this.persistor = persistStore(this.store);
    this._unsubscribeFromBranch = null;
  }

  _handleAppStateChange = (nextAppState) => {
    this.store.dispatch(appStateChange(nextAppState));
  };

  componentDidMount() {
    const state = this.store.getState();
    if (Platform.OS === 'android') {
      this.store.dispatch({ type: 'PUSH_NOTIFICATIONS/REGISTER_PUSH' });
    }

    // Persist analytics orderId for reliable ecommerce event tracking
    // Storing in AsyncStorage so you get a new id each time you re-install the app
    if (!state.analytics.orderId) {
      this.store.dispatch({
        type: TRACK_SET_ORDER_ID,
        payload: {
          orderId: uuidv4(),
        },
      });
    }

    AppState.addEventListener('change', this._handleAppStateChange);
    getOrLoadToken(this.store.dispatch);

    this.store.dispatch(
      hedvigRedux.listenerActions.addListener(
        hedvigRedux.types.SHOW_ACTION_SHEET,
        ({ payload: { options, callback } }) =>
          this.props.showActionSheetWithOptions(options, callback),
      ),
    );

    this._unsubscribeFromBranch = Branch.subscribe(({ error, params }) => {
      if (error) {
        SentryInstance.captureException(error);
        console.error('Error from Branch', error); // eslint-disable-line no-console
        return;
      }

      this.store.dispatch({
        type: DEEP_LINK_OPENED,
        payload: {
          branchParams: params,
        },
      });
    });

    Notifications.addListener(this._handleNotification);
  }

  _handleNotification = () => {
    const state = this.store.getState();
    this.store.dispatch(
      hedvigRedux.chatActions.getMessages({
        intent: state.conversation.intent,
      }),
    );
  };

  componentWillUnmount() {
    AppState.removeEventListener('change', this._handleAppStateChange);

    if (this._unsubscribeFromBranch) {
      this._unsubscribeFromBranch();
      this._unsubscribeFromBranch = null;
    }
  }

  render() {
    return (
      <ErrorBoundary raven={SentryInstance}>
        <WithAssets>
          <ThemeProvider theme={theme}>
            <Provider store={this.store}>
              <PersistGate loading={<Loader />} persistor={this.persistor}>
                <Router />
              </PersistGate>
            </Provider>
          </ThemeProvider>
        </WithAssets>
      </ErrorBoundary>
    );
  }
}

export class AppWithActionSheet extends React.Component {
  constructor() {
    super();
    this.WrappedComponent = connectActionSheet(App);
  }
  render() {
    return (
      <ActionSheetProvider>
        <this.WrappedComponent />
      </ActionSheetProvider>
    );
  }
}

export default AppWithActionSheet;
