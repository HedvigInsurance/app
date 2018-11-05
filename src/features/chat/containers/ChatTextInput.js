import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import {
  StyleSheet,
  TextInput,
  Platform,
  View,
  InputAccessoryView,
} from 'react-native';
import firebase from 'react-native-firebase';
import styled from '@sampettersson/primitives';
import color from 'color';

import { chatActions, dialogActions } from '../../../../hedvig-redux';
import { SendButton } from '../components/Button';

import { colors, fonts } from '@hedviginsurance/brand';
import { Provider } from '../components/upload/context';
import { Picker } from '../components/upload/picker';
import { Picker as GiphyPicker } from '../components/giphy-picker/picker';
import {
  Provider as GiphyProvider,
  Consumer as GiphyConsumer,
} from '../components/giphy-picker/context';
import { Buttons } from '../components/pickers/buttons';
import { BlurView } from 'react-native-blur';

const styles = StyleSheet.create({
  textInput: {
    flex: 1,
    alignSelf: 'stretch',
    minHeight: 40,
    maxHeight: 160,
    paddingTop: 10,
    paddingRight: 16,
    paddingBottom: 10,
    paddingLeft: 16,
    marginRight: 8,
    fontSize: 16,
    overflow: 'hidden',
    fontFamily: fonts.CIRCULAR,
  },
});

const BarContainer = styled(View)({
  borderTopWidth: StyleSheet.hairlineWidth,
  borderColor: color(colors.DARK_GRAY).lighten(0.15),
});

const Bar = styled(View)({
  flexDirection: 'row',
  alignItems: 'flex-end',
  paddingTop: 8,
  paddingRight: 8,
  paddingLeft: 8,
  paddingBottom: 8,
});

const TextInputContainer = styled(View)({
  flexDirection: 'row',
  flex: 1,
  backgroundColor: color(colors.WHITE).alpha(0.8),
  borderColor: color(colors.DARK_GRAY).lighten(0.15),
  borderWidth: StyleSheet.hairlineWidth,
  borderRadius: 24,
  alignItems: 'flex-end',
});

class ChatTextInput extends React.Component {
  static propTypes = {
    message: PropTypes.object,
    onChange: PropTypes.func.isRequired,
    isSending: PropTypes.bool,
    inputValue: PropTypes.string,
  };

  static defaultProps = {
    isSending: false,
    inputValue: '',
  };

  state = {
    height: 0,
    inputValue: '',
  };

  requestPush = () => {
    if (this.props.message.header.shouldRequestPushNotifications) {
      this.props.requestPushNotifications();
    }
  };

  _send = (message) => {
    this.requestPush();
    if (!this.props.isSending) {
      const inputValue = String(
        typeof message === 'string' ? message : this.state.inputValue,
      );
      this.ref.clear();
      this.props.send(this.props.message, inputValue);
    }
  };

  sendFileMessage = (key) => {
    this.requestPush();
    this.props.send(
      this.props.message,
      JSON.stringify({
        type: 'file',
        key: key,
      }),
    );
  };

  _onTextChange = (text) => {
    this.setState({ inputValue: text });
  };

  render() {
    const richTextChatCompatible = this.props.message.header
      .richTextChatCompatible;
    return (
      <Provider>
        <GiphyProvider>
          <InputAccessoryView>
            <BarContainer>
              <View>
                <Bar>
                  {richTextChatCompatible && <Buttons />}
                  <TextInputContainer>
                    <TextInput
                      ref={(ref) => (this.ref = ref)}
                      style={[styles.textInput]}
                      autoFocus
                      autoCapitalize="none"
                      placeholder="Skriv här..."
                      underlineColorAndroid="transparent"
                      onChangeText={this._onTextChange}
                      multiline={richTextChatCompatible}
                      returnKeyType={
                        richTextChatCompatible ? 'default' : 'send'
                      }
                      onSubmitEditing={() => {
                        if (!richTextChatCompatible) {
                          this._send();
                        }
                      }}
                      enablesReturnKeyAutomatically
                    />
                    <SendButton
                      onPress={this._send}
                      disabled={!this.state.inputValue}
                      size="small"
                    />
                  </TextInputContainer>
                </Bar>
                <Picker sendMessage={this.sendFileMessage} />
                <GiphyPicker sendMessage={this._send} />
              </View>
            </BarContainer>
          </InputAccessoryView>
        </GiphyProvider>
      </Provider>
    );
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    onChange: (value) =>
      dispatch({ type: 'CHAT/SET_INPUT_VALUE', payload: value }),
    send: (message, text) =>
      dispatch(
        chatActions.sendChatResponse(message, {
          text,
        }),
      ),
    sendFile: (message, bodyOverride) =>
      dispatch(chatActions.sendChatResponse(message, bodyOverride)),
    requestPushNotifications: async () => {
      if (Platform.OS === 'android') {
        return dispatch({
          type: 'PUSH_NOTIFICATIONS/REQUEST_PUSH',
        });
      }

      const enabled = await firebase.messaging().hasPermission();

      if (!enabled) {
        dispatch(
          dialogActions.showDialog({
            title: 'Notifikationer',
            paragraph:
              'Slå på notiser så att du inte missar när Hedvig svarar!',
            confirmButtonTitle: 'Slå på',
            dismissButtonTitle: 'Inte nu',
            onConfirm: () =>
              dispatch({
                type: 'PUSH_NOTIFICATIONS/REQUEST_PUSH',
              }),
            onDismiss: () => {},
          }),
        );
      } else {
        dispatch({
          type: 'PUSH_NOTIFICATIONS/REQUEST_PUSH',
        });
      }
    },
  };
};

const ChatTextInputContainer = connect(
  null,
  mapDispatchToProps,
)(ChatTextInput);

export default ChatTextInputContainer;
