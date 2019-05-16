import firebase from 'react-native-firebase';

const analytics = firebase.analytics();

export const logEcommercePurchase = (event) => {
  analytics.logEvent('ecommerce_purchase', event);
};
