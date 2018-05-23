// Actions used to test tracking
// View events here: https://app.segment.com/hedvig/sources/ios_test/debugger

export const installAttributed = {
  type: 'DEEP_LINK_OPENED',
  payload: {
    branchParams: {
      '~channel': 'Test',
      '~feature': 'Feature',
      '~tags': ['tag1'],
      '~campaign': 'Summer',
      '~stage': 'invite',
      '~creation_source': 'Web',
      '~referring_link': 'https://what.com',
      '~id': '123',
      '~keywords': ['keyword'],
      '+match_guaranteed': true,
      '+referrer': 'Facebook',
      '+phone_number': '+44720303733',
      '+is_first_session': true,
      '+clicked_branch_link': true,
      '+click_timestamp': '2018',
      '+url': 'https://something.com',
    },
  },
};

export const openDeepLink = {
  type: 'DEEP_LINK_OPENED',
  payload: {
    branchParams: {
      '~channel': 'Test',
      '~feature': 'Feature',
      '~tags': ['tag1'],
      '~campaign': 'Summer',
      '~stage': 'invite',
      '~creation_source': 'Web',
      '~referring_link': 'https://what.com',
      '~id': '123',
      '~keywords': ['keyword'],
      '+match_guaranteed': true,
      '+referrer': 'Facebook',
      '+phone_number': '+44720303733',
      '+is_first_session': false,
      '+clicked_branch_link': true,
      '+click_timestamp': '2018',
      '+url': 'https://something.com',
    },
  },
};

export const installParams = {
  type: 'DEEP_LINK_INSTALL_PARAMS',
  payload: {
    branchParams: {
      '~channel': 'Test',
      '~feature': 'Feature',
      '~tags': ['tag1'],
      '+phone_number': '+44720303733',
      '+url': 'https://something.com',
    },
  },
};

export const navigate = {
  type: 'Navigation/NAVIGATE',
  routeName: 'Name',
};

export const anonymousUser = {
  type: 'LOADED_USER',
  payload: {
    trackingId: null,
    age: 30,
    livingAreaSqm: 100,
    paymentStatus: 'PENDING',
    selectedCashback: 'SOS Barnbyar',
  },
};

// After bank id sign/auth
export const trackedUser = {
  type: 'LOADED_USER',
  payload: {
    trackingId: 123,
    age: 30,
    livingAreaSqm: 100,
    paymentStatus: 'PENDING',
    selectedCashback: 'SOS Barnbyar',
  },
};

export const paymentAdded = {
  type: 'TRACK_PAYMENT_ADDED',
  payload: {
    payment_method: 'Trustly',
  },
};

export const openOffer = {
  type: 'TRACK_OFFER_OPENED',
  payload: {
    revenue: 200,
    currency: 'SEK',
  },
};

export const closeOffer = {
  type: 'TRACK_OFFER_CLOSED',
};

export const viewOfferStep = {
  type: 'TRACK_OFFER_STEP_VIEWED',
  payload: { step: 1 },
};

export const completeOfferStep = {
  type: 'TRACK_OFFER_STEP_COMPLETED',
  payload: { step: 1 },
};

export const offerSigned = {
  type: 'TRACK_OFFER_SIGNED',
  payload: {
    revenue: 200,
    currency: 'SEK',
  },
};

export const logout = {
  type: 'DELETE_TRACKING_ID',
};
