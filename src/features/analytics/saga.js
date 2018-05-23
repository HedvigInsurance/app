import { put, takeEvery, select } from 'redux-saga/effects';
import { types } from '../../../hedvig-redux';
import {
  DEEP_LINK_OPENED,
  DEEP_LINK_INSTALL_PARAMS,
} from '../deep-linking/actions';
import {
  TRACK_SET_IDENTITY,
  TRACK_INSTALL_ATTRIBUTED,
  TRACK_DEEP_LINK_OPENED,
} from './actions';

const setTrackingIdentity = function*() {
  const state = yield select();
  const {
    user: { currentUser },
    insurance,
  } = state;

  // GDPR 👮
  // When adding: Do we need to track this to provide our serivce?
  // https://ec.europa.eu/info/law/law-topic/data-protection/reform/what-personal-data_en
  const customTraits = {
    age: currentUser.age,
    insuranceStatus: insurance.status,
    insuredAtOtherCompany: insurance.insuredAtOtherCompany,
    currentInsurerName: insurance.currentInsurerName,
    insuranceType: insurance.insuranceType,
    currentTotalPrice: insurance.currentTotalPrice,
  };

  yield put({
    type: TRACK_SET_IDENTITY,
    payload: {
      userId: currentUser.trackingId,
      customTraits,
    },
  });
};

// Map Branch params to standard UTM parameters
// https://support.branch.io/support/solutions/articles/6000127549-utm-parameters-and-the-branch-dashboard
const buildCampaignParams = (params) => {
  // +phone_number is provided when sending yourself a link from desktop: https://hedvig.com/download
  const mapBranchKeys = {
    '~channel': 'utmSource',
    '~feature': 'utmMedium',
    '~tags': 'utmContent',
    '~campaign': 'utmCampaign',
    '~keywords': 'utmTerm',
    '+phone_number': 'phoneNumber',
  };

  return Object.keys(params).reduce((acc, key) => {
    const targetKey = mapBranchKeys[key];
    const value = params[key];
    if (targetKey && value) {
      acc[targetKey] = value;
    }
    return acc;
  }, {});
};

// Available Branch params
// https://github.com/BranchMetrics/react-native-branch-deep-linking#params-object
const trackDeepLinkOpened = function*({ payload }) {
  let { branchParams } = payload;
  branchParams = branchParams || {};

  // Track all uri opens (e.g. opening app from bank id with hedvig://)
  // Adding this to allow debugging installs which are only triggered in production
  // https://segment.com/docs/spec/mobile/#deep-link-opened
  yield put({
    type: TRACK_DEEP_LINK_OPENED,
    payload: {
      provider: 'Branch Metrics',
      url: branchParams['+url'],
      branchParams,
    },
  });

  // Only attribute installs that have a channel set (e.g. Facebook)
  // https://segment.com/docs/spec/mobile/#install-attributed
  if (branchParams['+is_first_session'] && branchParams['~channel']) {
    yield put({
      type: TRACK_INSTALL_ATTRIBUTED,
      payload: {
        provider: 'Branch Metrics',
        campaign: {
          source: branchParams['~channel'],
          name: branchParams['~campaign'],
          content: branchParams['~tags'],
          medium: branchParams['~feature'],
          term: branchParams['~keywords'],
        },
        url: branchParams['+url'],
      },
    });
  }
};

const trackDeepLinkInstall = function*({ payload }) {
  let { branchParams } = payload;
  branchParams = branchParams || {};

  const params = buildCampaignParams(branchParams);
  if (!Object.keys(params).length) return;

  yield put({
    type: types.API,
    payload: {
      SUCCESS: 'REGISTER_CAMPAIGN_SUCCESS',
      url: '/hedvig/register_campaign',
      method: 'POST',
      headers: {
        Accept: 'application/json; charset=utf-8',
        'Content-Type': 'application/json; charset=utf-8',
      },
      body: JSON.stringify(params, null, 2),
    },
  });
};

export const setTrackingIdentitySaga = function*() {
  yield takeEvery(
    [types.LOADED_USER, types.LOADED_INSURANCE],
    setTrackingIdentity,
  );
};

export const trackDeepLinkOpenedSaga = function*() {
  yield takeEvery(DEEP_LINK_OPENED, trackDeepLinkOpened);
};

export const trackDeepLinkInstallSaga = function*() {
  yield takeEvery(DEEP_LINK_INSTALL_PARAMS, trackDeepLinkInstall);
};
