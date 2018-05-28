import { connect } from 'react-redux';

import React from 'react';
import { View, StyleSheet } from 'react-native';

import { verticalSizeClass, V_SPACIOUS } from '../../services/DimensionSizes';
import { PerilsOverview } from './PerilsOverview';
import { Hero } from './Hero';

const styles = StyleSheet.create({
  container: { flex: 1 },
  heroBackground: {
    backgroundColor: '#f5f4f7',
  },
});

class OfferScreen extends React.Component {
  render() {
    const category = this.props.insurance.categories[2];
    const regular = require('../../../assets/offer/hero/stuff.png');
    const spacious = require('../../../assets/offer/hero/stuff-xl.png');
    const heroImage =
      {
        [V_SPACIOUS]: spacious,
      }[verticalSizeClass] || regular;

    return (
      <View style={styles.container}>
        <PerilsOverview
          title="Prylskyddet"
          categoryTitle={category.title}
          description={
            <React.Fragment>
              Med Hedvig får du ett komplett skydd för dina prylar.
              Drulleförsäkring ingår och täcker prylar värda upp till
              50&nbsp;000&nbsp;kr&nbsp;styck.
            </React.Fragment>
          }
          perils={category.perils}
          hero={
            <Hero containerStyle={styles.heroBackground} source={heroImage} />
          }
        />
      </View>
    );
  }
}

const OfferContainer = connect(
  null,
  null,
)(OfferScreen);

export default OfferContainer;
