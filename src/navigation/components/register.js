import { register as registerHedvigLogoTitle } from './hedvigLogoTitle';
import { register as registerFAB } from './fab';
import { register as registerSignButton } from './sign-button';
import { register as registerChatButton } from './chat-button';
import { register as registerCashbackPicker } from './cashback-picker';
import { register as registerOfferChat } from './offer-chat';
import { register as registerFilePicker } from './file-picker';
import { register as registerPeril } from './peril';
import { register as registerChangeStartDate } from './change-start-date';

export const register = (registerComponent) => {
  registerHedvigLogoTitle(registerComponent);
  registerFAB(registerComponent);
  registerSignButton(registerComponent);
  registerChatButton(registerComponent);
  registerCashbackPicker(registerComponent);
  registerOfferChat(registerComponent);
  registerFilePicker(registerComponent);
  registerPeril(registerComponent);
  registerChangeStartDate(registerComponent);
};
