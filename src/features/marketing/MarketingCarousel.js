import React from 'react';
import PropTypes from 'prop-types';
import {
  View,
  Image,
  Text,
  Dimensions,
  TouchableOpacity,
  StatusBar,
  StyleSheet,
  ImageBackground,
  Linking,
} from 'react-native';
import Swiper from 'react-native-swiper';
import { connect } from 'react-redux';

import {
  verticalSizeClass,
  horizontalSizeClass,
  V_SPACIOUS,
  V_REGULAR,
  V_COMPACT,
  H_SPACIOUS,
  H_REGULAR,
  H_COMPACT,
} from '../../services/DimensionSizes';

import {
  MARKETING_SET_ACTIVE_SCREEN,
  MARKETING_CHAT_START,
  MARKETING_CHAT_LOGIN,
} from './actions';

const colors = {
  PRIMARY_GREEN: '#1BE9B6',
  PRIMARY_BLUE: '#0F007A',
  PRIMARY_PURPLE: '#651EFF',
  TEXT: '#686E7E',
};

const { width: viewportWidth, height: viewportHeight } = Dimensions.get(
  'window',
);

const slideStyles = StyleSheet.create({
  container: {
    overflow: 'hidden',
    flex: 1,
  },
  figure: {
    flex: 1,
    width: viewportWidth,
    alignItems: 'center',
  },
  caption: {
    fontFamily: 'circular-bold',
    fontSize: {
      [H_SPACIOUS]: 24,
      [H_REGULAR]: 22,
      [H_COMPACT]: 18,
    }[horizontalSizeClass],
    lineHeight: {
      [H_SPACIOUS]: 33,
      [H_REGULAR]: 26,
      [H_COMPACT]: 22,
    }[horizontalSizeClass],
    textAlign: 'center',
    color: '#414150',
    width: viewportWidth,
    paddingTop: {
      [V_SPACIOUS]: 35,
      [V_REGULAR]: 30,
      [V_COMPACT]: 20,
    }[verticalSizeClass],
    paddingBottom: {
      [V_SPACIOUS]: 180,
      [V_REGULAR]: 165,
      [V_COMPACT]: 160,
    }[verticalSizeClass],
    paddingLeft: 10,
    paddingRight: 10,
    backgroundColor: 'white',
    shadowColor: '#000',
    shadowOpacity: 0.25,
    shadowRadius: 20,
  },
});

const Slide = ({ title, children }) => (
  <View style={slideStyles.container}>
    <View style={slideStyles.figure}>{children}</View>
    <Text numberOfLines={2} style={slideStyles.caption}>
      {title}
    </Text>
  </View>
);

Slide.propTypes = {
  children: PropTypes.node.isRequired,
  title: PropTypes.string.isRequired,
};

const slideImageWidth = viewportWidth - 60;

const slideImageStyles = StyleSheet.create({
  container: {
    flex: 1,
    width: slideImageWidth,
    position: 'relative',
    top: {
      [V_SPACIOUS]: 90,
      [V_REGULAR]: 30,
      [V_COMPACT]: 18,
    }[verticalSizeClass],
    overflow: 'hidden',
  },
  image: {
    position: 'absolute',
    top: {
      [V_SPACIOUS]: 0,
      [V_REGULAR]: 0,
      [V_COMPACT]: -30,
    }[verticalSizeClass],
    left: 0,
    right: 0,
    bottom: 0,
    width: slideImageWidth,
    resizeMode: Image.resizeMode.contain,
  },
});

const SlideImage = ({ imageSource }) => {
  return (
    <View style={slideImageStyles.container}>
      <Image
        source={imageSource}
        style={slideImageStyles.image}
        resizeMethod={'scale'}
      />
    </View>
  );
};

SlideImage.propTypes = {
  imageSource: PropTypes.number.isRequired,
};

const marketingCarouselStyles = StyleSheet.create({
  container: {
    flex: 1,
    alignSelf: 'stretch',
    marginTop: 0,
    backgroundColor: 'white',
    justifyContent: 'center',
  },
  background: {
    width: viewportWidth,
    height: viewportHeight,
    backgroundColor: colors.PRIMARY_PURPLE,
    justifyContent: 'center',
    alignItems: 'center',
  },
  introHeader: {
    width: {
      [H_SPACIOUS]: viewportWidth - 60,
      [H_REGULAR]: viewportWidth - 50,
      [H_COMPACT]: viewportWidth - 40,
    }[horizontalSizeClass],
    top: -30,
  },
  swiperDot: {
    backgroundColor: '#dcdbdc',
    width: 7.5,
    height: 7.5,
    borderRadius: 7.5,
    marginLeft: 5.5,
    marginRight: 5.5,
  },
  swiperDotIsFirst: {
    backgroundColor: 'white',
  },
  swiperDotIsActive: {
    backgroundColor: colors.PRIMARY_PURPLE,
    width: 7.5,
    height: 7.5,
    borderRadius: 7.5,
    marginLeft: 5.5,
    marginRight: 5.5,
  },
  swiperPagination: {
    bottom: {
      [V_SPACIOUS]: 25,
      [V_REGULAR]: 18,
      [V_COMPACT]: 18,
    }[verticalSizeClass],
  },
  slideOneContainer: {
    flex: 1,
    alignItems: 'center',
  },
  slideTwoContainer: {
    flex: 1,
    backgroundColor: colors.PRIMARY_BLUE,
  },
  slideThreeContainer: {
    flex: 1,
    backgroundColor: colors.PRIMARY_PURPLE,
  },
  slideFourContainer: {
    flex: 1,
    backgroundColor: '#FF8A80',
  },
  footerContainer: {
    position: 'absolute',
    bottom: 0,
    width: viewportWidth,
    paddingBottom: {
      [V_SPACIOUS]: 30,
      [V_REGULAR]: 18,
      [V_COMPACT]: 18,
    }[verticalSizeClass],
  },
  footerCtaButton: {
    alignItems: 'center',
    justifyContent: 'center',
    alignSelf: 'center',
    maxWidth: viewportWidth * 0.7,
    minWidth: 250,
    height: 60,
    marginBottom: 18,
    padding: 0,
    backgroundColor: colors.PRIMARY_GREEN,
    borderWidth: 0,
    borderRadius: 30,
  },
  footerCtaButtonIsFirst: {
    backgroundColor: 'white',
  },
  footerCtaButtonText: {
    fontSize: 21,
    fontFamily: 'circular',
    textAlignVertical: 'center',
    textAlign: 'center',
    color: 'white',
  },
  footerCtaButtonTextIsFirst: {
    color: '#141033',
  },
  footerLoginContainer: {
    marginTop: 0,
    marginBottom: 20,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'flex-start',
  },
  footerLoginText: {
    fontFamily: 'circular',
    fontSize: 18,
    lineHeight: 20,
    height: 20,
    color: '#9B9BAA',
    marginRight: 8,
  },
  footerLoginTextIsFirst: {
    color: 'white',
  },
  footerPrivacyText: {
    fontFamily: 'circular',
    fontSize: 18,
    lineHeight: 20,
    height: 20,
    color: 'white',
    marginRight: 8,
  },
  footerLoginCta: {
    fontFamily: 'circular',
    fontSize: 18,
    lineHeight: 20,
    height: 20,
    color: colors.PRIMARY_PURPLE,
  },
});

export default class MarketingCarousel extends React.Component {
  render() {
    const { activeMarketingScreenIndex } = this.props;
    const isFirst = activeMarketingScreenIndex === 0;

    return (
      <View style={marketingCarouselStyles.container}>
        <StatusBar hidden />
        <View style={{ flex: 1 }}>
          <Swiper
            style={{}}
            index={0}
            onIndexChanged={(index) =>
              this.props.setActiveMarketingScreen(index)
            }
            dot={
              <View key={'dot'}>
                <View
                  style={[
                    marketingCarouselStyles.swiperDot,
                    isFirst && marketingCarouselStyles.swiperDotIsFirst,
                  ]}
                />
              </View>
            }
            activeDot={
              <View key={'activeDot'}>
                <View style={marketingCarouselStyles.swiperDotIsActive} />
              </View>
            }
            paginationStyle={marketingCarouselStyles.swiperPagination}
            loop={false}
          >
            <View style={marketingCarouselStyles.slideOneContainer}>
              <ImageBackground
                style={marketingCarouselStyles.background}
                resizeMode={'cover'}
                source={require('../../../assets/offer/unbroken-bg.png')}
              >
                <Image
                  resizeMode={'contain'}
                  style={marketingCarouselStyles.introHeader}
                  source={require('../../../assets/onboarding/marketing-intro-header.png')}
                />
              </ImageBackground>
            </View>
            <View style={marketingCarouselStyles.slideTwoContainer}>
              <Slide title={'Hemförsäkring för dig\nsom bor i\u00A0lägenhet'}>
                <SlideImage
                  imageSource={require('../../../assets/onboarding/app-marketing-screen1.png')}
                />
              </Slide>
            </View>
            <View style={marketingCarouselStyles.slideThreeContainer}>
              <Slide
                title={'Anmäl en skada på sekunder,\nfå betalt på\u00A0minuter'}
              >
                <SlideImage
                  imageSource={require('../../../assets/onboarding/app-marketing-screen2.png')}
                />
              </Slide>
            </View>
            <View style={marketingCarouselStyles.slideFourContainer}>
              <Slide title={'Överskottet doneras\ntill välgörenhet'}>
                <View
                  style={{
                    flex: 1,
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  <Image
                    source={require('../../../assets/onboarding/heart.png')}
                    style={{
                      width: 132,
                      height: 129,
                      resizeMode: Image.resizeMode.contain,
                    }}
                    resizeMethod={'scale'}
                  />
                </View>
              </Slide>
            </View>
          </Swiper>
        </View>

        <View
          style={marketingCarouselStyles.footerContainer}
          key="marketing-footer-container"
        >
          <View key="marketing-footer">
            {isFirst && (
              <TouchableOpacity
                key="marketing-footer-policy"
                style={marketingCarouselStyles.footerLoginContainer}
                onPress={() => {
                  Linking.openURL('https://www.hedvig.com/privacy/');
                }}
              >
                <Text style={[marketingCarouselStyles.footerPrivacyText]}>
                  Om dina personuppgifter
                </Text>
              </TouchableOpacity>
            )}

            <TouchableOpacity
              key="marketing-footer-cta"
              onPress={() => this.props.startChat()}
              style={[
                marketingCarouselStyles.footerCtaButton,
                isFirst && marketingCarouselStyles.footerCtaButtonIsFirst,
              ]}
            >
              <Text
                numberOfLines={1}
                style={[
                  marketingCarouselStyles.footerCtaButtonText,
                  isFirst && marketingCarouselStyles.footerCtaButtonTextIsFirst,
                ]}
              >
                Säg hej till Hedvig
              </Text>
            </TouchableOpacity>
            <View
              style={marketingCarouselStyles.footerLoginContainer}
              key="marketing-footer-login"
            >
              <View>
                <Text
                  style={[
                    marketingCarouselStyles.footerLoginText,
                    isFirst && marketingCarouselStyles.footerLoginTextIsFirst,
                  ]}
                >
                  Redan medlem?
                </Text>
              </View>
              <TouchableOpacity onPress={() => this.props.login()}>
                <Text style={marketingCarouselStyles.footerLoginCta}>
                  Logga in
                </Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </View>
    );
  }
}

const mapStateToProps = (state) => {
  const { activeMarketingScreenIndex } = state.marketing;
  return {
    activeMarketingScreenIndex,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    login: () => {
      dispatch({
        type: MARKETING_CHAT_LOGIN,
      });
    },
    startChat: () => {
      dispatch({
        type: MARKETING_CHAT_START,
      });
    },
    setActiveMarketingScreen: (index) => {
      return dispatch({
        type: MARKETING_SET_ACTIVE_SCREEN,
        payload: { index },
      });
    },
  };
};

const ConnectedMarketingCarousel = connect(
  mapStateToProps,
  mapDispatchToProps,
)(MarketingCarousel);

export { ConnectedMarketingCarousel as MarketingCarousel };
