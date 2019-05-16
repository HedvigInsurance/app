import { Platform } from 'react-primitives';

interface Constants {
  statusBarHeight?: number;
}

export let navigationConstants: Constants = {};

export const getNavigationConstants = async (): Promise<void> =>
  new Promise<void>((resolve) => {
    if (Platform.OS === 'android') {
      resolve();
      return;
    }
    Navigation.constants().then((constants) => {
      navigationConstants = constants;
      resolve();
    });
  });
