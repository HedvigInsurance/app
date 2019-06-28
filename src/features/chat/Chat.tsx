import * as React from 'react';
import { connect } from 'react-redux';
import {
  View,
  AppState,
  Platform,
  NativeModules,
  NativeEventEmitter,
} from 'react-native';
import styled from '@sampettersson/primitives';
import { Mount, Update, Unmount } from 'react-lifecycle-components';
import { Container, EffectMap, EffectProps } from 'constate';
import gql from 'graphql-tag';

import MessageList from './containers/MessageList';
import InputComponent from './components/InputComponent';
import { Loader } from '../../components/Loader';
import { chatActions, dialogActions } from '../../../hedvig-redux';
import * as selectors from './state/selectors';
import {
  RESTART_BUTTON,
  CLOSE_BUTTON,
  GO_TO_DASHBOARD_BUTTON,
  SHOW_OFFER_BUTTON,
} from '../../navigation/screens/chat/buttons';

import { Message } from './types';
import { KeyboardAvoidingOnAndroid } from 'src/components/KeyboardAvoidingOnAndroid';
import { PixelRatio } from 'react-native';
import { client } from 'src/graphql/client';

import { colors } from '@hedviginsurance/brand';

interface ChatProps {
  onboardingDone: boolean;
  isModal: boolean;
  showReturnToOfferButton: boolean;
  componentId: string;
  intent: string;
  messages: Array<Message>;
  getAvatars: () => void;
  getMessages: (intent: string) => void;
  resetConversation: () => void;
}

interface State {
  longPollTimeout: number | null;
}

const initialState: State = {
  longPollTimeout: null,
};

interface Effects {
  startPolling: (getMessages: (intent: string) => void, intent: string) => void;
  stopPolling: () => void;
}

const effects: EffectMap<State, Effects> = {
  startPolling: (getMessages, intent) => ({
    setState,
    state,
  }: EffectProps<State>) => {
    if (!state.longPollTimeout) {
      setState(() => ({
        longPollTimeout: setInterval(() => {
          getMessages(intent);
        }, 15000),
      }));
    }
  },
  stopPolling: () => ({ setState, state }: EffectProps<State>) => {
    if (state.longPollTimeout) {
      clearInterval(state.longPollTimeout);
      setState(() => ({
        longPollTimeout: null,
      }));
    }
  },
};

const Messages = styled(View)({
  flex: 1,
  alignSelf: 'stretch',
});

const BackgroundView = styled(View)({
  backgroundColor: colors.OFF_WHITE,
  flex: 1,
});

const Response = styled(View)({
  alignItems: 'stretch',
  paddingTop: 0,
});

const showOffer = async (componentId: string) => {
  if (Platform.OS === 'android') {
    NativeModules.ActivityStarter.navigateToOfferFromChat();
    return;
  }

  NativeModules.NativeRouting.showOffer();
};

const handleAppStateChange = (
  appState: string,
  getMessages: (intent: string) => void,
  intent: string,
) => {
  if (appState === 'active') {
    getMessages(intent);
  }
};

const KeyboardAvoidingOnAndroidIfModal: React.SFC<{ isModal: boolean }> = ({
  children,
  isModal,
}) =>
  isModal ? (
    <KeyboardAvoidingOnAndroid
      additionalPadding={PixelRatio.getPixelSizeForLayoutSize(14)}
    >
      {children}
    </KeyboardAvoidingOnAndroid>
  ) : (
    <>{children}</>
  );

const Chat: React.SFC<ChatProps> = ({
  onboardingDone = false,
  isModal,
  showReturnToOfferButton,
  componentId,
  intent,
  messages,
  getAvatars,
  getMessages,
  resetConversation,
}) => (
  <Container effects={effects} initialState={initialState}>
    {({ startPolling, stopPolling }) => (
      <BackgroundView>
        <Mount
          on={() => {
            getMessages(intent);
            getAvatars();
            AppState.addEventListener('change', (appState) => {
              handleAppStateChange(appState, getMessages, intent);
            });
            startPolling(getMessages, intent);

            const nativeRoutingEvents = new NativeEventEmitter(
              NativeModules.NativeRouting,
            );

            nativeRoutingEvents.addListener('NativeRoutingRestartChat', () => {
              resetConversation();
            });
          }}
        >
          {null}
        </Mount>
        <Update
          was={() => {
            if (
              messages &&
              messages[0].id == 'message.login.with.mail.passwrod.success'
            ) {
              client
                .mutate({
                  mutation: gql`
                    mutation EmailSign {
                      emailSign
                    }
                  `,
                })
                .then(() => {
                  NativeModules.NativeRouting.presentLoggedIn();
                });
            }

            startPolling(getMessages, intent);
          }}
          watched={messages}
        >
          {null}
        </Update>
        <Unmount
          on={() => {
            AppState.addEventListener('change', (appState) => {
              handleAppStateChange(appState, getMessages, intent);
            });
            stopPolling();
          }}
        >
          {null}
        </Unmount>
        <KeyboardAvoidingOnAndroidIfModal isModal={isModal}>
          <Messages>
            {messages.length ? (
              <MessageList componentId={componentId} />
            ) : (
              <Loader />
            )}
          </Messages>
          <Response>
            <InputComponent
              showOffer={() => showOffer(componentId)}
              messages={messages}
            />
          </Response>
        </KeyboardAvoidingOnAndroidIfModal>
      </BackgroundView>
    )}
  </Container>
);

const mapStateToProps = (state: any) => {
  return {
    messages: state.chat.messages,
    showReturnToOfferButton: selectors.shouldShowReturnToOfferScreenButton(
      state,
    ),
    onboardingDone: selectors.isOnboardingDone(state),
  };
};

const mapDispatchToProps = (dispatch: any) => {
  return {
    getMessages: (intent: null | string) =>
      dispatch(
        chatActions.getMessages({
          intent,
        }),
      ),
    getAvatars: () => dispatch(chatActions.getAvatars()),
    resetConversation: () =>
      dispatch(
        dialogActions.showDialog({
          title: 'Vill du börja om?',
          paragraph:
            'Om du trycker ja så börjar\nkonversationen om från början',
          confirmButtonTitle: 'Ja',
          dismissButtonTitle: 'Nej',
          onConfirm: () => dispatch(chatActions.resetConversation()),
          onDismiss: () => {},
        }),
      ),
    editLastResponse: () => dispatch(chatActions.editLastResponse()),
  };
};

const ChatContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Chat);

export default ChatContainer;
export { Chat as PureChat };
