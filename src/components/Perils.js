import React from 'react';
import { View, Dimensions, Image, StyleSheet, Linking } from 'react-native';
import { default as SnapCarousel } from 'react-native-snap-carousel';
import {
  StyledCarouselContainer,
  StyledAlignedCarouselItems,
  StyledImageCarouselContainer,
  StyledCarouselHeading,
  StyledCarouselTexts,
  StyledCarouselParagraph,
  StyledParagraphToggleContainer,
} from './styles/carousel';
import { TextButton } from './Button';

const deviceWidth = Dimensions.get('window').width;
const perilContainerSize = 185;

const styles = StyleSheet.create({
  perilImage: { width: perilContainerSize, height: perilContainerSize },
  policyLinkContainer: {
    alignItems: 'center',
    paddingTop: 0,
    marginBottom: 40,
  },
});

class Perils extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      slideIndex: props.initialSlideIndex || 0,
      showFullDescription: false,
    };
  }

  _renderItem({ item }) {
    return (
      <View>
        <Image
          style={styles.perilImage}
          source={item.itemSrc}
          resizeMode="contain"
          key={item.id}
        />
      </View>
    );
  }

  getItem() {
    return this.props.items[this.state.slideIndex];
  }

  shortDescription() {
    return this.getItem().description;
  }

  shownDescription() {
    return this.state.showFullDescription
      ? this.getItem().longText
      : this.shortDescription();
  }

  maybePolicyLink() {
    if (this.state.showFullDescription && this.getItem().policyUrl) {
      return (
        <View style={styles.policyLinkContainer}>
          <TextButton
            title="Läsa mer"
            onPress={() => Linking.openUrl(this.getItem().policyUrl)}
          />
        </View>
      );
    }
  }

  maybeCta() {
    if (this.props.renderCta) {
      return (
        <StyledParagraphToggleContainer>
          {this.props.renderCta(this.getItem())}
        </StyledParagraphToggleContainer>
      );
    } else {
      return null;
    }
  }

  _onSnapToItem = (slideIndex) => {
    this.setState({ slideIndex, showFullDescription: false });
  };

  render() {
    const item = this.getItem();
    const title = item.title.includes('-\n')
      ? item.title.replace('-\n', '')
      : item.title;

    return (
      <React.Fragment>
        <StyledCarouselContainer>
          <StyledAlignedCarouselItems>
            <StyledImageCarouselContainer>
              <SnapCarousel
                data={this.props.items}
                renderItem={this._renderItem}
                sliderWidth={deviceWidth}
                sliderHeight={perilContainerSize}
                itemWidth={perilContainerSize}
                firstItem={this.state.slideIndex}
                inactiveSlideOpacity={0.4}
                inactiveSlideScale={0.7}
                removeClippedSubviews={false} // removeClippedSubviews fixes an issue where the item is not always initially rendered
                onSnapToItem={this._onSnapToItem}
              />
            </StyledImageCarouselContainer>
            <StyledCarouselTexts>
              <StyledCarouselHeading>{title}</StyledCarouselHeading>
              <StyledCarouselParagraph>
                {this.shownDescription()}
              </StyledCarouselParagraph>
              {this.maybePolicyLink()}
              {this.maybeCta()}
            </StyledCarouselTexts>
          </StyledAlignedCarouselItems>
        </StyledCarouselContainer>
      </React.Fragment>
    );
  }
}

const ConnectedPerils = Perils;

export { ConnectedPerils as Perils };
