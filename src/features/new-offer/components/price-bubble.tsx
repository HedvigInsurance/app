import * as React from 'react';
import { View, ViewProps, Text, Animated, Dimensions } from 'react-native';
import styled from '@sampettersson/primitives';
import { colors, fonts } from '@hedviginsurance/brand';
import { Sequence, Spring, Delay } from 'animated-react-native-components';
import { MonetaryAmountV2 } from 'src/graphql/components';
import { TranslationsConsumer } from 'src/components/translations/consumer';

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const LARGE_CIRCLE_SIZE = 180;
const SMALL_CIRCLE_SIZE = 125;

const getCircleSize = () => {
  const windowHeight = Dimensions.get('window').height;

  if (windowHeight < 700) {
    return SMALL_CIRCLE_SIZE;
  }

  return LARGE_CIRCLE_SIZE;
};

const Circle = styled(View)({
  height: getCircleSize(),
  width: getCircleSize(),
  borderRadius: getCircleSize() / 2,
  backgroundColor: colors.WHITE,
  alignItems: 'center',
  justifyContent: 'center',
  shadowColor: 'black',
  shadowOpacity: 0.05,
  shadowOffset: {
    width: 0,
    height: 2,
  },
});

const DiscountCircle = styled(Circle)({
  backgroundColor: colors.PINK,
  height: getCircleSize() * 0.54,
  width: getCircleSize() * 0.54,
  borderRadius: (getCircleSize() * 0.54) / 2,
  transform: [{ translateX: getCircleSize() * 0.8 }],
  position: 'absolute',
});

const PreDiscountText = styled(Text)({
  color: colors.WHITE,
  fontFamily: fonts.CIRCULAR,
  fontSize: getCircleSize() === LARGE_CIRCLE_SIZE ? 14 : 12,
  fontWeight: 'normal',
  textAlign: 'center',
});

const DiscountText = styled(Text)({
  color: colors.WHITE,
  fontFamily: fonts.CIRCULAR,
  fontSize: getCircleSize() === LARGE_CIRCLE_SIZE ? 14 : 12,
  fontWeight: 'bold',
  textAlign: 'center',
});

const Price = styled(Text)({
  color: colors.BLACK,
  fontSize: getCircleSize() === LARGE_CIRCLE_SIZE ? 60 : 40,
  fontFamily: fonts.CIRCULAR,
});

const MonthlyLabel = styled(Text)({
  color: colors.BLACK,
  fontSize: getCircleSize() === LARGE_CIRCLE_SIZE ? 20 : 18,
  fontFamily: fonts.CIRCULAR,
});

const GrossPrice = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  fontSize: 14,
  textDecorationLine: 'line-through',
  textDecorationStyle: 'solid',
});

const NetPrice = styled(Price)({
  color: colors.PINK,
});

interface PriceBubbleProps {
  price: MonetaryAmountV2;
  discountedPrice: MonetaryAmountV2;
  freeMonths: number;
}

const formatMonetaryAmount = (monetaryAmount: MonetaryAmountV2) =>
  Number(monetaryAmount.amount);

export const PriceBubble: React.SFC<PriceBubbleProps> = ({
  price,
  discountedPrice,
  freeMonths,
}) => (
  <Sequence>
    <Delay config={{ delay: 650 }} />
    <Spring
      config={{
        bounciness: 12,
      }}
      toValue={1}
      initialValue={0.5}
    >
      {(animatedValue) => (
        <AnimatedView
          style={{
            opacity: animatedValue.interpolate({
              inputRange: [0.5, 1],
              outputRange: [0, 1],
            }),
            transform: [{ scale: animatedValue }],
          }}
        >
          <Circle>
            {discountedPrice.amount !== price.amount ? (
              <>
                <GrossPrice>{formatMonetaryAmount(price)} kr/mån</GrossPrice>
                <NetPrice>{formatMonetaryAmount(discountedPrice)}</NetPrice>
              </>
            ) : (
              <Price>{formatMonetaryAmount(price)}</Price>
            )}
            <MonthlyLabel>kr/mån</MonthlyLabel>
          </Circle>

          {(discountedPrice.amount !== price.amount || freeMonths > 0) && (
            <DiscountCircle>
              {discountedPrice.amount !== price.amount ? (
                <TranslationsConsumer textKey="OFFER_SCREEN_INVITED_BUBBLE">
                  {(text) => <DiscountText>{text}</DiscountText>}
                </TranslationsConsumer>
              ) : (
                <>
                  <PreDiscountText>Rabatt!</PreDiscountText>
                  <DiscountText>{freeMonths} månader gratis!</DiscountText>
                </>
              )}
            </DiscountCircle>
          )}
        </AnimatedView>
      )}
    </Spring>
  </Sequence>
);
