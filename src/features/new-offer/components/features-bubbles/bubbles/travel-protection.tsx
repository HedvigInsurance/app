import * as React from 'react';
import { colors } from '@hedviginsurance/brand';

import { Bubble } from '../bubble';
import { BubbleAnimation } from '../bubble-animation';
import { TranslationsConsumer } from 'src/components/translations/consumer';

import { Title } from './common/title';

export const TravelProtection = () => (
  <BubbleAnimation delay={100}>
    <Bubble width={125} height={125} backgroundColor={colors.TURQUOISE}>
      <TranslationsConsumer textKey="OFFER_BUBBLES_TRAVEL_PROTECTION_TITLE">
        {(text) => <Title>{text}</Title>}
      </TranslationsConsumer>
    </Bubble>
  </BubbleAnimation>
);
