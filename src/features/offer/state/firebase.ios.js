import { NativeModules } from 'react-native';

export const logEcommercePurchase = () => {
  NativeModules.NativeRouting.logEcommercePurchase();
};
