import * as React from 'react';
import { Appbar } from 'react-native-paper';
import { SignButton } from 'src/components/sign-button';
import { ChatButton } from 'src/components/chat-button';
import { colors, fonts } from '@hedviginsurance/brand';
import { TranslationsConsumer } from 'src/components/translations/consumer';

interface AndroidHeaderProps {
  subtitle: string;
}

export const AndroidHeader: React.SFC<AndroidHeaderProps> = ({ subtitle }) => (
  <TranslationsConsumer textKey="OFFER_TITLE">
    {(title) => (
      <Appbar.Header
        dark
        theme={{
          colors: {
            primary: colors.BLACK_PURPLE,
          },
          fonts: {
            regular: fonts.CIRCULAR,
            medium: fonts.CIRCULAR,
            light: fonts.CIRCULAR,
            thin: fonts.CIRCULAR
          }
        }}
      >
        <Appbar.Content title={title} subtitle={subtitle} />
        <SignButton />
        <ChatButton />
      </Appbar.Header>
    )}
  </TranslationsConsumer>
);
