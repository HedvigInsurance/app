import * as React from 'react';
import { DraggableOverlay } from 'src/components/draggable-overlay';
import { TranslationsConsumer } from 'src/components/translations/consumer';
import { Header } from 'src/components/draggable-overlay/header';
import OfferChat from 'src/features/chat/OfferChat';
import { BackButton } from 'src/components/BackButton';
import { View } from 'react-native';
import { colors } from '@hedviginsurance/brand';
import { Platform } from 'react-native';
import styled from '@sampettersson/primitives';

const NonDraggableOverlay = styled(View)({
  marginTop: 60,
  backgroundColor: colors.WHITE,
  flex: 1,
  overflow: 'hidden',
});

export const OfferChatOverlay: React.SFC<{ componentId: string }> = ({
  componentId,
}) => {
  const handleClose = () => Navigation.dismissOverlay(componentId);
  const inner = (close: () => void) => (
    <>
      <TranslationsConsumer textKey="OFFER_CHAT_HEADER">
        {(text) => (
          <Header title={text} onCloseClick={close} restartButton={true} />
        )}
      </TranslationsConsumer>
      <OfferChat onRequestClose={close} />
    </>
  );
  if (Platform.OS === 'ios') {
    return (
      <DraggableOverlay heightPercentage={90} onClose={handleClose}>
        {(handleClose) => inner(handleClose)}
      </DraggableOverlay>
    );
  }
  return (
    <NonDraggableOverlay>
      <BackButton onPress={() => handleClose()} />
      {inner(handleClose)}
    </NonDraggableOverlay>
  );
};
