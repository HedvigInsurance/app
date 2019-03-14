import * as React from 'react';
import { colors, fonts } from '@hedviginsurance/brand';
import { TouchableOpacity } from 'react-native';
import styled from '@sampettersson/primitives';
import color from 'color';
import { Text } from 'react-native';

import { Bubble } from '../../bubble';
import { BubbleAnimation } from '../../bubble-animation';
import { Spacing } from 'src/components/Spacing';
import { TranslationsConsumer } from 'src/components/translations/consumer';

import { Title } from '../common/title';

import { Switcher } from './switcher';
import { New } from './new';

import { Navigation } from 'react-native-navigation';
import { CHANGE_START_DATE_COMPONENT } from 'src/navigation/components/change-start-date';

import { Container, ActionMap } from 'constate';
import { Subtitle } from '../common/subtitle';

const ChangeButton = styled(TouchableOpacity)({
  paddingTop: 4,
  paddingRight: 8,
  paddingBottom: 4,
  paddingLeft: 8,
  backgroundColor: color(colors.WHITE).alpha(0.8),
  borderRadius: 13,
  alignItems: 'center',
  justifyContent: 'center',
});

const ChangeButtonText = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.OFF_BLACK_DARK,
  fontSize: 14,
  lineHeight: 18,
});

interface State {
  date: Date | null;
}

interface Actions {
  setDate: (date: Date | null) => void;
}

const actions: ActionMap<State, Actions> = {
  setDate: (date) => () => ({
    date,
  }),
};

interface StartDateProps {
  insuredAtOtherCompany: boolean;
  requestedStartDate: string;
}

export const StartDate: React.SFC<StartDateProps> = ({
  insuredAtOtherCompany,
  requestedStartDate,
}) => (
  <Container actions={actions} initialState={{ date: null }}>
    {({ date, setDate }) => (
      <BubbleAnimation delay={0}>
        <Bubble width={160} height={160} backgroundColor={colors.PURPLE}>
          <Spacing height={12.5} />
          <TranslationsConsumer textKey="OFFER_BUBBLES_START_DATE_TITLE">
            {(text) => <Title>{text}</Title>}
          </TranslationsConsumer>
          <Spacing height={2.5} />
          {requestedStartDate !== null ? (
            <Subtitle>
              {new Date(requestedStartDate).toLocaleDateString('sv-SE')}
            </Subtitle>
          ) : date != null ? (
            <Subtitle>{date.toLocaleDateString('sv-SE')}</Subtitle>
          ) : insuredAtOtherCompany ? (
            <Switcher />
          ) : (
            <New />
          )}
          <Spacing height={20} />
          <TranslationsConsumer textKey="OFFER_BUBBLES_START_DATE_CHANGE_BUTTON">
            {(text) => (
              <ChangeButton
                onPress={() => {
                  Navigation.showOverlay({
                    component: {
                      name: CHANGE_START_DATE_COMPONENT.name,
                      options: {
                        layout: {
                          backgroundColor: 'transparent',
                        },
                      },
                      passProps: {
                        insuredAtOtherCompany,
                        date,
                        setDate,
                      },
                    },
                  });
                }}
              >
                <ChangeButtonText>{text}</ChangeButtonText>
              </ChangeButton>
            )}
          </TranslationsConsumer>
        </Bubble>
      </BubbleAnimation>
    )}
  </Container>
);
