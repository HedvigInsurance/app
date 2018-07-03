import React from 'react';
import { View, AsyncStorage, StyleSheet } from 'react-native';
import { connect } from 'react-redux';
import { NavigationActions } from 'react-navigation';
import {
  createReduxBoundAddListener,
  initializeListeners,
} from 'react-navigation-redux-helpers';

import { insuranceActions } from '../../../hedvig-redux';
import BaseNavigator from './base-navigator/BaseNavigator';
import { SEEN_MARKETING_CAROUSEL_KEY, IS_VIEWING_OFFER } from '../../constants';
import { REDIRECTED_INITIAL_ROUTE } from '../../actions/router';

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

class BaseRouter extends React.Component {
  constructor(props) {
    super(props);
    // Hooking up react-navigation + redux
    initializeListeners('root', this.props.nav);
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
    initializeListeners('root', this.props.nav);

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
    const navigation = {
      dispatch: this.props.dispatch,
      state: this.props.nav,
      addListener: createReduxBoundAddListener('root'),
    };

    return (
      <View style={styles.container}>
        <BaseNavigator navigation={navigation} />
      </View>
    );
  }
}

const mapStateToProps = ({ insurance, router, nav }, ownProps) => {
  return {
    ...ownProps,
    insurance,
    nav,
    hasRedirected: router.hasRedirected,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    getInsurance: () => dispatch(insuranceActions.getInsurance()),
    dispatch,
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
