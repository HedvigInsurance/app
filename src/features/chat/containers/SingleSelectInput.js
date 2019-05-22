import React from 'react';
import { Platform, NativeModules } from 'react-native';
import PropTypes from 'prop-types';
import { Linking } from 'react-native';
import { connect } from 'react-redux';

import { chatActions } from '../../../../hedvig-redux';
import { AnimatedSingleSelectOptionButton } from '../components/Button';
import {
  StyledRightAlignedOptions,
  StyledMarginContainer,
} from '../styles/chat';

const goToDashboard = () => {
  if (Platform.OS === 'ios') {
    NativeModules.NativeRouting.presentLoggedIn();
  } else {
    NativeModules.ActivityStarter.navigateToLoggedInFromChat();
  }
};

class SingleSelectInput extends React.Component {
  static propTypes = {
    selectChoice: PropTypes.func.isRequired,
    done: PropTypes.func.isRequired,
  };

  render() {
    const { message, selectChoice, done, showOffer } = this.props;
    let anySelected = message.body.choices.some((choice) => choice.selected);
    let opts = message.body.choices.map((choice) => {
      return (
        <StyledRightAlignedOptions key={choice.text}>
          <AnimatedSingleSelectOptionButton
            hidden={anySelected && !choice.selected}
            title={choice.text}
            selected={choice.selected}
            onPress={() => {
              if (choice.type === 'selection') {
                selectChoice(message, choice);
                done(message);
              } else if (choice.type === 'link' && choice.view !== null) {
                selectChoice(message, choice);
                done(message);
                if (choice.view === 'Dashboard') {
                  goToDashboard();
                } else if (choice.view === 'Offer') {
                  showOffer();
                }
              } else if (choice.type === 'link' && choice.appUrl !== null) {
                selectChoice(message, choice);
                done(message);
                Linking.openURL(choice.appUrl);
              } else if (choice.type === 'link' && choice.webUrl !== null) {
                selectChoice(message, choice);
                done(message);
                Linking.openURL(choice.webUrl);
              } else if (choice.type === 'trustly') {
                goToDashboard();
                selectChoice(message, choice);
                done(message);
              }
            }}
          />
        </StyledRightAlignedOptions>
      );
    });
    return <StyledMarginContainer>{opts}</StyledMarginContainer>;
  }
}

const mapStateToProps = (state) => {
  return {
    message: state.chat.messages[0],
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    selectChoice: (message, choice) =>
      dispatch(chatActions.selectChoice(message, choice)),
    done: (message) => dispatch(chatActions.sendChatResponse(message)),
  };
};

const SingleSelectInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(SingleSelectInput);

export default SingleSelectInputContainer;
