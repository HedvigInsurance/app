import * as React from 'react';
import { DraggableOverlay } from 'src/components/draggable-overlay';
import {
  Dimensions,
  View,
  TouchableOpacity,
  Text,
  Platform,
} from 'react-native';
import styled from '@sampettersson/primitives';
import { colors, fonts } from '@hedviginsurance/brand';
import { Navigation } from 'react-native-navigation';
import { Header } from 'src/components/draggable-overlay/header';
import { Spacing } from 'src/components/Spacing';
import DatePicker from 'react-native-date-picker';
import { TranslationsConsumer } from 'src/components/translations/consumer';
import { Container, ActionMap } from 'constate';
import { Mutation } from 'react-apollo';
import gql from 'graphql-tag';

const REQUEST_START_DATE_MUTATION = gql`
  mutation requestStartDate($requestedStartDate: LocalDate) {
    requestStartDate(requestedStartDate: $requestedStartDate)
  }
`;

const OverlayContent = styled(View)({
  justifyContent: 'center',
});

const Row = styled(View)({
  alignItems: 'center',
});

const Heading = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.OFF_BLACK,
  fontSize: 16,
  lineHeight: 20,
  fontWeight: '500',
  textAlign: 'center',
  padding: 24,
});

const StartDatePicker = styled(DatePicker)({
  borderTopWidth: 1,
  borderTopColor: colors.LIGHT_GRAY,
  borderBottomWidth: 1,
  borderBottomColor: colors.LIGHT_GRAY,
});

const ConfirmButton = styled(TouchableOpacity)({
  paddingTop: 14,
  paddingRight: 32,
  paddingBottom: 14,
  paddingLeft: 32,
  backgroundColor: colors.PURPLE,
  borderRadius: 25,
  alignItems: 'center',
  justifyContent: 'center',
});

const ConfirmButtonText = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.WHITE,
  fontSize: 17,
  lineHeight: 22,
});

const ResetButton = styled(TouchableOpacity)({
  backgroundColor: colors.LIGHT_GRAY,
  borderRadius: 24,
  alignItems: 'center',
  justifyContent: 'center',
  paddingTop: 7,
  paddingRight: 12,
  paddingBottom: 7,
  paddingLeft: 12,
});

const ResetButtonText = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.OFF_BLACK_DARK,
  fontSize: 14,
  lineHeight: 20,
});

interface State {
  datePickerDate: Date;
}

interface Actions {
  setDatePickerDate: (date: Date) => void;
}

const actions: ActionMap<State, Actions> = {
  setDatePickerDate: (datePickerDate) => () => ({
    datePickerDate,
  }),
};

interface ChangeOverlayProps {
  componentId: string;
  insuredAtOtherCompany: boolean;
  date: Date;
  setDate: (date: Date | null) => void;
}

export const ChangeOverlay: React.SFC<ChangeOverlayProps> = ({
  componentId,
  insuredAtOtherCompany,
  date,
  setDate,
}) => (
  <Container
    actions={actions}
    initialState={{ datePickerDate: date != null ? date : new Date() }}
  >
    {({ datePickerDate, setDatePickerDate }) => (
      <DraggableOverlay
        heightPercentage={70}
        onClose={() => Navigation.dismissOverlay(componentId)}
      >
        {(handleClose) => (
          <>
            <TranslationsConsumer textKey="OFFER_BUBBLES_START_DATE_CHANGE_TITLE">
              {(text) => (
                <Header
                  title={text}
                  onCloseClick={handleClose}
                  restartButton={false}
                  backgroundColor={colors.OFF_WHITE}
                  textColor={colors.OFF_BLACK_DARK}
                  buttonColor={colors.DARK_GRAY}
                />
              )}
            </TranslationsConsumer>

            <OverlayContent>
              <TranslationsConsumer textKey="OFFER_BUBBLES_START_DATE_CHANGE_HEADING">
                {(text) => <Heading>{text}</Heading>}
              </TranslationsConsumer>
              <StartDatePicker
                date={datePickerDate}
                minimumDate={new Date()}
                locale="sv"
                mode="date"
                onDateChange={(date: Date) => {
                  setDatePickerDate(date);
                }}
              />
              <Spacing height={40} />
              <Mutation mutation={REQUEST_START_DATE_MUTATION}>
                {(mutate) => (
                  <>
                    <Row>
                      <TranslationsConsumer textKey="OFFER_BUBBLES_START_DATE_CHANGE_CONFIRM">
                        {(text) => (
                          <ConfirmButton
                            onPress={() => {
                              mutate({
                                variables: {
                                  requestedStartDate: datePickerDate.toISOString(),
                                },
                              });
                              setDate(datePickerDate);
                              handleClose();
                            }}
                          >
                            <ConfirmButtonText>{text}</ConfirmButtonText>
                          </ConfirmButton>
                        )}
                      </TranslationsConsumer>
                    </Row>
                    <Spacing height={22} />
                    <Row>
                      <TranslationsConsumer
                        textKey={
                          insuredAtOtherCompany
                            ? 'OFFER_BUBBLES_START_DATE_CHANGE_RESET_SWITCHER'
                            : 'OFFER_BUBBLES_START_DATE_CHANGE_RESET_NEW'
                        }
                      >
                        {(text) => (
                          <ResetButton
                            onPress={() => {
                              mutate({
                                variables: {
                                  requestedStartDate: null,
                                },
                              });
                              setDate(null);
                              handleClose();
                            }}
                          >
                            <ResetButtonText>{text}</ResetButtonText>
                          </ResetButton>
                        )}
                      </TranslationsConsumer>
                    </Row>
                  </>
                )}
              </Mutation>
            </OverlayContent>
          </>
        )}
      </DraggableOverlay>
    )}
  </Container>
);
