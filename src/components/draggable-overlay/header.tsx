import * as React from 'react';
import styled from '@sampettersson/primitives';
import { View, Text, TouchableOpacity } from 'react-native';
import { colors, fonts } from '@hedviginsurance/brand';
import { Arrow } from 'src/components/icons/Arrow';
import { RestartOfferChat } from '../RestartOfferChat';

interface HeaderContainerProps {
  backgroundColor: string;
}

const HeaderContainer = styled(View)(
  ({ backgroundColor }: HeaderContainerProps) => ({
    height: 60,
    width: '100%',
    backgroundColor: backgroundColor,
    paddingLeft: 20,
    paddingRight: 20,
    alignItems: 'center',
    justifyContent: 'space-between',
    flexDirection: 'row',
    zIndex: 5,
  }),
);

interface TitleProps {
  color: string;
}

const Title = styled(Text)(({ color }: TitleProps) => ({
  fontFamily: fonts.CIRCULAR,
  fontSize: 20,
  color: color,
  fontWeight: '500',
}));

interface CloseButtonProps {
  color: string;
}

const CloseButton = styled(TouchableOpacity)(({ color }: CloseButtonProps) => ({
  width: 30,
  height: 30,
  backgroundColor: color,
  borderRadius: 15,
  opacity: 0.8,
  alignItems: 'center',
  justifyContent: 'center',
  paddingTop: 2,
}));

const InvisibleComponent = styled(View)({
  width: 12,
  height: 12,
});

interface HeaderProps {
  title: string;
  onCloseClick?: () => void;
  restartButton?: boolean;
  backgroundColor?: string;
  textColor?: string;
  buttonColor?: string;
}

export const Header: React.SFC<HeaderProps> = ({
  title,
  onCloseClick = () => {},
  restartButton,
  backgroundColor = colors.PURPLE,
  textColor = colors.WHITE,
  buttonColor = colors.WHITE,
}) => (
  <HeaderContainer backgroundColor={backgroundColor}>
    <CloseButton color={buttonColor} onPress={() => onCloseClick()}>
      <Arrow width={12} height={12} arrowFill={backgroundColor} rotate={0} />
    </CloseButton>
    <Title color={textColor}>{title}</Title>
    {restartButton ? (
      <RestartOfferChat onCloseClick={() => onCloseClick()} />
    ) : (
      <InvisibleComponent />
    )}
  </HeaderContainer>
);
