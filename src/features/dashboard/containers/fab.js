import { Platform } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Image, StyleSheet } from 'react-native';
import { FloatingAction } from '@hedviginsurance/react-native-floating-action';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { Parallel, Spring } from 'animated-react-native-components';

import { AnimatedView } from 'src/components/AnimatedPrimitives';
import { NavigationEvents } from '../../../navigation/events';

import { chatActions } from '../../../../hedvig-redux';
import FabAction from '../components/FabAction';
import { getFabActions } from '../state/selectors';
import { colors } from '@hedviginsurance/brand';

const styles = StyleSheet.create({
  animatedView: {
    position: 'absolute',
    bottom: 0,
  },
});

class FloatingActionButton extends React.Component {
  static propTypes = {
    fabActions: PropTypes.arrayOf(PropTypes.object).isRequired,
    goToChat: PropTypes.func.isRequired,
    getMessages: PropTypes.func.isRequired,
  };
  static defaultProps = { fabActions: [] };

  constructor(props) {
    super(props);
    this.state = {
      show: true,
      modalStackIndex: 0,
    };
  }

  componentDidMount() {
    if (this.props.fabActions.length === 0) {
      this.props.getMessages();
    }
  }

  handlePressItem = (url) => {
    const maybeBackendAction = this.props.fabActions.filter(
      (a) => a.triggerUrl === url,
    );
    if (maybeBackendAction.length !== 1) {
      throw new Error(
        `Mismatch in remote and local fabActions: ${JSON.stringify(
          this.props.fabActions,
        )}, url: ${url}`,
      );
    }
    const backendAction = maybeBackendAction[0];
    if (backendAction.enabled === false) {
      return;
    }
    this.props.goToChat(url);
  };

  onNavigationCommand = async (name) => {
    if (name === 'showModal') {
      this.setState({
        show: false,
        modalStackIndex: this.state.modalStackIndex + 1,
      });
    }
    if (name === 'dismissModal') {
      if (this.state.modalStackIndex === 1) {
        setTimeout(
          () => this.setState({ show: true, modalStackIndex: 0 }),
          250,
        );
      } else {
        this.setState({ modalStackIndex: this.state.modalStackIndex - 1 });
      }
    }
  };

  onGlobalEvent = ({ name }) => {
    this.onNavigationCommand(name);
  };

  render() {
    const { fabActions } = this.props;

    return (
      <React.Fragment>
        {Platform.OS === 'ios' && (
          <NavigationEvents
            onNavigationCommand={this.onNavigationCommand}
            onGlobalEvent={this.onGlobalEvent}
          />
        )}
        <Parallel>
          <Spring
            initialValue={0}
            toValue={this.state.show ? 1 : 0}
            config={{ bounciness: 5 }}
          >
            {(animatedValue) => (
              <AnimatedView
                style={[
                  styles.animatedView,
                  {
                    transform: [
                      {
                        translateY: animatedValue.interpolate({
                          inputRange: [0, 1],
                          outputRange: [100, 0],
                        }),
                      },
                    ],
                  },
                ]}
              >
                <FloatingAction
                  color="#651eff"
                  distanceToEdge={isIphoneX() ? 55 : 20}
                  position={Platform.OS === 'ios' ? 'center' : 'right'}
                  customButtonStyles={{
                    shadowColor: colors.PURPLE,
                    shadowOffset: {
                      width: 0,
                      height: 0,
                    },
                  }}
                  floatingIcon={
                    <Image
                      source={require('../../../../assets/buttons/fab/fab-icon.png')}
                    />
                  }
                  overlayColor="rgba(0,0,0,0.15)"
                  actions={fabActions.map((a) => ({
                    name: a.triggerUrl,
                    margin: 0,
                    render: (props) => (
                      <FabAction {...props} text={a.text} enabled={a.enabled} />
                    ),
                  }))}
                  actionsPaddingTopBottom={0}
                  onPressItem={this.handlePressItem}
                />
              </AnimatedView>
            )}
          </Spring>
        </Parallel>
      </React.Fragment>
    );
  }
}

export default connect(
  (state) => ({ fabActions: getFabActions(state) }),
  (dispatch) => ({
    goToChat: (url) =>
      dispatch(
        chatActions.apiAndNavigateToChat({
          method: 'POST',
          url,
          SUCCESS: 'INITIATED_CHAT_MAIN',
        }),
      ),
    getMessages: () => dispatch(chatActions.getMessages()),
  }),
)(FloatingActionButton);
