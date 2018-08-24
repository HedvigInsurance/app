import { Platform } from 'react-native';
import React from 'react';
import { Profile } from '../../../features/profile/newprofile';
import Fab from '../../../features/dashboard/containers/fab';

class ProfileScreen extends React.Component {
  static get options() {
    return {
      topBar: {
        visible: false,
      },
      statusBar: {
        visible: true,
        style: 'dark',
      },
    };
  }

  render() {
    return (
      <React.Fragment>
        <Profile {...this.props} />
        {Platform.OS === 'android' && <Fab />}
      </React.Fragment>
    );
  }
}

export const PROFILE_SCREEN = {
  component: {
    name: 'ProfileScreen',
  },
};

export const register = (registerComponent) =>
  registerComponent(PROFILE_SCREEN.component.name, () => ProfileScreen);
