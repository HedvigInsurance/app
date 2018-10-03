import * as React from 'react';
import { colors } from '@hedviginsurance/brand';

import { Bubble } from '../bubble';
import { BubbleAnimation } from '../bubble-animation';
import { TranslationsConsumer } from 'src/components/translations/consumer';

import { Title } from './common/title';

export const OwnedAddon = () => (
  <BubbleAnimation delay={100}>
    <Bubble width={135} height={135} backgroundColor={colors.PURPLE}>
      <TranslationsConsumer textKey="OFFER_BUBBLES_OWNED_ADDON_TITLE">
        {(text) => <Title>{text}</Title>}
      </TranslationsConsumer>
    </Bubble>
  </BubbleAnimation>
);
