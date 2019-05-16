import * as React from 'react';
import {
  Animated,
  View,
  Dimensions,
  ViewProps,
  TouchableWithoutFeedback,
} from 'react-native';
import {
  AnimationValueProvider,
  Parallel,
  Timing,
} from 'animated-react-native-components';
import { Mount } from 'react-lifecycle-components';
import { Container, ActionMap } from 'constate';
import { Delayed } from 'src/components/Delayed';

import {
  PanGestureHandler,
  State as GestureState,
} from 'react-native-gesture-handler';
import styled from '@sampettersson/primitives';

import { Bar } from './bar';

const OuterContainer = styled(View)({
  flex: 1
})

const heightPercentageToFraction = (heightPercentage: number) =>
  heightPercentage / 100;

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const Shadow = styled(AnimatedView)(
  ({ animatedValue }: { animatedValue: Animated.Value }) => ({
    backgroundColor: 'black',
    opacity: animatedValue.interpolate({
      inputRange: [0, 1],
      outputRange: [0, 0.6],
    }),
    height: '100%',
    width: '100%',
    position: 'absolute',
  }),
);

const WhiteBackground = styled(View)(() => ({
  height: Dimensions.get('window').height * 2,
  width: '100%',
  backgroundColor: 'white',
  borderTopLeftRadius: 20,
  borderTopRightRadius: 20,
  overflow: 'hidden',
}));

const ContentContainer = styled(View)(
  ({ heightPercentage }: { heightPercentage: number }) => ({
    height:
      Dimensions.get('window').height *
      heightPercentageToFraction(heightPercentage),
    width: '100%',
    position: 'relative',
  }),
);

const Translation = styled(AnimatedView)(
  ({
    translationY,
    heightPercentage,
  }: {
    translationY: Animated.Value;
    heightPercentage: number;
  }) => ({
    position: 'absolute',
    top: `-${heightPercentage}%`,
    width: '100%',
    transform: [
      {
        translateY: Animated.add(
          new Animated.Value(getStationaryTranslate()),
          translationY.interpolate({
            inputRange: getTranslateRange(heightPercentage),
            outputRange: getTranslateRange(heightPercentage),
            extrapolateLeft: 'clamp',
          }),
        ),
      },
    ],
  }),
);

const BOUNCINESS = 8;
const UNMOUNT_DELAY = 500;

const getStationaryTranslate = () => Dimensions.get('window').height;
const getInitialTranslate = (heightPercentage: number) =>
  50 +
  Dimensions.get('window').height *
  heightPercentageToFraction(heightPercentage);

const getTranslateRange = (heightPercentage: number) => [
  -(Dimensions.get('window').height * 0.94) +
  Dimensions.get('window').height *
  heightPercentageToFraction(heightPercentage),
  0,
];

const shouldBounceBack = ({
  velocityY,
  translationY,
  heightPercentage,
}: {
  velocityY: number;
  translationY: number;
  heightPercentage: number;
}) =>
  velocityY < 250 &&
  translationY <
  getStationaryTranslate() *
  (heightPercentageToFraction(heightPercentage) * 0.5);

interface State {
  open: boolean;
}

interface Actions {
  setOpen: (open: boolean) => void;
}

const actions: ActionMap<State, Actions> = {
  setOpen: (open) => () => ({
    open,
  }),
};

interface DraggableOverlayProps {
  onClose?: () => void;
  heightPercentage?: number;
  children: ((handleClose: (velocity?: number) => void) => React.ReactNode);
}

export const DraggableOverlay: React.SFC<DraggableOverlayProps> = ({
  onClose = () => { },
  heightPercentage = 30,
  children,
}) => (
    <Container actions={actions} initialState={{ open: true }}>
      {({ open, setOpen }) => (
        <AnimationValueProvider
          initialValue={getInitialTranslate(heightPercentage)}
        >
          {({ animatedValue }) => {
            const handleClose = (velocity?: number) => {
              Animated.spring(animatedValue, {
                velocity: velocity,
                bounciness: BOUNCINESS,
                toValue: getInitialTranslate(heightPercentage),
                useNativeDriver: true,
              }).start();
              setOpen(false);
              setTimeout(() => {
                onClose();
              }, UNMOUNT_DELAY + 50);
            };

            const animateToStart = () =>
              Animated.spring(animatedValue, {
                bounciness: BOUNCINESS,
                toValue: 0,
                useNativeDriver: true,
              }).start();

            return (
              <OuterContainer>
                <Delayed
                  mountChildren={open}
                  mountChildrenAfter={0}
                  unmountChildrenAfter={UNMOUNT_DELAY}
                >
                  <Parallel>
                    <Timing
                      toValue={open ? 1 : 0}
                      initialValue={0}
                      config={{ duration: UNMOUNT_DELAY / 2 }}
                    >
                      {(shadowAnimatedValue) => (
                        <TouchableWithoutFeedback onPress={() => handleClose()}>
                          <Shadow animatedValue={shadowAnimatedValue} />
                        </TouchableWithoutFeedback>
                      )}
                    </Timing>
                  </Parallel>
                  <Mount on={animateToStart}>{null}</Mount>
                  <PanGestureHandler
                    onHandlerStateChange={(event) => {
                      if (event.nativeEvent.oldState === GestureState.ACTIVE) {
                        if (
                          shouldBounceBack({
                            velocityY: event.nativeEvent.velocityY,
                            translationY: event.nativeEvent.translationY,
                            heightPercentage,
                          })
                        ) {
                          animateToStart();
                        } else {
                          handleClose(event.nativeEvent.velocityY);
                        }
                      }
                    }}
                    onGestureEvent={Animated.event(
                      [
                        {
                          nativeEvent: {
                            translationY: animatedValue,
                          },
                        },
                      ],
                      {
                        useNativeDriver: true,
                      },
                    )}
                  >
                    <Translation
                      translationY={animatedValue}
                      heightPercentage={heightPercentage}
                    >
                      <Bar />
                      <WhiteBackground>
                        <ContentContainer heightPercentage={heightPercentage}>
                          {typeof children === 'function'
                            ? children(handleClose)
                            : children}
                        </ContentContainer>
                      </WhiteBackground>
                    </Translation>
                  </PanGestureHandler>
                </Delayed>
              </OuterContainer>
            );
          }}
        </AnimationValueProvider>
      )}
    </Container>
  );
