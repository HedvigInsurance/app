import React from 'react';
import PropTypes from 'prop-types';
import { Linking } from 'react-native';
import { connect } from 'react-redux';
import { Navigation } from 'react-native-navigation';

import { chatActions } from '../../../../hedvig-redux';
import { AnimatedSingleSelectOptionButton } from '../components/Button';
import {
  StyledRightAlignedOptions,
  StyledMarginContainer,
} from '../styles/chat';

import { PAYMENT_SCREEN } from '../../../navigation/screens/payment';
import { setLayout, getMainLayout } from '../../../navigation/layout';
import { NEW_OFFER_SCREEN } from 'src/navigation/screens/new-offer';
import { NavigationContext } from 'src/navigation/context';

const showOffer = (componentId) => {
  Navigation.push(componentId, NEW_OFFER_SCREEN);
};

const showTrustly = (id) =>
  Navigation.showModal({
    stack: {
      children: [
        {
          component: {
            ...PAYMENT_SCREEN.component,
            passProps: {
              id,
            },
          },
        },
      ],
    },
  });

const goToDashboard = () => {
  setLayout(getMainLayout());
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
                showTrustly(choice.id);
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
