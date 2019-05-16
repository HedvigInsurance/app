import React from 'react';
import {
  View,
  StyleSheet,
  FlatList,
  Text,
  Platform,
  NativeModules,
} from 'react-native';
import { connect } from 'react-redux';
import KeyboardSpacer from '@hedviginsurance/react-native-keyboard-spacer';
import { isIphoneX } from 'react-native-iphone-x-helper';

import { StyledAvatarContainer } from '../styles/chat';
import Avatar from '../containers/Avatar';
import LoadingIndicator from '../containers/LoadingIndicator';
import { RichMessage } from '../components/rich-message';
import { OptionInputComponent } from '../components/InputComponent';
import { NEW_OFFER_SCREEN } from 'src/navigation/screens/new-offer';

import { InputHeightContainer } from './InputHeight';

const styles = StyleSheet.create({
  scrollContent: {
    flex: 1,
  },
  statusMessage: {
    fontFamily: 'CircularStd-Book',
    fontSize: 12,
    textAlign: 'right',
    paddingRight: 4,
    color: '#8a8a99',
  },
  userMessageOuterContainer: { maxWidth: '88%' },
  userMessageInnerContainer: {
    flexDirection: 'row-reverse',
    alignItems: 'center',
  },
  messageUserContainer: { flexDirection: 'row-reverse', alignSelf: 'flex-end' },
  messageHedvigContainer: {
    flexDirection: 'row',
    alignSelf: 'flex-start',
    width: '88%',
  },
  flatListContentContainer: {
    paddingBottom: 20,
  },
});

class DefaultHedvigMessage extends React.Component {
  render() {
    const { message, index } = this.props;

    if (message.body.text === '') {
      return null;
    } else {
      return (
        <View style={styles.messageHedvigContainer}>
          <RichMessage withMargin index={index} message={message} />
        </View>
      );
    }
  }
}

class DefaultUserMessage extends React.Component {
  render() {
    const { message, index } = this.props;

    const withMargin =
      !message.header.statusMessage ||
      (message.header.statusMessage && index !== 1);

    return (
      <View style={styles.userMessageOuterContainer}>
        <View style={styles.userMessageInnerContainer}>
          <RichMessage
            fromUser
            index={index}
            message={message}
            withMargin={withMargin}
          />
        </View>
        {message.header.statusMessage &&
          index === 1 && (
            <Text style={styles.statusMessage}>
              {message.header.statusMessage}
            </Text>
          )}
      </View>
    );
  }
}

const UserMessageMapping = {};

const HedvigMessageMapping = {
  hero: () => null,
  bankid_collect: () => null,
  audio: () => null,
  polling: () => null,
};

const renderMessage = (message, idx) => {
  let fromMe = message.header.fromId !== 1;
  const lastIndex = idx === 0;

  let MessageRenderComponent;
  if (!fromMe) {
    MessageRenderComponent = DefaultHedvigMessage;
    if (HedvigMessageMapping.hasOwnProperty(message.body.type)) {
      MessageRenderComponent = HedvigMessageMapping[message.body.type];
    }
  } else {
    MessageRenderComponent = DefaultUserMessage;
    if (UserMessageMapping.hasOwnProperty(message.body.type)) {
      MessageRenderComponent = UserMessageMapping[message.body.type];
    }
  }

  let avatar =
    lastIndex && message.header.avatarName ? (
      <StyledAvatarContainer>
        <Avatar messageIndex={idx} />
      </StyledAvatarContainer>
    ) : null;
  return (
    <View
      style={{
        paddingLeft: 16,
        paddingRight: 16,
      }}
      key={message.globalId || idx}
    >
      {avatar}
      <View
        style={
          fromMe ? styles.messageUserContainer : styles.messageHedvigContainer
        }
      >
        <MessageRenderComponent message={message} index={idx} />
      </View>
      {lastIndex ? <LoadingIndicator messageIndex={idx} /> : null}
    </View>
  );
};

const showOffer = async (componentId) => {
  if (Platform.OS === 'android') {
    NativeModules.ActivityStarter.navigateToOfferFromChat();
    return;
  }

  NativeModules.NativeRouting.showOffer();
};

class MessageList extends React.Component {
  _renderItem = ({ item, index }) => renderMessage(item, index);
  _keyExtractor = (item) => '' + item.globalId;

  render() {
    let message = this.props.messages[0];
    let bodyType = message.body.type;

    let isTextInput = bodyType == 'text' || bodyType == 'number';

    return (
      <InputHeightContainer>
        {({ inputHeight }) => (
          <FlatList
            inverted
            contentContainerStyle={styles.flatListContentContainer}
            style={styles.scrollContent}
            showsVerticalScrollIndicator={false}
            data={this.props.messages}
            renderItem={this._renderItem}
            keyExtractor={this._keyExtractor}
            keyboardDismissMode="interactive"
            ListHeaderComponent={
              Platform.OS === 'ios' ? (
                <>
                  <OptionInputComponent
                    showOffer={() => showOffer(this.props.componentId)}
                    messages={this.props.messages}
                  />
                  <KeyboardSpacer
                    restSpacing={isIphoneX() ? 35 : 0}
                    topSpacing={isTextInput ? inputHeight : 0}
                  />
                </>
              ) : (
                <>
                  <OptionInputComponent
                    showOffer={() => showOffer(this.props.scomponentId)}
                    messages={this.props.messages}
                  />
                  <View style={{ height: inputHeight }} />
                </>
              )
            }
          />
        )}
      </InputHeightContainer>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    messages: state.chat.messages,
  };
};

export default connect(
  mapStateToProps,
  undefined,
)(MessageList);
