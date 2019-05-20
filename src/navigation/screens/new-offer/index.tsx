import React from 'react';
import { Mount } from 'react-lifecycle-components';
import { AsyncStorage } from 'react-native';

import { NewOffer } from 'src/features/new-offer';
import { ComponentRegistrator } from '../types';
import { NavigationEvents } from 'src/navigation/events';

import { IS_VIEWING_OFFER } from 'src/constants';
import { NEW_OFFER_OPTIONS } from 'src/navigation/screens/new-offer/options';

class NewOfferScreen extends React.Component {
  static get options() {
    return NEW_OFFER_OPTIONS;
  }

  render() {
    return (
      <>
        <NavigationEvents
          onNavigationButtonPressed={(_: any, componentId: string) =>
            Navigation.dismissModal(componentId)
          }
        />
        <Mount on={() => AsyncStorage.setItem(IS_VIEWING_OFFER, 'true')}>
          {null}
        </Mount>
        <NewOffer {...this.props} />
      </>
    );
  }
}

export const NEW_OFFER_SCREEN = {
  component: {
    name: 'NewOfferScreen',
  },
};

export const register = (registerComponent: ComponentRegistrator) =>
  registerComponent(NEW_OFFER_SCREEN.component.name, () => NewOfferScreen);
