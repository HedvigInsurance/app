import React from 'react';
import { View, StatusBar, AsyncStorage, StyleSheet } from 'react-native';
import { connect } from 'react-redux';
import { NavigationActions, addNavigationHelpers } from 'react-navigation';
import { createReduxBoundAddListener } from 'react-navigation-redux-helpers';

import { insuranceActions } from '../../../hedvig-redux';
import BaseNavigator from './base-navigator/BaseNavigator';
import { SEEN_MARKETING_CAROUSEL_KEY, IS_VIEWING_OFFER } from '../../constants';
import { REDIRECTED_INITIAL_ROUTE } from '../../actions/router';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

const ReduxBaseNavigator = ({ dispatch, nav, addListener }) => {
  return (
    <BaseNavigator
      navigation={addNavigationHelpers({
        dispatch: dispatch,
        state: nav,
        addListener,
      })}
    />
  );
};

const ConnectedReduxBaseNavigator = connect(({ nav }, ownProps) => ({
  ...ownProps,
  nav,
}))(ReduxBaseNavigator);

class BaseRouter extends React.Component {
  constructor(props) {
    super(props);
    // Hooking up react-navigation + redux
    this.addListener = createReduxBoundAddListener('root');
    this._doRedirection = this._doRedirection.bind(this);
  }

  async _doRedirection() {
    if (
      this.props.hasRedirected ||
      !this.props.insurance ||
      !this.props.insurance.status
    ) {
      return;
    }

    if (['ACTIVE', 'INACTIVE'].includes(this.props.insurance.status)) {
      this.props.redirectToRoute({ routeName: 'Account' });
    } else {
      let isViewingOffer = await AsyncStorage.getItem(IS_VIEWING_OFFER);

      let action;
      if (isViewingOffer) {
        action = NavigationActions.navigate({
          routeName: 'Offer',
        });
      }

      this.props.redirectToRoute({
        routeName: 'Conversation',
        action,
      });
    }
  }

  async componentDidMount() {
    this.props.getInsurance();

    if (this.props.hasRedirected) return;

    let alreadySeenMarketingCarousel = await AsyncStorage.getItem(
      SEEN_MARKETING_CAROUSEL_KEY,
    );

    if (!alreadySeenMarketingCarousel) {
      this.props.redirectToRoute({ routeName: 'Marketing' });
    } else {
      this._doRedirection();
    }
  }

  componentDidUpdate() {
    this._doRedirection();
  }

  render() {
    return (
      <View style={styles.container}>
        <StatusBar backgroundColor="white" />
        <ConnectedReduxBaseNavigator addListener={this.addListener} />
      </View>
    );
  }
}

const mapStateToProps = ({ insurance, router }, ownProps) => {
  return {
    ...ownProps,
    insurance,
    hasRedirected: router.hasRedirected,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    getInsurance: () => dispatch(insuranceActions.getInsurance()),
    redirectToRoute: (options) => {
      dispatch({ type: REDIRECTED_INITIAL_ROUTE });
      return dispatch(
        NavigationActions.reset({
          index: 0,
          key: null,
          actions: [NavigationActions.navigate(options)],
        }),
      );
    },
  };
};

export const Router = connect(
  mapStateToProps,
  mapDispatchToProps,
)(BaseRouter);
