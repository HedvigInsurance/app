import * as React from 'react';
import { Text, View, Animated, TextProps } from 'react-native';
import { colors, fonts } from '@hedviginsurance/brand';
import styled from '@sampettersson/primitives';
import { isIphoneX } from 'react-native-iphone-x-helper';
import { Container, ActionMap } from 'constate';

import { Spacing } from 'src/components/Spacing';
import { TranslationsConsumer } from 'src/components/translations/consumer';

const AnimatedText = Animated.createAnimatedComponent<TextProps>(Text);

const CONTENT_INSET = isIphoneX() ? 75 : 0;

const Title = styled(AnimatedText)(
  ({
    scrollAnimatedValue,
    positionFromTop,
  }: {
    scrollAnimatedValue: Animated.Value;
    positionFromTop: number;
  }) => ({
    fontFamily: fonts.CIRCULAR,
    fontWeight: '800',
    color: colors.WHITE,
    fontSize: 25,
    opacity: scrollAnimatedValue.interpolate({
      inputRange: [
        positionFromTop - CONTENT_INSET,
        positionFromTop - CONTENT_INSET + 60,
      ],
      outputRange: [0, 1],
      extrapolate: 'clamp',
    }),
  }),
);

const Body = styled(AnimatedText)(
  ({
    scrollAnimatedValue,
    positionFromTop,
  }: {
    scrollAnimatedValue: Animated.Value;
    positionFromTop: number;
  }) => ({
    fontFamily: fonts.CIRCULAR,
    color: colors.WHITE,
    fontWeight: '400',
    fontSize: 15,
    textAlign: 'center',
    opacity: scrollAnimatedValue.interpolate({
      inputRange: [
        positionFromTop - CONTENT_INSET,
        positionFromTop - CONTENT_INSET + 80,
      ],
      outputRange: [0, 1],
      extrapolate: 'clamp',
    }),
  }),
);

const Block = styled(View)({
  padding: 20,
  paddingBottom: isIphoneX() ? 110 : 145,
  alignItems: 'center',
  backgroundColor: colors.BLACK_PURPLE,
});

interface State {
  positionFromTop: number;
}

interface Actions {
  setPositionFromTop: (positionFromTop: number) => void;
}

interface GetHedvigProps {
  scrollAnimatedValue: Animated.Value;
}

const actions: ActionMap<State, Actions> = {
  setPositionFromTop: (positionFromTop) => () => ({
    positionFromTop,
  }),
};

export const GetHedvig: React.SFC<GetHedvigProps> = ({
  scrollAnimatedValue,
}) => (
  <Container actions={actions} initialState={{ positionFromTop: 0 }}>
    {({ positionFromTop, setPositionFromTop }) => (
      <View
        onLayout={(event) => setPositionFromTop(event.nativeEvent.layout.y)}
      >
        <Block>
          <TranslationsConsumer textKey="OFFER_GET_HEDVIG_TITLE">
            {(text) => (
              <Title
                scrollAnimatedValue={scrollAnimatedValue}
                positionFromTop={positionFromTop}
              >
                {text}
              </Title>
            )}
          </TranslationsConsumer>
          <Spacing height={15} />
          <TranslationsConsumer textKey="OFFER_GET_HEDVIG_BODY">
            {(text) => (
              <Body
                scrollAnimatedValue={scrollAnimatedValue}
                positionFromTop={positionFromTop}
              >
                {text}
              </Body>
            )}
          </TranslationsConsumer>
        </Block>
      </View>
    )}
  </Container>
);
