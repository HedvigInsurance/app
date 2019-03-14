import * as React from 'react';
import {
  TouchableOpacity,
  Platform,
  View,
  ViewProps,
  Animated,
} from 'react-native';
import { Container, ActionMap } from 'constate';
import styled from '@sampettersson/primitives';
import { Sequence, Delay, Timing } from 'animated-react-native-components';
import { Navigation } from 'react-native-navigation';

import { SpeechBubbles } from 'src/components/icons/SpeechBubbles';
import { Mutation } from 'react-apollo';
import gql from 'graphql-tag';

import { OFFER_CHAT_COMPONENT } from 'src/navigation/components/offer-chat';

const AnimatedView = Animated.createAnimatedComponent<ViewProps>(View);

const ChatButtonContainer = styled(View)(
  Platform.select({
    ios: {},
    android: {
      paddingRight: 10,
      paddingLeft: 10,
    },
  }),
);

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

const OFFER_CLOSED_MUTATION = gql`
  mutation OfferClosed {
    offerClosed
  }
`;

export const ChatButton: React.SFC = () => (
  <ChatButtonContainer>
    <Mutation mutation={OFFER_CLOSED_MUTATION}>
      {(mutate) => (
        <>
          <Sequence>
            <Delay config={{ delay: 500 }} />
            <Timing toValue={1} initialValue={0} config={{ duration: 250 }}>
              {(animatedValue) => (
                <AnimatedView style={{ opacity: animatedValue }}>
                  <TouchableOpacity
                    onPress={() => {
                      mutate().then(() => {
                        Navigation.showOverlay({
                          component: {
                            name: OFFER_CHAT_COMPONENT.name,
                            options: {
                              layout: {
                                backgroundColor: 'transparent',
                              },
                            },
                          },
                        });
                      });
                    }}
                  >
                    <SpeechBubbles height={25} width={25} />
                  </TouchableOpacity>
                </AnimatedView>
              )}
            </Timing>
          </Sequence>
        </>
      )}
    </Mutation>
  </ChatButtonContainer>
);
