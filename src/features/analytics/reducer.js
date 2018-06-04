import { TRACK_SET_ORDER_ID } from './actions';

export default (
  state = {
    orderId: null,
  },
  action,
) => {
  switch (action.type) {
    case TRACK_SET_ORDER_ID:
      return Object.assign({}, state, {
        orderId: action.payload.orderId,
      });
    default:
      return state;
  }
};
