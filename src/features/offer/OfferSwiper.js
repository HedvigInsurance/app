import React from 'react';
import {
  View,
  Image,
  Text,
  StatusBar,
  StyleSheet,
  AsyncStorage,
  Dimensions,
  TouchableOpacity,
  BackHandler,
} from 'react-native';
import Swiper from 'react-native-swiper';
import { connect } from 'react-redux';

import { insuranceActions, eventActions } from '../../../hedvig-redux';
import {
  TRACK_OFFER_OPENED,
  TRACK_OFFER_CLOSED,
  TRACK_OFFER_STEP_VIEWED,
  TRACK_OFFER_STEP_COMPLETED,
} from '../../features/analytics/actions';

import { IS_VIEWING_OFFER } from '../../constants';
import { Loader } from '../../components/Loader';
import { PerilsDialog } from './PerilsDialog';
import { OFFER_SET_ACTIVE_SCREEN } from './actions';

import OfferScreen1 from './OfferScreen1';
import OfferScreen2 from './OfferScreen2';
import OfferScreen3 from './OfferScreen3';
import OfferScreen4 from './OfferScreen4';
import OfferScreen5 from './OfferScreen5';
import OfferScreen6 from './OfferScreen6';
import OfferScreen7 from './OfferScreen7';
import OfferScreen8 from './OfferScreen8';

import {
  verticalSizeClass,
  V_SPACIOUS,
  V_REGULAR,
  V_COMPACT,
} from '../../services/DimensionSizes';

const { width: viewportWidth, height: viewportHeight } = Dimensions.get(
  'window',
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  closeOffer: {
    position: 'absolute',
    top: 20,
    left: 17,
    zIndex: 2,
  },
  swiperDot: {
    backgroundColor: '#E9ECEF',
    width: 7.5,
    height: 7.5,
    borderRadius: 7.5,
    marginLeft: 5.5,
    marginRight: 5.5,
  },
  swiperDotIsActive: {
    backgroundColor: '#1BE9B6',
    width: 7.5,
    height: 7.5,
    borderRadius: 7.5,
    marginLeft: 5.5,
    marginRight: 5.5,
  },
  swiperPagination: {
    bottom: {
      [V_SPACIOUS]: 18,
      [V_REGULAR]: 15,
      [V_COMPACT]: 15,
    }[verticalSizeClass],
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    paddingLeft: 30,
    paddingRight: 30,
    backgroundColor: '#31e8b7',
    borderRadius: 30,
    height: 47,
    zIndex: 200,
    elevation: 1,
    marginBottom: {
      [V_SPACIOUS]: 29,
      [V_REGULAR]: 26,
      [V_COMPACT]: 26,
    }[verticalSizeClass],
  },
  buttonIsFirst: {
    backgroundColor: '#fff',
  },
  label: {
    position: 'relative',
    top: -1,
    color: 'white',
    fontSize: 20,
    textAlign: 'center',
    fontFamily: 'circular',
    textAlignVertical: 'center',
  },
  labelIsFirst: {
    color: '#141132',
  },
  icon: {
    marginLeft: 10,
  },
});

const hitSlop = {
  top: 20,
  right: 20,
  bottom: 20,
  left: 20,
};

class OfferSwiper extends React.Component {
  componentDidMount() {
    this.props.getInsurance();
    // Routing to Offer view from BaseRouter when
    // the app has been force closed and lost its state
    AsyncStorage.setItem(IS_VIEWING_OFFER, 'true');

    const { orderId } = this.props.analytics;
    this.props.trackOfferOpen(this.props.insurance.newTotalPrice, orderId);
    this.props.trackOfferStepViewed(this.props.activeOfferScreenIndex, orderId);

    BackHandler.addEventListener('hardwareBackPress', this.onBackPress);
  }

  componentWillUnmount() {
    BackHandler.removeEventListener('hardwareBackPress', this.onBackPress);
  }

  onBackPress = () => {
    const activeIndex = this.props.activeOfferScreenIndex;
    if (activeIndex === 0) {
      this.props.closeOffer(this.props.analytics.orderId);
      return true;
    }
    if (this.swiper) {
      this.swiper.scrollBy(-1);
    }
    return true;
  };

  componentDidUpdate(prevProps) {
    const { orderId } = this.props.analytics;
    if (
      this.props.activeOfferScreenIndex !== prevProps.activeOfferScreenIndex
    ) {
      this.props.trackOfferStepCompleted(
        prevProps.activeOfferScreenIndex,
        orderId,
      );
      this.props.trackOfferStepViewed(
        this.props.activeOfferScreenIndex,
        orderId,
      );
    }
  }

  hasLoaded(insurance) {
    // WARNING: Change this to loading state based on the request or something
    // more robust
    return insurance.newTotalPrice;
  }

  render() {
    const { insurance, analytics } = this.props;
    if (!this.hasLoaded(insurance)) {
      return <Loader />;
    }

    const screens = [
      OfferScreen1,
      OfferScreen2,
      OfferScreen3,
      OfferScreen4,
      OfferScreen5,
      OfferScreen6,
    ];
    if (insurance.insuredAtOtherCompany) {
      screens.push(OfferScreen7);
    }
    screens.push(OfferScreen8);
    const { activeOfferScreenIndex } = this.props;
    const isLast = activeOfferScreenIndex === screens.length - 1;
    const isFirst = activeOfferScreenIndex === 0;

    return (
      <View style={styles.container}>
        <StatusBar hidden />
        <View style={{ flex: 1 }}>
          {activeOfferScreenIndex === 0 ? (
            <View style={styles.closeOffer}>
              <TouchableOpacity
                onPress={() => this.props.closeOffer(analytics.orderId)}
                hitSlop={hitSlop}
              >
                <Image
                  source={require('../../../assets/icons/close/close_white.png')}
                  style={{ width: 26, height: 26 }}
                />
              </TouchableOpacity>
            </View>
          ) : null}
          <Swiper
            ref={(ref) => (this.swiper = ref)}
            style={styles.swiper}
            loop={false}
            showsButtons={!isLast}
            showsPagination={!isLast && !isFirst}
            buttonWrapperStyle={{
              backgroundColor: 'transparent',
              position: 'absolute',
              bottom: 0,
              justifyContent: 'center',
              alignItems: 'flex-end',
            }}
            dot={
              <View key={'dot'}>
                <View style={styles.swiperDot} />
              </View>
            }
            activeDot={
              <View key={'activeDot'}>
                <View style={styles.swiperDotIsActive} />
              </View>
            }
            paginationStyle={styles.swiperPagination}
            onIndexChanged={(index) => this.props.setActiveOfferScreen(index)}
            nextButton={
              <View key={'nextButton'}>
                <View style={[styles.button, isFirst && styles.buttonIsFirst]}>
                  <Text style={[styles.label, isFirst && styles.labelIsFirst]}>
                    {isFirst ? 'Berätta mer' : 'Gå vidare'}
                  </Text>
                  {!isFirst && (
                    <Image
                      style={styles.icon}
                      source={require('../../../assets/icons/offer/offer-progress-arrow.png')}
                    />
                  )}
                </View>
              </View>
            }
            prevButton={<View key={'prevButton'} />}
            width={viewportWidth}
            height={viewportHeight}
            containerStyle={{}}
          >
            {screens.map((Screen, index) => {
              const isActive = activeOfferScreenIndex === index;
              return (
                <Screen isActive={isActive} insurance={insurance} key={index} />
              );
            })}
          </Swiper>
        </View>
        <PerilsDialog />
      </View>
    );
  }
}

const mapStateToProps = (state) => {
  const { insurance, analytics, offer } = state;
  const { activeOfferScreenIndex } = offer;
  return {
    insurance,
    analytics,
    activeOfferScreenIndex,
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    getInsurance: () => dispatch(insuranceActions.getInsurance()),
    setActiveOfferScreen: (index) => {
      return dispatch({ type: OFFER_SET_ACTIVE_SCREEN, payload: { index } });
    },
    closeOffer: (orderId) => {
      dispatch({
        type: TRACK_OFFER_CLOSED,
        payload: {
          order_id: orderId,
        },
      });
      dispatch(
        eventActions.event(
          {
            type: 'MODAL_CLOSED',
            value: 'quote',
          },
          {
            getMessagesAfter: true,
            showLoadingIndicator: true,
          },
        ),
      );
      ownProps.navigation.goBack();
    },
    navigate: (params) => ownProps.navigation.navigate(params),
    trackOfferOpen: (pricePerMonth, orderId) =>
      dispatch({
        type: TRACK_OFFER_OPENED,
        payload: {
          revenue: pricePerMonth,
          currency: 'SEK',
          order_id: orderId,
        },
      }),
    trackOfferStepViewed: (step, orderId) =>
      dispatch({
        type: TRACK_OFFER_STEP_VIEWED,
        payload: { step, order_id: orderId },
      }),
    trackOfferStepCompleted: (step, orderId) =>
      dispatch({
        type: TRACK_OFFER_STEP_COMPLETED,
        payload: { step, order_id: orderId },
      }),
  };
};

const OfferSwiperContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(OfferSwiper);

export { OfferSwiperContainer as OfferSwiper };
