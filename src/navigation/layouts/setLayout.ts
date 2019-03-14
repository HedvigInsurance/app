import { Navigation } from 'react-native-navigation';
import { colors, fonts } from '@hedviginsurance/brand';

export const setLayout = ({
  root,
  modals = [],
  overlays = [],
}: {
  root: any;
  modals?: any[];
  overlays?: any[];
}) => {
  Navigation.setDefaultOptions({
    topBar: {
      title: {
        fontFamily: fonts.CIRCULAR,
        fontSize: 17,
      },
      subtitle: {
        fontFamily: fonts.CIRCULAR,
        fontSize: 13,
      },
      largeTitle: {
        fontFamily: fonts.SORAY,
        fontSize: 30,
      },
    },
    statusBar: {
      visible: true,
      drawBehind: false,
      style: 'light',
    },
    bottomTab: {
      iconColor: colors.DARK_GRAY,
      selectedIconColor: colors.PURPLE,
      textColor: colors.DARK_GRAY,
      selectedTextColor: colors.PURPLE,
      fontFamily: fonts.CIRCULAR,
      fontSize: 13,
    },
    layout: {
      backgroundColor: 'white',
    },
  });

  Navigation.setRoot({
    root,
  });

  if (modals) {
    modals.forEach((modal) => Navigation.showModal(modal));
  }

  if (overlays) {
    overlays.forEach((overlay) => Navigation.showOverlay(overlay));
  }
};
