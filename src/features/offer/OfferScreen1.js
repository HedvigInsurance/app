import { connect } from 'react-redux';

import React from 'react';
import {
  ScrollView,
  View,
  Text,
  StyleSheet,
  Dimensions,
  ImageBackground,
} from 'react-native';

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
  container: { flex: 1 },
  scroll: {
    flex: 1,
    width: viewportWidth,
    height: viewportHeight,
  },
  background: {
    width: viewportWidth,
    height: viewportHeight,
    backgroundColor: '#1be9b6',
  },
  scrollContent: {
    width: viewportWidth,
    height: viewportHeight,
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  content: {
    padding: 20,
    alignItems: 'center',
  },
  heading: {
    position: 'absolute',
    top: {
      [V_SPACIOUS]: 60,
      [V_REGULAR]: 50,
      [V_COMPACT]: 43,
    }[verticalSizeClass],
  },
  headingText: {
    fontFamily: 'circular-bold',
    fontSize: 26,
    lineHeight: 35,
    color: '#fff',
    textAlign: 'center',
  },
  categoryHeader: {
    marginTop: {
      [V_SPACIOUS]: '23%',
      [V_REGULAR]: '21%',
      [V_COMPACT]: '21%',
    }[verticalSizeClass],
    marginBottom: {
      [V_SPACIOUS]: '23%',
      [V_REGULAR]: '21%',
      [V_COMPACT]: '19%',
    }[verticalSizeClass],
    fontFamily: 'circular-bold',
    fontSize: 20,
    lineHeight: 20,
    color: '#fff',
    textAlign: 'center',
  },
});

class OfferScreen extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <ImageBackground
          style={styles.background}
          resizeMode={'cover'}
          source={require('../../../assets/offer/hero/intro.png')}
        >
          <ScrollView style={styles.scroll}>
            <View style={styles.scrollContent}>
              <View style={styles.heading}>
                <Text style={styles.headingText}>
                  Din hemförsäkring{'\n'}innehåller
                </Text>
              </View>
              <View style={styles.content}>
                <Text style={styles.categoryHeader}>Personskydd</Text>
                <Text style={styles.categoryHeader}>Prylskydd</Text>
                <Text style={styles.categoryHeader}>Lägenhetsskydd</Text>
              </View>
            </View>
          </ScrollView>
        </ImageBackground>
      </View>
    );
  }
}

const OfferContainer = connect(
  null,
  null,
)(OfferScreen);

export default OfferContainer;
