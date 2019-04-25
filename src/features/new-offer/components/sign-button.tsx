import * as React from 'react';
import styled from '@sampettersson/primitives';
import {
  TouchableOpacity,
  ViewProps,
  Animated,
  View,
  Text,
  Dimensions,
  Platform,
} from 'react-native';
import { BankID } from 'src/components/icons/BankID';
import { NavigationEvents } from 'src/navigation/events';
import { Parallel, Spring } from 'animated-react-native-components';
import { fonts, colors } from '@hedviginsurance/brand';
import { AnimationValueListener } from 'src/components/animation-value-listener';
import { TranslationsConsumer } from 'src/components/translations/consumer';
import { Update } from 'react-lifecycle-components';
import { AndroidOfferState } from './android-offer-state';

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const ButtonContainer = styled(TouchableOpacity)({
  width: 190,
  height: 50,
  borderRadius: 30,
  backgroundColor: 'white',
  alignItems: 'center',
  justifyContent: 'space-between',
  paddingLeft: 25,
  paddingRight: 25,
  flexDirection: 'row',
  shadowColor: colors.BLACK_PURPLE,
  elevation: 5,
  shadowOpacity: 0.15,
  shadowRadius: 15,
  shadowOffset: {
    width: 1,
    height: 1,
  },
});

const GetText = styled(Text)({
  fontSize: 17,
  fontFamily: fonts.CIRCULAR,
  fontWeight: '500',
  color: colors.BLACK,
});

const BounceUpView = styled(AnimatedView)(
  ({ animatedValue }: { animatedValue: Animated.Value }) => ({
    position: 'absolute',
    bottom: 30,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    transform: [
      {
        translateY: animatedValue,
      },
    ],
  }),
);

interface SignButtonProps {
  scrollAnimatedValue: Animated.Value;
}

const ButtonView: React.SFC<{ onClick: () => void }> = ({ onClick }) => (
  <ButtonContainer
    onPress={onClick}                        >
    <TranslationsConsumer textKey="OFFER_SIGN_BUTTON">
      {(text) => <GetText>{text}</GetText>}
    </TranslationsConsumer>
    <BankID width={20} height={20} />
  </ButtonContainer>
)

export const SignButton: React.SFC<SignButtonProps> = ({
  scrollAnimatedValue,
}) => (
    <AnimationValueListener
      testValue={(value) => {
        return value >= Dimensions.get('window').height * 0.5

      }}
      animatedValue={scrollAnimatedValue}
    >
      {(isActive) => (
        <>
          {Platform.OS === 'ios' ? (
            <NavigationEvents>
              {(triggerEvent: (event: { id: string }) => void) => (
                <Update<boolean>
                  was={() => {
                    if (isActive) {
                      triggerEvent({ id: 'HideSignButton' });
                    } else {
                      triggerEvent({ id: 'ShowSignButton' });
                    }
                  }}
                  watched={isActive}
                >
                  {null}
                </Update>
              )}
            </NavigationEvents>
          ) : (
              <AndroidOfferState>
                {({ setTopSignButtonVisibility }) => (
                  <Update<boolean>
                    was={() => {
                      if (isActive) {
                        setTopSignButtonVisibility(false)
                      } else {
                        setTopSignButtonVisibility(true)
                      }
                    }}
                    watched={isActive}
                  >
                    {null}
                  </Update>
                )}
              </AndroidOfferState>
            )}
          <Parallel>
            <Spring
              toValue={isActive ? 0 : 100}
              initialValue={100}
              config={{ bounciness: 10, velocity: 2 }}
            >
              {(animatedValue) => (
                <BounceUpView animatedValue={animatedValue}>
                  {Platform.OS === 'ios' ? (
                    <NavigationEvents>
                      {(triggerEvent: (event: { id: string }) => void) => (
                        <ButtonView onClick={() => {
                          triggerEvent({
                            id: 'SignButtonPressed',
                          })
                        }}
                        />
                      )}
                    </NavigationEvents>
                  ) : (
                      <AndroidOfferState>
                        {({ setIsCheckingOut }) => (
                          <ButtonView onClick={() => setIsCheckingOut(true)} />
                        )}
                      </AndroidOfferState>
                    )}
                </BounceUpView>
              )}
            </Spring>
          </Parallel>
        </>
      )}
    </AnimationValueListener>
  );
