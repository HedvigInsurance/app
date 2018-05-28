import { connect } from 'react-redux';

import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import * as R from 'ramda';

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
import { Hero } from './Hero';

const styles = StyleSheet.create({
  container: { flex: 1 },
  scroll: {
    flex: 1,
    backgroundColor: '#fff',
  },
  scrollContent: {
    paddingBottom: 80,
  },
  content: {
    flex: 1,
    padding: 20,
    paddingTop: 25,
    paddingBottom: 10,
  },
  heroBackground: {
    backgroundColor: '#651EFF',
  },
  heading: {
    fontFamily: 'circular-bold',
    fontSize: 23,
    lineHeight: 32,
    color: '#414150',
    marginBottom: {
      [V_SPACIOUS]: 15,
      [V_REGULAR]: 15,
      [V_COMPACT]: 0,
    }[verticalSizeClass],
    textAlign: 'center',
  },
  stepsContainer: {
    width: {
      [H_SPACIOUS]: 350,
      [H_REGULAR]: 320,
      [H_COMPACT]: '100%',
    }[horizontalSizeClass],
    alignSelf: 'center',
  },
  step: {
    flexDirection: 'row',
    marginTop: 10,
    marginBottom: {
      [V_SPACIOUS]: 20,
      [V_REGULAR]: 20,
      [V_COMPACT]: 6,
    }[verticalSizeClass],
    position: 'relative',
    alignItems: 'center',
  },
  stepNumber: {
    backgroundColor: '#651EFF',
    width: 35,
    height: 35,
    marginRight: 15,
    borderRadius: 20,
    justifyContent: 'center',
  },
  stepNumberText: {
    top: -1,
    position: 'relative',
    fontFamily: 'circular',
    fontSize: 19,
    color: 'white',
    textAlign: 'center',
  },
  stepLabel: {
    flex: 1,
    fontFamily: 'circular',
    fontSize: 19,
    color: '#9B9BAA',
    lineHeight: 27,
  },
  explainer: {
    fontFamily: 'circular',
    fontSize: 19,
    color: '#9B9BAA',
    lineHeight: 27,
    textAlign: 'center',
    marginTop: 10,
  },
});

const insuranceNames = [
  {
    currentInsurerName: 'LANSFORSAKRINGAR',
    displayName: <Text>från&nbsp;Länsförsäkringar</Text>,
  },
  {
    currentInsurerName: 'IF',
    displayName: <Text>från&nbsp;If</Text>,
  },
  {
    currentInsurerName: 'FOLKSAM',
    displayName: <Text>från&nbsp;Folksam</Text>,
  },
  {
    currentInsurerName: 'TRYGG_HANSA',
    displayName: <Text>från&nbsp;Trygg-Hansa</Text>,
  },
  {
    currentInsurerName: 'MODERNA',
    displayName: <Text>från Moderna&nbsp;Försäkringar</Text>,
  },
  {
    currentInsurerName: 'ICA',
    displayName: <Text>från ICA&nbsp;Försäkring</Text>,
  },
  {
    currentInsurerName: 'GJENSIDIGE',
    displayName: <Text>från&nbsp;Gjensidige</Text>,
  },
  {
    currentInsurerName: 'VARDIA',
    displayName: <Text>från&nbsp;Vardia</Text>,
  },
  {
    currentInsurerName: 'OTHER',
    displayName: <Text>från din nuvarande försäkring</Text>,
  },
];

class OfferScreen extends React.Component {
  render() {
    const { currentInsurerName } = this.props.insurance;
    const insuranceName = R.find(
      R.propEq('currentInsurerName', currentInsurerName),
    )(insuranceNames).displayName;
    const regular = require('../../../assets/offer/hero/switch.png');
    const spacious = require('../../../assets/offer/hero/switch-xl.png');
    const heroImage =
      {
        [V_SPACIOUS]: spacious,
      }[verticalSizeClass] || regular;

    return (
      <View style={styles.container}>
        <Hero containerStyle={styles.heroBackground} source={heroImage} />
        <ScrollView style={styles.scroll}>
          <View style={styles.scrollContent}>
            <View style={styles.content}>
              <Text style={styles.heading}>
                Hedvig sköter bytet {insuranceName}
              </Text>

              <View style={styles.stepsContainer}>
                <View style={styles.step}>
                  <View style={styles.stepNumber}>
                    <Text style={styles.stepNumberText}>1</Text>
                  </View>
                  <Text style={styles.stepLabel}>
                    Signera med ditt mobila&nbsp;BankID
                  </Text>
                </View>
                <View style={styles.step}>
                  <View style={styles.stepNumber}>
                    <Text style={styles.stepNumberText}>2</Text>
                  </View>
                  <Text style={styles.stepLabel}>
                    Hedvig kontaktar ditt försäkringsbolag och säger upp din
                    gamla försäkring
                  </Text>
                </View>
                <View style={styles.step}>
                  <View style={styles.stepNumber}>
                    <Text style={styles.stepNumberText}>3</Text>
                  </View>
                  <Text style={styles.stepLabel}>
                    Din Hedvigförsäkring aktiveras samma dag som din gamla
                    försäkring går ut
                  </Text>
                </View>
              </View>
            </View>
          </View>
        </ScrollView>
      </View>
    );
  }
}

const OfferContainer = connect(
  null,
  null,
)(OfferScreen);

export default OfferContainer;
