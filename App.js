import React from 'react';
import { Platform } from 'react-native';
import { Provider } from 'react-redux';
import { Provider as ConstateProvider } from 'constate';
import { PersistGate } from 'redux-persist/es/integration/react';
import { ApolloProvider } from 'react-apollo';
import { gestureHandlerRootHOC } from 'react-native-gesture-handler';

import { ErrorBoundary } from './src/components/ErrorBoundary';
import { Loader } from './src/components/Loader';
import Dialog from './src/containers/Dialog';
import { TranslationsProvider } from './src/components/translations/provider';

import { Raven, Store, Persistor } from './src/setupApp';

import { NavigationContext } from './src/navigation/context';
import { client } from 'src/graphql/client';

export const HOC = (options) => (Component) => {
  class Screen extends React.Component {
    static options = options;

    render() {
      const { componentId } = this.props;
      return (
        <ErrorBoundary raven={Raven}>
          <ApolloProvider client={client}>
            <TranslationsProvider>
              <Provider store={Store}>
                <ConstateProvider devtools={__DEV__}>
                  <PersistGate loading={<Loader />} persistor={Persistor}>
                    <NavigationContext.Provider value={{ componentId }}>
                      <Component {...this.props} />
                      <Dialog />
                    </NavigationContext.Provider>
                  </PersistGate>
                </ConstateProvider>
              </Provider>
            </TranslationsProvider>
          </ApolloProvider>
        </ErrorBoundary>
      );
    }
  }

  return gestureHandlerRootHOC(Screen);
};
