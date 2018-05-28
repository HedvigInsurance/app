import { connect } from 'react-redux';

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Linking,
  Image,
  TouchableOpacity,
  ScrollView,
} from 'react-native';

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
import { INSURANCE_TYPES } from '../../constants';
import { CheckedBullet } from './CheckedBullet';
import { Hero } from './Hero';

const styles = StyleSheet.create({
  container: { flex: 1 },
  scroll: {
    backgroundColor: '#fff',
    flex: 1,
  },
  scrollContent: {
    flex: 1,
    paddingBottom: 80,
  },
  content: {
    flex: 1,
    padding: 20,
    paddingTop: 25,
    paddingBottom: 15,
  },
  heroBackground: {
    backgroundColor: '#f8f7f9',
  },
  header: {
    fontFamily: 'circular-bold',
    fontSize: 23,
    lineHeight: 32,
    color: '#414150',
    marginBottom: 10,
    textAlign: 'center',
  },
  subHeader: {
    fontFamily: 'circular',
    fontSize: 20,
    lineHeight: 25,
    color: '#414150',
    marginTop: 10,
  },
  bulletContainer: {
    width: {
      [H_SPACIOUS]: 270,
      [H_REGULAR]: 270,
      [H_COMPACT]: '97%',
    }[horizontalSizeClass],
    alignSelf: 'center',
  },
  legalLinkWrapper: {
    position: 'relative',
    paddingLeft: 5,
    paddingRight: 5,
    marginTop: 0,
  },
  legalLinkContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  legalLinkContent: {
    alignItems: 'center',
  },
  legalLink: {
    width: '33%',
    alignItems: 'center',
  },
  legalLinkIcon: {
    width: 55,
    height: 55,
  },
  legalLinkLabel: {
    paddingLeft: 0,
    paddingRight: 0,
    fontFamily: 'circular',
    fontSize: {
      [V_SPACIOUS]: 15,
      [V_REGULAR]: 15,
      [V_COMPACT]: 14,
    }[verticalSizeClass],
    color: '#9B9BAA',
    textAlign: 'center',
    marginTop: 7,
    marginBottom: 10,
  },
});

const HEDVIG_INTEGRITET_S3_LINK =
  'https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/Hedvig+-+integritetspolicy.pdf';

const hitSlop = {
  top: 5,
  right: 5,
  bottom: 5,
  left: 5,
};

class OfferScreen extends React.Component {
  render() {
    const insuranceType = this.props.insurance.insuranceType;
    const isApartmentOwner =
      insuranceType === INSURANCE_TYPES.BRF ||
      insuranceType === INSURANCE_TYPES.SUBLET_BRF;
    const regular = require('../../../assets/offer/hero/legal.png');
    const spacious = require('../../../assets/offer/hero/legal-xl.png');
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
              <View>
                <Text style={styles.header}>Viktiga villkor</Text>
              </View>
              <View style={styles.bulletContainer}>
                <CheckedBullet
                  label={
                    <React.Fragment>
                      Sveriges enda försäkring{'\n'}helt utan bindningstid
                    </React.Fragment>
                  }
                />
                {isApartmentOwner && (
                  <CheckedBullet
                    label={
                      <React.Fragment>
                        Lägenheten är försäkrad{'\n'}utan begränsning
                      </React.Fragment>
                    }
                  />
                )}
                <CheckedBullet
                  label={
                    <React.Fragment>
                      Maxersättning för prylarna{'\n'}i ditt hem är 1 miljon kr
                    </React.Fragment>
                  }
                />
                <CheckedBullet
                  label={
                    <React.Fragment>
                      Självrisken är 1&nbsp;500&nbsp;kr
                    </React.Fragment>
                  }
                />
              </View>
            </View>

            <View style={styles.legalLinkWrapper}>
              <View style={styles.legalLinkContainer}>
                <TouchableOpacity
                  onPress={() =>
                    Linking.openURL(this.props.insurance.presaleInformationUrl)
                  }
                  hitSlop={hitSlop}
                  style={styles.legalLink}
                >
                  <View style={styles.legalLinkContent}>
                    <Image
                      style={styles.legalLinkIcon}
                      source={require('../../../assets/icons/offer/legal/forkopsinformation.png')}
                    />
                    <Text style={styles.legalLinkLabel}>
                      Förköps-{'\n'}information
                    </Text>
                  </View>
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() =>
                    Linking.openURL(this.props.insurance.policyUrl)
                  }
                  hitSlop={hitSlop}
                  style={styles.legalLink}
                >
                  <View style={styles.legalLinkContent}>
                    <Image
                      style={styles.legalLinkIcon}
                      source={require('../../../assets/icons/offer/legal/forsakringsvillkor.png')}
                    />
                    <Text style={styles.legalLinkLabel}>
                      Försäkrings-{'\n'}villkor
                    </Text>
                  </View>
                </TouchableOpacity>
                <TouchableOpacity
                  onPress={() => Linking.openURL(HEDVIG_INTEGRITET_S3_LINK)}
                  hitSlop={hitSlop}
                  style={styles.legalLink}
                >
                  <View style={styles.legalLinkContent}>
                    <Image
                      style={styles.legalLinkIcon}
                      source={require('../../../assets/icons/offer/legal/personuppgiftspolicy.png')}
                    />
                    <Text style={styles.legalLinkLabel}>
                      Personuppgifts-{'\n'}policy
                    </Text>
                  </View>
                </TouchableOpacity>
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
