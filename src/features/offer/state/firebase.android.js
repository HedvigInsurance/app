import { NativeModules } from 'react-native';

export const logEcommercePurchase = (event) => {
  NativeModules.ActivityStarter.logEvent('ecommerce_purchase', event);
};
