import * as React from 'react';
import {
  Animated,
  ScrollView,
  ScrollViewProps,
  View,
  ViewProps,
  Platform,
  NativeModules,
} from 'react-native';
import styled from '@sampettersson/primitives';
import { colors } from '@hedviginsurance/brand';

import { PriceBubble } from 'src/features/new-offer/components/price-bubble';
import { FeaturesBubbles } from 'src/features/new-offer/components/features-bubbles';
import { AnimationValueProvider } from 'animated-react-native-components';
import { Spacing } from 'src/components/Spacing';
import { ScrollContent } from 'src/features/new-offer/components/scroll-content';
import { Checkout } from 'src/features/new-offer/components/checkout';
import { SignButton } from 'src/features/new-offer/components/sign-button';
import { AndroidHeader } from 'src/features/new-offer/android-header';
import { Provider } from 'constate';

import { NewOfferComponent } from 'src/graphql/components';
import { DiscountButton } from './components/discount-button';

const AnimatedScrollView = Animated.createAnimatedComponent<ScrollViewProps>(
  ScrollView,
);

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const ScrollContainer = styled(AnimatedScrollView)({
  backgroundColor: colors.BLACK_PURPLE,
});

interface FixedContainerProps {
  animatedValue: Animated.Value;
}

const FixedContainer = styled(AnimatedView)(
  ({ animatedValue }: FixedContainerProps) => ({
    alignItems: 'center',
    transform: [
      {
        translateY: Animated.divide(animatedValue, new Animated.Value(1.25)),
      },
    ],
  }),
);

interface FeaturesContainer {
  animatedValue: Animated.Value;
}

const FeaturesContainer = styled(AnimatedView)(
  ({ animatedValue }: FeaturesContainer) => ({
    transform: [
      {
        translateY: Animated.divide(
          animatedValue,
          new Animated.Value(2),
        ).interpolate({
          inputRange: [-500, 0],
          outputRange: [180, 0],
          extrapolateRight: 'clamp',
        }),
      },
    ],
  }),
);

const getScrollHandler = (animatedValue: Animated.Value) =>
  Animated.event(
    [
      {
        nativeEvent: {
          contentOffset: {
            y: animatedValue,
          },
        },
      },
    ],
    {
      useNativeDriver: true,
    },
  );

interface ScrollToParams {
  x: number;
  y: number;
  animated: boolean;
}

interface ScrollViewElement {
  scrollTo: (params: ScrollToParams) => void;
  flashScrollIndicators: () => void;
}

interface AnimatedScrollViewComponent {
  getNode: () => ScrollViewElement;
}

const NewOfferRef = React.createRef<AnimatedScrollViewComponent>();

const bounceScrollView = () => {
  const scrollView = NewOfferRef.current!.getNode();

  scrollView.scrollTo({
    x: 0,
    y: -25,
    animated: true,
  });

  setTimeout(() => {
    scrollView.scrollTo({
      x: 0,
      y: 0,
      animated: true,
    });
    scrollView.flashScrollIndicators();
  }, 250);
};

export const NewOffer: React.SFC = () => (
  <Provider>
    <NewOfferComponent>
      {({ data, loading, error, refetch, updateQuery }) =>
        loading || error ? null : (
          <>
            <AnimationValueProvider initialValue={0}>
              {({ animatedValue }) => (
                <>
                  {Platform.OS === 'android' && (
                    <AndroidHeader subtitle={data!.insurance.address!} />
                  )}
                  <ScrollContainer
                    onScroll={getScrollHandler(animatedValue)}
                    scrollEventThrottle={1}
                    contentContainerStyle={{
                      alignItems: 'center',
                    }}
                    innerRef={NewOfferRef}
                  >
                    <FixedContainer animatedValue={animatedValue}>
                      <Spacing height={15} />
                      <PriceBubble
                        discountedPrice={data!.insurance!.cost!.monthlyNet}
                        price={data!.insurance!.cost!.monthlyGross}
                      />
                      <Spacing height={15} />
                      <FeaturesContainer animatedValue={animatedValue}>
                        <FeaturesBubbles
                          onPress={() => bounceScrollView()}
                          personsInHousehold={
                            data!.insurance.personsInHousehold!
                          }
                          insuredAtOtherCompany={
                            data!.insurance.insuredAtOtherCompany!
                          }
                          type={data!.insurance.type!}
                        />
                        <DiscountButton
                          discount={data!.insurance!.cost!.monthlyDiscount}
                          onPress={() => {
                            if (
                              Number(
                                data!.insurance!.cost!.monthlyDiscount!.amount,
                              ) !== 0
                            ) {
                              if (Platform.OS === 'ios') {
                                NativeModules.NativeRouting.showRemoveCodeAlert(
                                  true,
                                ).then((didRemoveCode: boolean) => {
                                  if (didRemoveCode) {
                                    updateQuery((queryData) => ({
                                      ...queryData!,
                                      insurance: {
                                        ...queryData!.insurance,
                                        cost: {
                                          __typename: 'InsuranceCost',
                                          monthlyDiscount: {
                                            __typename: 'MonetaryAmountV2',
                                            amount: '0.00',
                                          },
                                          monthlyNet: queryData.insurance.cost!
                                            .monthlyGross,
                                          monthlyGross: queryData.insurance
                                            .cost!.monthlyGross,
                                        },
                                      },
                                    }));
                                  }
                                });
                              }
                              if (Platform.OS === 'android') {
                                NativeModules.ActivityStarter.showRemoveCodeAlert().then(
                                  (didRemoveCode: boolean) => {
                                    if (didRemoveCode) {
                                      updateQuery((queryData) => ({
                                        ...queryData!,
                                        insurance: {
                                          ...queryData!.insurance,
                                          cost: {
                                            __typename: 'InsuranceCost',
                                            monthlyDiscount: {
                                              __typename: 'MonetaryAmountV2',
                                              amount: '0.00',
                                            },
                                            monthlyNet: queryData.insurance
                                              .cost!.monthlyGross,
                                            monthlyGross: queryData.insurance
                                              .cost!.monthlyGross,
                                          },
                                        },
                                      }));
                                    }
                                  },
                                );
                              }
                            } else {
                              if (Platform.OS === 'ios') {
                                NativeModules.NativeRouting.showRedeemCodeOverlay(
                                  true,
                                ).then((redeemResponse: string) => {
                                  if (redeemResponse != null) {
                                    updateQuery((queryData) => ({
                                      ...queryData!,
                                      insurance: {
                                        ...queryData!.insurance,
                                        cost: JSON.parse(redeemResponse),
                                      },
                                    }));
                                  }
                                });
                              }
                              if (Platform.OS === 'android') {
                                NativeModules.ActivityStarter.showRedeemCodeOverlay().then(
                                  (redeemResponse: string) => {
                                    if (redeemResponse != null) {
                                      updateQuery((queryData) => ({
                                        ...queryData!,
                                        insurance: {
                                          ...queryData!.insurance,
                                          cost: JSON.parse(redeemResponse),
                                        },
                                      }));
                                    }
                                  },
                                );
                              }
                            }
                          }}
                        />
                      </FeaturesContainer>
                    </FixedContainer>
                    <ScrollContent
                      insuredAtOtherCompany={
                        data!.insurance.insuredAtOtherCompany!
                      }
                      scrollAnimatedValue={animatedValue}
                    />
                  </ScrollContainer>
                  <SignButton scrollAnimatedValue={animatedValue} />
                  <Checkout monthlyCost={data!.insurance.monthlyCost!} />
                </>
              )}
            </AnimationValueProvider>
          </>
        )
      }
    </NewOfferComponent>
  </Provider>
);
