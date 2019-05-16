import { colors } from '@hedviginsurance/brand';

export const CLOSE_BUTTON = (color: string = colors.DARK_GRAY) => ({
  id: 'CLOSE_BUTTON',
  icon: require('assets/icons/navigation/topBar/ios/close.png'),
  color,
});
