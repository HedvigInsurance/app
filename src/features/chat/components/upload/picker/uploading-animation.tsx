import * as React from 'react';
import { View, Text, Animated, ViewProps } from 'react-native';
import styled from '@sampettersson/primitives';
import { Parallel, Spring } from 'animated-react-native-components';
import * as Progress from 'react-native-progress';
import { fonts, colors } from '@hedviginsurance/brand';
import { Spacing } from 'src/components/Spacing';

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const AnimationContainer = styled(View)({
  position: 'relative',
  width: '100%',
  height: '100%',
  alignItems: 'center',
  justifyContent: 'center',
});

const Content = styled(AnimatedView)({});

const Spinner = styled(AnimatedView)({
  position: 'absolute',
});

const SpinnerText = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.WHITE,
  fontSize: 12,
});

interface UploadingAnimationProps {
  isUploading: boolean;
}

export const UploadingAnimation: React.SFC<UploadingAnimationProps> = ({
  isUploading,
  children,
}) => (
  <AnimationContainer>
    <Parallel>
      <Spring
        toValue={isUploading ? 1 : 0}
        initialValue={0}
        config={{ bounciness: 5 }}
      >
        {(animatedValue) => (
          <>
            {isUploading && (
              <Spinner
                style={{
                  opacity: animatedValue.interpolate({
                    inputRange: [0, 1],
                    outputRange: [0, 1],
                  }),
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                <View style={{ width: 40, height: 40 }}>
                  <Progress.CircleSnail
                    animating={isUploading}
                    color={'white'}
                  />
                </View>
                <Spacing height={5} />
                <SpinnerText>Laddar upp...</SpinnerText>
              </Spinner>
            )}
            <Content
              style={{
                opacity: animatedValue.interpolate({
                  inputRange: [0, 1],
                  outputRange: [1, 0],
                }),
              }}
            >
              {children}
            </Content>
          </>
        )}
      </Spring>
    </Parallel>
  </AnimationContainer>
);
