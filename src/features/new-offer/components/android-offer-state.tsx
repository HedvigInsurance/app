import React from 'react';
import { ActionMap, Container } from 'constate';
import { Platform } from 'react-primitives';
import { Mount } from 'react-lifecycle-components';

interface AndroidOfferState {
  topSignButtonVisible: boolean;
  isCheckingOut: boolean;
}

interface AndroidOfferStateActions {
  setTopSignButtonVisibility: (visibility: boolean) => void;
  setIsCheckingOut: (isCheckingOut: boolean) => void;
}

const actions: ActionMap<AndroidOfferState, AndroidOfferStateActions> = {
  setTopSignButtonVisibility: (topSignButtonVisible) => () => ({
    topSignButtonVisible,
  }),
  setIsCheckingOut: (isCheckingOut) => () => ({
    isCheckingOut,
  }),
};

export const AndroidOfferState: React.SFC<{
  children: (
    props: AndroidOfferState & AndroidOfferStateActions,
  ) => React.ReactNode;
}> = ({ children }) => (
  <Container actions={actions} context="android-offer">
    {children}
  </Container>
);
