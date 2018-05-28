import { connect } from 'react-redux';

import React from 'react';
import { View, StyleSheet } from 'react-native';

import { verticalSizeClass, V_SPACIOUS } from '../../services/DimensionSizes';
import { PerilsOverview } from './PerilsOverview';
import { Hero } from './Hero';

const styles = StyleSheet.create({
  container: { flex: 1 },
  heroBackground: {
    backgroundColor: '#f8f7f9',
  },
});

class OfferScreen extends React.Component {
  render() {
    const category = this.props.insurance.categories[1];
    const regular = require('../../../assets/offer/hero/house.png');
    const spacious = require('../../../assets/offer/hero/house-xl.png');
    const heroImage =
      {
        [V_SPACIOUS]: spacious,
      }[verticalSizeClass] || regular;

    return (
      <View style={styles.container}>
        <PerilsOverview
          title="Lägenhetsskyddet"
          categoryTitle={category.title}
          description={
            <React.Fragment>
              Vi vet hur mycket ett hem betyder. Därför ger vi det ett riktigt
              bra skydd, så att du kan känna dig trygg i alla&nbsp;lägen.
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
