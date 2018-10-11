import { ComponentRegistrator } from './types';

import { register as registerChat } from './chat/register';
import { register as registerMarketing } from './marketing';
import { register as registerOffer } from './offer';
import { register as registerNewOffer } from './new-offer';
import { register as registerDashboard } from './dashboard/register';
import { register as registerPayment } from './payment';
import { register as registerProfile } from './profile';
import { register as registerDebug } from './debug';

export const register = (registerComponent: ComponentRegistrator) => {
  registerChat(registerComponent);
  registerMarketing(registerComponent);
  registerOffer(registerComponent);
  registerDashboard(registerComponent);
  registerPayment(registerComponent);
  registerProfile(registerComponent);
  registerNewOffer(registerComponent);
  registerDebug(registerComponent);
};
