import * as React from 'react';
import {
  TouchableOpacity,
  View,
  ViewProps,
  Text,
  Animated,
} from 'react-native';
import styled from '@sampettersson/primitives';
import { colors, fonts } from '@hedviginsurance/brand';
import { Spacing } from 'src/components/Spacing';
import { MonetaryAmountV2 } from 'src/graphql/components';
import { TranslationsConsumer } from 'src/components/translations/consumer';
import { Sequence, Spring, Delay } from 'animated-react-native-components';

interface DiscountButtonProps {
  discount: MonetaryAmountV2;
  onPress: () => void;
}

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const ButtonStyle = styled(TouchableOpacity)({
  borderRadius: 32,
  paddingTop: 8,
  paddingRight: 16,
  paddingBottom: 8,
  paddingLeft: 16,
  borderColor: '#FFFFFF32',
  borderWidth: 1,
});

const TextStyle = styled(Text)({
  color: colors.WHITE,
  fontFamily: fonts.CIRCULAR,
  fontSize: 14,
});

export const DiscountButton: React.SFC<DiscountButtonProps> = ({
  discount,
  onPress,
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
          <ButtonStyle onPress={onPress}>
            <TranslationsConsumer
              textKey={
                Number(discount.amount) !== 0
                  ? 'OFFER_REMOVE_DISCOUNT_BUTTON'
                  : 'OFFER_ADD_DISCOUNT_BUTTON'
              }
            >
              {(text) => <TextStyle>{text}</TextStyle>}
            </TranslationsConsumer>
          </ButtonStyle>
        </AnimatedView>
      )}
    </Spring>
  </Sequence>
);
