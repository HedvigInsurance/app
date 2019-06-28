import { UIManager, AppRegistry } from 'react-native';
import Chat from './src/features/chat/Chat';
import { NewOffer } from './src/features/new-offer';
import { HOC } from './AndroidApp';
import { SignButton } from 'src/components/sign-button';

UIManager.setLayoutAnimationEnabledExperimental &&
  UIManager.setLayoutAnimationEnabledExperimental(true);

const WrappedChat = HOC(Chat);
const WrappedOffer = HOC(NewOffer);
const WrappedOfferSignButton = HOC(SignButton);

console.disableYellowBox = true;

AppRegistry.registerComponent('OfferSignButton', () => WrappedOfferSignButton);
AppRegistry.registerComponent('ChatScreen', () => WrappedChat);
AppRegistry.registerComponent('OfferScreen', () => WrappedOffer);
