import { ComponentRegistrator } from './types';

import { register as registerChat } from './chat/register';
import { register as registerNewOffer } from './new-offer';
import { register as registerPayment } from './payment';
import { register as registerDebug } from './debug';

export const register = (registerComponent: ComponentRegistrator) => {
  registerChat(registerComponent);
  registerPayment(registerComponent);
  registerNewOffer(registerComponent);
  registerDebug(registerComponent);
};
