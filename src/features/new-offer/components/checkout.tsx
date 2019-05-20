import * as React from 'react';
import { connect } from 'react-redux';
import { OFFER_CHECKOUT } from 'src/features/offer/state/actions';
import { Dialog } from 'src/features/bankid/Dialog';
import { Mount, Update } from 'react-lifecycle-components';

import { TRACK_OFFER_OPENED } from 'src/features/analytics/actions';
import { AndroidOfferState } from './android-offer-state';
import { signButtonEvents } from 'src/features/new-offer/components/sign-button';

interface CheckoutProps {
  monthlyCost: number;
  orderId: number;
  checkout: () => void;
  trackOfferOpen: (monthlyCost: number, orderId: number) => void;
}

export const CheckoutComp: React.SFC<CheckoutProps> = ({
  checkout,
  orderId,
  trackOfferOpen,
  monthlyCost,
}) => (
  <>
    <Mount on={() => trackOfferOpen(monthlyCost, orderId)}>{null}</Mount>
    <AndroidOfferState>
      {({ isCheckingOut, setIsCheckingOut }) => (
        <>
          <Update<boolean>
            was={() => {
              if (isCheckingOut) {
                setIsCheckingOut(false);
                checkout();
              }
            }}
            watched={isCheckingOut}
          >
            {null}
          </Update>
          <Mount
            on={() => {
              signButtonEvents.on('checkout', () => {
                checkout();
              });
            }}
          >
            {null}
          </Mount>
        </>
      )}
    </AndroidOfferState>
    <Dialog />
  </>
);

const mapDispatchToProps = (dispatch: any) => {
  return {
    checkout: () => {
      dispatch({
        type: OFFER_CHECKOUT,
      });
    },
    trackOfferOpen: (pricePerMonth: number, orderId: number) =>
      dispatch({
        type: TRACK_OFFER_OPENED,
        payload: {
          revenue: pricePerMonth,
          currency: 'SEK',
          order_id: orderId,
        },
      }),
  };
};

export const Checkout = connect(
  ({ analytics }: { analytics: { orderId: number } }) => ({
    orderId: analytics.orderId,
  }),
  mapDispatchToProps,
)(CheckoutComp);
