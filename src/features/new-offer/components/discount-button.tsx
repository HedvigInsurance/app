import * as React from 'react';
import { TouchableOpacity, Text } from 'react-native';
import styled from '@sampettersson/primitives';
import { colors, fonts } from '@hedviginsurance/brand';
import { Spacing } from 'src/components/Spacing';
import { MonetaryAmountV2 } from 'src/graphql/components';
import { TranslationsConsumer } from 'src/components/translations/consumer';

interface DiscountButtonProps {
  discount: MonetaryAmountV2;
  onPress: () => void;
}

const ButtonStyle = styled(TouchableOpacity)({
  borderRadius: 32,
  paddingTop: 8,
  paddingRight: 16,
  paddingBottom: 8,
  paddingLeft: 16,
  borderColor: '#FFFFFF32',
  borderWidth: 1,
});

const TextStyle = styled(Text)({
  color: colors.WHITE,
  fontFamily: fonts.CIRCULAR,
  fontSize: 14,
});

export const DiscountButton: React.SFC<DiscountButtonProps> = ({
  discount,
  onPress,
}) => (
  <>
    <ButtonStyle onPress={onPress}>
      <TranslationsConsumer
        textKey={
          Number(discount.amount) !== 0
            ? 'OFFER_REMOVE_DISCOUNT_BUTTON'
            : 'OFFER_ADD_DISCOUNT_BUTTON'
        }
      >
        {(text) => <TextStyle>{text}</TextStyle>}
      </TranslationsConsumer>
    </ButtonStyle>
    <Spacing height={16} />
  </>
);
