import React from 'react';
import { Provider } from 'react-redux';
import { Provider as ConstateProvider } from 'constate';
import { PersistGate } from 'redux-persist/es/integration/react';
import { ApolloProvider } from 'react-apollo';

import { ErrorBoundary } from './src/components/ErrorBoundary';
import { Loader } from './src/components/Loader';
import Dialog from './src/containers/Dialog';
import { TranslationsProvider } from './src/components/translations/provider';

import { Raven, Store, Persistor } from './src/setupApp';

import { client } from 'src/graphql/client';

export const HOC = (Component) => {
  class Screen extends React.Component {
    render() {
      return (
        <ErrorBoundary raven={Raven}>
          <ApolloProvider client={client}>
            <TranslationsProvider>
              <Provider store={Store}>
                <ConstateProvider devtools={__DEV__}>
                  <PersistGate loading={<Loader />} persistor={Persistor}>
                    <Component {...this.props} />
                    <Dialog />
                  </PersistGate>
                </ConstateProvider>
              </Provider>
            </TranslationsProvider>
          </ApolloProvider>
        </ErrorBoundary>
      );
    }
  }

  return Screen;
};
