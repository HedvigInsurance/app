import { connect } from 'react-redux';

import React from 'react';
import {
  Text,
  Image,
  View,
  SafeAreaView,
  Dimensions,
  StyleSheet,
  TouchableOpacity,
  Platform,
} from 'react-native';
import { DangerZone } from 'expo';
const { Lottie } = DangerZone;

import {
  verticalSizeClass,
  V_COMPACT,
  V_REGULAR,
  V_SPACIOUS,
} from '../../services/DimensionSizes';

const animationModule = require('../../../assets/animations/claims_demo.json');

const { width: viewportWidth } = Dimensions.get('window');

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
  },
  content: {
    flex: 1,
    flexDirection: 'column',
    alignItems: 'center',
  },
  spacer: {
    flex: 1,
  },
  header: {
    marginTop: {
      [V_COMPACT]: 10,
      [V_REGULAR]: 25,
      [V_SPACIOUS]: 35,
    }[verticalSizeClass],
    fontFamily: 'circular-bold',
    fontSize: 23,
    lineHeight: 32,
    color: '#414150',
    marginBottom: 0,
    textAlign: 'center',
  },
  overlay: {
    backgroundColor: 'white',
    width: '100%',
    height: '100%',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1,
  },
  play: {
    width: 100,
    height: 100,
    borderRadius: 60,
    backgroundColor: 'rgba(101, 30, 255, 1.0)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  playIcon: {
    width: 35,
    height: 44,
    left: 6,
  },
  // Maintain aspect ratio for animation
  animationContainer: {
    width: viewportWidth * 0.89,
    overflow: 'hidden',
    height: viewportWidth * 1.15,
    marginTop: {
      [V_COMPACT]: 19,
      [V_REGULAR]: 30,
      [V_SPACIOUS]: 30,
    }[verticalSizeClass],
    borderRadius: 15,
    backgroundColor: '#fff',
  },
  animation: {
    height: viewportWidth * 1.62,
    width: viewportWidth * 0.94,
    // Some crazy scaling difference per platform - resolution independent!
    top: -(viewportWidth * (Platform.OS === 'ios' ? 0.25 : 0.45)),
    left: -(viewportWidth * (Platform.OS === 'ios' ? 0.012 : 0.025)),
  },
});

const hitSlop = {
  top: 20,
  left: 20,
  bottom: 20,
  right: 20,
};

class OfferScreen extends React.Component {
  state = {
    isPlaying: false,
  };

  componentDidUpdate() {
    const { isActive } = this.props;
    if (!isActive) {
      if (this.state.isPlaying) {
        this.setState({
          isPlaying: false,
        });
      }
      this.animation.reset();
    }
  }

  play() {
    this.setState({
      isPlaying: true,
    });
    this.animation.play();
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.content}>
          <SafeAreaView>
            <Text style={styles.header}>
              Så funkar det när{'\n'}du har en skada
            </Text>
          </SafeAreaView>
          <View style={styles.animationContainer}>
            {!this.state.isPlaying ? (
              <View style={styles.overlay}>
                <TouchableOpacity
                  style={styles.play}
                  hitSlop={hitSlop}
                  onPress={() => this.play()}
                >
                  <Image
                    style={styles.playIcon}
                    source={require('../../../assets/icons/offer/offer-play.png')}
                  />
                </TouchableOpacity>
              </View>
            ) : null}

            <Lottie
              ref={(animation) => {
                this.animation = animation;
              }}
              style={styles.animation}
              loop={true}
              autoplay={false}
              source={animationModule}
            />
          </View>
        </View>
      </View>
    );
  }
}

const OfferContainer = connect(
  null,
  null,
)(OfferScreen);

export default OfferContainer;
