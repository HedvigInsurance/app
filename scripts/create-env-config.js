#!/usr/bin/env node
/* eslint-disable no-console */

const fs = require('fs');

const { S3_UPLOAD_BUCKET_TEST, S3_UPLOAD_BUCKET_PRODUCTION } = process.env;

const API_BASE_URL_TEST = 'https://gateway.test.hedvig.com';
const API_BASE_URL_PRODUCTION = 'https://gateway.hedvig.com';

const isValidBucket = (url) => url && url.indexOf('https://') > -1;

const writeConfig = (UPLOAD_URL, API_BASE_URL, filename) => {
  const config = {
    UPLOAD_URL,
    API_BASE_URL,
  };
  const json = JSON.stringify(config, null, 2);
  fs.writeFileSync(filename, json, 'utf8');
};

if (isValidBucket(S3_UPLOAD_BUCKET_PRODUCTION)) {
  writeConfig(
    S3_UPLOAD_BUCKET_PRODUCTION,
    API_BASE_URL_PRODUCTION,
    './hedvig-redux/env-config.production.json',
  );
} else {
  console.log('Please export $S3_UPLOAD_BUCKET_PRODUCTION');
  process.exit();
}

if (isValidBucket(S3_UPLOAD_BUCKET_TEST)) {
  writeConfig(
    S3_UPLOAD_BUCKET_TEST,
    API_BASE_URL_TEST,
    './hedvig-redux/env-config.test.json',
  );
  writeConfig(
    S3_UPLOAD_BUCKET_TEST,
    API_BASE_URL_TEST,
    './hedvig-redux/env-config.development.json',
  );
} else {
  console.log('Please export $S3_UPLOAD_BUCKET_TEST');
  process.exit();
}
