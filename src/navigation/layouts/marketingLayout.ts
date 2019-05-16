import { Platform } from 'react-native';
import { registerExternalComponentId } from './../native-routing';

export const getMarketingLayout = () =>
  Platform.select({
    ios: () => {
      Navigation.events().registerComponentDidAppearListener(
        ({ componentId, componentName }) => {
          registerExternalComponentId(componentId, componentName);
        },
      );

      return {
        root: {
          stack: {
            children: [
              {
                externalComponent: {
                  name: 'marketingScreen',
                },
              },
            ],
            options: {
              topBar: {
                visible: false,
              },
            },
          },
        },
      };
    },
    android: () => {
      Navigation.events().registerComponentDidAppearListener(
        ({ componentId, componentName }) => {
          registerExternalComponentId(componentId, componentName);
        },
      );

      return {
        root: {
          stack: {
            children: [
              {
                externalComponent: {
                  name: 'marketingScreen',
                },
              },
            ],
            options: {
              topBar: {
                visible: false,
                drawBehind: true,
              },
            },
          },
        },
      };
    },
  })();
