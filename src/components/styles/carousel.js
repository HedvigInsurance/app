import React from 'react';
import { View, ScrollView, Text, StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  carouselContainer: { flex: 1, backgroundColor: '#ffffff' },
  alignedCarouselItems: { flex: 1, alignItems: 'center', paddingTop: 32 },
  carouselTexts: { marginTop: 30 },
  imageCarouselContainer: { height: 185 },
  carouselHeading: {
    fontFamily: 'merriweather',
    fontSize: 24,
    textAlign: 'center',
    paddingBottom: 24,
    marginRight: 40,
    marginLeft: 40,
  },
  carouselParagraph: {
    fontFamily: 'circular',
    fontSize: 16,
    color: '#9b9baa',
    textAlign: 'center',
    marginRight: 40,
    marginBottom: 40,
    marginLeft: 40,
    lineHeight: 20,
  },
  paragraphToggleContainer: { marginBottom: 40 },
});

export class StyledCarouselContainer extends React.Component {
  render() {
    return <View {...this.props} style={styles.carouselContainer} />;
  }
}

export class StyledAlignedCarouselItems extends React.Component {
  render() {
    return <View {...this.props} style={styles.alignedCarouselItems} />;
  }
}

export class StyledCarouselTexts extends React.Component {
  render() {
    return <ScrollView {...this.props} style={styles.carouselTexts} />;
  }
}

export class StyledImageCarouselContainer extends React.Component {
  render() {
    return <View {...this.props} style={styles.imageCarouselContainer} />;
  }
}

export class StyledCarouselHeading extends React.Component {
  render() {
    return <Text {...this.props} style={styles.carouselHeading} />;
  }
}

export class StyledCarouselParagraph extends React.Component {
  render() {
    return <Text {...this.props} style={styles.carouselParagraph} />;
  }
}

export class StyledParagraphToggleContainer extends React.Component {
  render() {
    return <View {...this.props} style={styles.paragraphToggleContainer} />;
  }
}
