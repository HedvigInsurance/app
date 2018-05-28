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
    // Is there a better way?
    const category = this.props.insurance.categories[0];
    const regular = require('../../../assets/offer/hero/you.png');
    const spacious = require('../../../assets/offer/hero/you-xl.png');
    const heroImage =
      {
        [V_SPACIOUS]: spacious,
      }[verticalSizeClass] || regular;

    return (
      <View style={styles.container}>
        <PerilsOverview
          title="Personskyddet"
          categoryTitle={category.title}
          description={
            <React.Fragment>
              Hedvig skyddar dig mot obehagliga saker som kan hända på
              hemmaplan, och det mesta som kan hända när du är ute
              och&nbsp;reser.
            </React.Fragment>
          }
          perils={category.perils}
          explainer={'Tryck på ikonerna för mer info'}
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
