import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { WebView, BackHandler, View, Text, Linking } from 'react-native';
import { NavigationActions } from 'react-navigation';

import { NavBar } from '../../components/NavBar';
import { NavigateBackButton } from '../../components/Button';
class Payment extends React.Component {
  static propTypes = {
    url: PropTypes.string,
    requestPaymentRegistration: PropTypes.func.isRequired,
    goBack: PropTypes.func.isRequired,
    onPaymentSuccess: PropTypes.func.isRequired,
    onPaymentFailure: PropTypes.func.isRequired,
  };

  static defaultProps = {
    url: undefined,
  };

  componentDidMount() {
    BackHandler.addEventListener('hardwareBackPress', this.onBackPress);
    this.props.requestPaymentRegistration(
      this.props.navigation.state.params.id,
    );
    Linking.addEventListener('url', this.handleAllDeepLinks);
  }

  componentWillUnmount() {
    BackHandler.removeEventListener('hardwareBackPress', this.onBackPress);
    Linking.removeEventListener('url', this.handleAllDeepLinks);
  }

  onBackPress = () => {
    this.props.goBack();
    return true;
  };

  goBack = () => {
    this.props.goBack();
  };

  handleAllDeepLinks = (event) => {
    if (event.url.match('trustly/payment-success')) {
      this.props.onPaymentSuccess();
    } else if (event.url.match('trustly/payment-failure')) {
      this.props.onPaymentFailure();
    }
  };

  render() {
    return (
      <View style={{ flex: 1 }}>
        <NavBar
          title="Betalning"
          headerLeft={<NavigateBackButton onPress={this.goBack} />}
        />
        {this.props.url ? (
          <WebView source={{ uri: this.props.url }} />
        ) : (
          <View>
            <Text>Loading...</Text>
          </View>
        )}
      </View>
    );
  }
}

export default connect(
  (state) => ({
    url: state.payment.url,
  }),
  (dispatch) => ({
    requestPaymentRegistration: (id) =>
      dispatch({
        type: 'PAYMENT/REQUEST_PAYMENT_REGISTRATION',
        payload: { id },
      }),
    onPaymentSuccess: () => {
      dispatch({
        type: 'PAYMENT/ON_PAYMENT_SUCCESS',
        payload: { onFinish: () => dispatch(NavigationActions.back()) },
      });
    },
    onPaymentFailure: () =>
      dispatch({
        type: 'PAYMENT/ON_PAYMENT_FAILURE',
        payload: { onFinish: () => dispatch(NavigationActions.back()) },
      }),
    goBack: () =>
      dispatch({
        type: 'PAYMENT/CANCEL_PAYMENT',
        payload: { onFinish: () => dispatch(NavigationActions.back()) },
      }),
  }),
)(Payment);
