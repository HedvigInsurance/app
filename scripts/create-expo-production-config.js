#!/usr/bin/env node
/* eslint-disable no-console */

const fs = require('fs');
const appConfig = require('../app.json');

const { BRANCH_API_KEY_LIVE } = process.env;

if (!BRANCH_API_KEY_LIVE || BRANCH_API_KEY_LIVE.indexOf('key_live_') === -1) {
  console.log('Please export the LIVE Branch Key as $BRANCH_API_KEY_LIVE');
  console.log('Find it here: https://dashboard.branch.io/account-settings/app');
  process.exit();
}

const appId = 'com.hedvig.app';
const name = 'Hedvig';
const scheme = 'hedvig';

appConfig.expo.name = name;
appConfig.expo.scheme = scheme;
appConfig.expo.ios.bundleIdentifier = appId;
appConfig.expo.android.package = appId;
appConfig.expo.ios.config.branch.apiKey = BRANCH_API_KEY_LIVE;
appConfig.expo.android.config.branch.apiKey = BRANCH_API_KEY_LIVE;

const json = JSON.stringify(appConfig, null, 2);
fs.writeFileSync('app.production.json', json, 'utf8');
