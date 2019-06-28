import * as React from 'react';
import { WebView, View } from 'react-native';
import { Container, ActionMap } from 'constate';
import styled from '@sampettersson/primitives';

import { NavigationEvents } from '../../navigation/events';
import {
  DirectDebitRegistrationComponent,
  DirectDebitStatus,
} from 'src/graphql/components';
import { Loader } from 'src/components/Loader';
import { Mount } from 'react-lifecycle-components';

import { PaymentSuccess } from './PaymentSuccess';
import { PaymentFailure } from './PaymentFailure';

interface State {
  showSuccess: boolean;
  showFailure: boolean;
}

interface Actions {
  setShowSuccess: () => void;
  setShowFailure: () => void;
}

const actions: ActionMap<State, Actions> = {
  setShowSuccess: () => () => ({
    showSuccess: true,
  }),
  setShowFailure: () => () => ({
    showFailure: true,
  }),
};

const FullSizeView = styled(View)({
  flex: 1,
});

interface StandalonePaymentProps {
  componentId: string;
}

export const StandalonePayment: React.SFC<StandalonePaymentProps> = ({
  componentId,
}) => (
  <Container
    actions={actions}
    initialState={{ showFailure: false, showSuccess: false }}
  >
    {({ showSuccess, showFailure, setShowSuccess, setShowFailure }) => (
      <DirectDebitRegistrationComponent>
        {(start, { loading, error, data, client }) =>
          (!loading && !data) || error ? (
            <Mount on={start}>
              <Loader />
            </Mount>
          ) : (
            <FullSizeView>
              <NavigationEvents
                onNavigationButtonPressed={() => {
                  Navigation.dismissModal(componentId);
                }}
              />
              {showSuccess && (
                <PaymentSuccess
                  onPressContinue={() => {
                    client.cache.writeData({
                      data: {
                        directDebitStatus: DirectDebitStatus.ACTIVE,
                      },
                    });

                    Navigation.dismissModal(componentId);
                  }}
                />
              )}
              {showFailure && (
                <PaymentFailure
                  onPressContinue={() => {
                    Navigation.dismissModal(componentId);
                  }}
                />
              )}
              {data &&
                !showSuccess &&
                !showFailure && (
                  <WebView
                    renderError={() => (
                      <PaymentFailure
                        onPressContinue={() => {
                          Navigation.dismissModal(componentId);
                        }}
                      />
                    )}
                    source={{
                      uri: data.startDirectDebitRegistration,
                    }}
                    onNavigationStateChange={(event) => {
                      if (!event.url) return;
                      if (showSuccess || showFailure) return;

                      if (event.url.match('success')) {
                        setShowSuccess();
                      } else if (event.url.match('fail')) {
                        setShowFailure();
                      }
                    }}
                  />
                )}
            </FullSizeView>
          )
        }
      </DirectDebitRegistrationComponent>
    )}
  </Container>
);
