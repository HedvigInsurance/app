import { LOADED_USER, DELETE_TRACKING_ID } from '../actions/types';
import * as R from 'ramda';

const reducer = (
  state = {
    currentUser: {
      address: null,
      age: null,
      email: null,
      familyMembers: [],
      firstName: null,
      lastName: null,
      livingAreaSqm: 0,
      maskedBankAccountNumber: '',
      name: '',
      nextPaymentDate: '',
      paymentStatus: '',
      safetyIncreasers: [],
      selectedCashback: '',
      selectedCashbackSignature: '',
      selectedCashbackParagraph: '',
    },
  },
  action,
) => {
  switch (action.type) {
    case LOADED_USER:
      if (!R.isNil(action.payload)) {
        return Object.assign({}, state, { currentUser: action.payload });
      } else {
        return state;
      }
    case DELETE_TRACKING_ID:
      return { currentUser: { ...state.currentUser, trackingId: null } };
    default:
      return state;
  }
};

export default reducer;
