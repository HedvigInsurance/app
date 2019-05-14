import { register as registerHedvigLogoTitle } from './hedvigLogoTitle';
import { register as registerSignButton } from './sign-button';
import { register as registerChatButton } from './chat-button';
import { register as registerOfferChat } from './offer-chat';
import { register as registerFilePicker } from './file-picker';
import { register as registerPeril } from './peril';

export const register = (registerComponent) => {
  registerHedvigLogoTitle(registerComponent);
  registerSignButton(registerComponent);
  registerChatButton(registerComponent);
  registerOfferChat(registerComponent);
  registerFilePicker(registerComponent);
  registerPeril(registerComponent);
};
