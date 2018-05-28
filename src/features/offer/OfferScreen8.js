import { connect } from 'react-redux';

import React from 'react';
import {
  View,
  Image,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
  SafeAreaView,
  Dimensions,
  ImageBackground,
  ScrollView,
} from 'react-native';

import {
  horizontalSizeClass,
  verticalSizeClass,
  H_SPACIOUS,
  H_REGULAR,
  H_COMPACT,
  V_SPACIOUS,
  V_REGULAR,
  V_COMPACT,
} from '../../services/DimensionSizes';
import { OFFER_CHECKOUT } from './actions';
import { Dialog } from '../bankid/Dialog';

const { width: viewportWidth, height: viewportHeight } = Dimensions.get(
  'window',
);

const styles = StyleSheet.create({
  container: { flex: 1 },
  scroll: {
    flex: 1,
  },
  scrollContent: {
    flex: 1,
    paddingBottom: 80,
  },
  content: {
    flex: 1,
    paddingHorizontal: 20,
  },
  view: {
    flex: 1,
    alignItems: 'center',
  },
  backButton: {
    position: 'absolute',
    left: 20,
    top: 26,
    zIndex: 200,
  },
  priceContainer: {
    marginTop: {
      [V_SPACIOUS]: 50,
      [V_REGULAR]: 40,
      [V_COMPACT]: 30,
    }[verticalSizeClass],
  },
  priceBackground: {
    width: 127,
    height: 127,
    justifyContent: 'center',
  },
  priceAmount: {
    textAlign: 'center',
    fontFamily: 'circular',
    fontSize: 43,
    lineHeight: 44,
    color: '#141132',
    position: 'relative',
    top: 5,
  },
  priceLabel: {
    textAlign: 'center',
    fontFamily: 'circular',
    fontSize: 17,
    lineHeight: 20,
    color: '#141132',
  },
  contractExplainer: {
    fontFamily: 'circular',
    fontSize: 17,
    color: 'white',
    marginTop: 12,
  },
  category: {
    flexDirection: 'row',
    marginBottom: 15,
    alignItems: 'center',
  },
  categoryContent: {
    flex: 1,
    flexDirection: 'column',
  },
  categoryImage: {
    position: 'relative',
    top: 1,
    marginRight: 17,
  },
  categoryHeader: {
    fontFamily: 'circular-bold',
    fontSize: 17,
    lineHeight: 25,
    color: '#414150',
    marginBottom: 2,
  },
  button: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    height: 60,
    paddingLeft: 30,
    paddingRight: 30,
    backgroundColor: '#fff',
    borderRadius: 100,
    marginBottom: 36,
  },
  buttonLabel: {
    position: 'relative',
    top: -1,
    color: '#141132',
    fontSize: 19,
    textAlign: 'center',
    fontFamily: 'circular',
    textAlignVertical: 'center',
  },
  buttonIcon: {
    marginLeft: 15,
  },
  checkoutContainer: {
    position: 'absolute',
    bottom: 0,
    alignSelf: 'center',
  },
  checkWrapper: {
    flex: 1,
    marginTop: {
      [V_SPACIOUS]: 70,
      [V_REGULAR]: 50,
      [V_COMPACT]: 30,
    }[verticalSizeClass],
    width: {
      [H_SPACIOUS]: 330,
      [H_REGULAR]: 320,
      [H_COMPACT]: '85%',
    }[horizontalSizeClass],
  },
  check: {
    flexDirection: 'row',
    position: 'relative',
    marginBottom: {
      [H_SPACIOUS]: 40,
      [H_REGULAR]: 40,
      [H_COMPACT]: 35,
    }[horizontalSizeClass],
    alignItems: 'center',
  },
  checkIcon: {
    width: 30,
    height: 30,
    marginRight: 17,
  },
  checkLabel: {
    flex: 1,
    fontFamily: 'circular-bold',
    fontSize: 21,
    lineHeight: 24,
    color: 'white',
  },
  screenBackground: {
    height: viewportHeight,
    width: viewportWidth,
  },
});

const hitSlop = {
  top: 20,
  right: 20,
  bottom: 20,
  left: 20,
};

class OfferScreen extends React.Component {
  state = {
    springTranslate: new Animated.Value(110),
  };

  componentDidUpdate() {
    const { isActive } = this.props;
    const toValue = isActive ? 0 : 110;
    const delay = isActive ? 400 : 0;

    Animated.spring(this.state.springTranslate, {
      toValue,
      delay,
      bounciness: 1,
      speed: 1,
      useNativeDriver: true,
    }).start();
  }

  render() {
    const { insuredAtOtherCompany } = this.props.insurance;
    const isDisabled = this.props.isCurrentlySigning;

    return (
      <View style={styles.container}>
        <ImageBackground
          style={styles.screenBackground}
          resizeMode={'cover'}
          source={require('../../../assets/offer/unbroken-bg.png')}
        >
          <ScrollView style={styles.scroll}>
            <View style={styles.scrollContent}>
              <SafeAreaView style={styles.view}>
                <View style={styles.priceContainer}>
                  <ImageBackground
                    style={styles.priceBackground}
                    resizeMode={'cover'}
                    source={require('../../../assets/offer/offer-price-bg.png')}
                  >
                    <View>
                      <Text style={styles.priceAmount}>
                        {this.props.insurance.newTotalPrice}
                      </Text>
                      <Text style={styles.priceLabel}>kr/mån</Text>
                    </View>
                  </ImageBackground>
                </View>

                <View style={styles.checkWrapper}>
                  <View style={styles.check}>
                    <Image
                      style={styles.checkIcon}
                      source={require('../../../assets/icons/offer/offer-check.png')}
                    />
                    <Text style={styles.checkLabel}>Blixtsnabb hjälp</Text>
                  </View>
                  <View style={styles.check}>
                    <Image
                      style={styles.checkIcon}
                      source={require('../../../assets/icons/offer/offer-check.png')}
                    />
                    <Text style={styles.checkLabel}>
                      Blixtsnabb utbetalning
                    </Text>
                  </View>
                  <View style={styles.check}>
                    <Image
                      style={styles.checkIcon}
                      source={require('../../../assets/icons/offer/offer-check.png')}
                    />
                    <Text style={styles.checkLabel}>Ingen bindningstid</Text>
                  </View>
                  <View style={styles.check}>
                    <Image
                      style={styles.checkIcon}
                      source={require('../../../assets/icons/offer/offer-check.png')}
                    />
                    <Text style={styles.checkLabel}>
                      Överskottet till välgörenhet
                    </Text>
                  </View>
                </View>
              </SafeAreaView>
            </View>
          </ScrollView>

          <Animated.View
            style={[
              styles.checkoutContainer,
              { transform: [{ translateY: this.state.springTranslate }] },
            ]}
          >
            <TouchableOpacity
              onPress={() => {
                !isDisabled && this.props.checkout();
              }}
              hitSlop={hitSlop}
              style={styles.button}
            >
              <Text style={styles.buttonLabel}>
                {insuredAtOtherCompany ? 'Byt till Hedvig' : 'Skaffa Hedvig'}
              </Text>
              <Image
                style={styles.buttonIcon}
                width={27}
                height={26}
                source={require('../../../assets/icons/bankid/bankid-logo-dark.png')}
              />
            </TouchableOpacity>
          </Animated.View>
        </ImageBackground>
        <Dialog />
      </View>
    );
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    checkout: () => {
      dispatch({
        type: OFFER_CHECKOUT,
      });
    },
  };
};

const OfferContainer = connect(
  null,
  mapDispatchToProps,
)(OfferScreen);

export default OfferContainer;
