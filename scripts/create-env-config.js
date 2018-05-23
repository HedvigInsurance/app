#!/usr/bin/env node
/* eslint-disable no-console */

const fs = require('fs');

const writeConfig = (config, filename) => {
  const json = JSON.stringify(config, null, 2);
  fs.writeFileSync(filename, json, 'utf8');
};

const DEVELOPMENT_ENV = 'DEVELOPMENT_ENV';
const TEST_ENV = 'TEST_ENV';
const PRODUCTION_ENV = 'PRODUCTION_ENV';

const ENVIRONMENT = 'ENVIRONMENT';
const UPLOAD_URL = 'UPLOAD_URL';
const SEGMENT_IOS_WRITE_KEY = 'SEGMENT_IOS_WRITE_KEY';
const SEGMENT_ANDROID_WRITE_KEY = 'SEGMENT_ANDROID_WRITE_KEY';
const API_BASE_URL = 'API_BASE_URL';
const SENTRY_DSN = 'SENTRY_DSN';

const SENTRY_DSN_ALL =
  'https://11b25670dab44c79bfd36ec805fda14a:6e2a4c2b8eb64711ae3e1d2e16e79583@sentry.io/271600';

const API_BASE_URL_TEST = 'https://gateway.test.hedvig.com';
const API_BASE_URL_PRODUCTION = 'https://gateway.hedvig.com';

const envConfigs = {
  [ENVIRONMENT]: {
    [DEVELOPMENT_ENV]: 'development',
    [TEST_ENV]: 'test',
    [PRODUCTION_ENV]: 'production',
  },
  [UPLOAD_URL]: {
    [DEVELOPMENT_ENV]: process.env.S3_UPLOAD_BUCKET_TEST,
    [TEST_ENV]: process.env.S3_UPLOAD_BUCKET_TEST,
    [PRODUCTION_ENV]: process.env.S3_UPLOAD_BUCKET_PRODUCTION,
  },
  [SEGMENT_IOS_WRITE_KEY]: {
    [DEVELOPMENT_ENV]: process.env.SEGMENT_IOS_WRITE_KEY_TEST,
    [TEST_ENV]: process.env.SEGMENT_IOS_WRITE_KEY_TEST,
    [PRODUCTION_ENV]: process.env.SEGMENT_IOS_WRITE_KEY_PRODUCTION,
  },
  [SEGMENT_ANDROID_WRITE_KEY]: {
    [DEVELOPMENT_ENV]: process.env.SEGMENT_ANDROID_WRITE_KEY_TEST,
    [TEST_ENV]: process.env.SEGMENT_ANDROID_WRITE_KEY_TEST,
    [PRODUCTION_ENV]: process.env.SEGMENT_ANDROID_WRITE_KEY_PRODUCTION,
  },
  [API_BASE_URL]: {
    [DEVELOPMENT_ENV]: API_BASE_URL_TEST,
    [TEST_ENV]: API_BASE_URL_TEST,
    [PRODUCTION_ENV]: API_BASE_URL_PRODUCTION,
  },
  [SENTRY_DSN]: {
    [DEVELOPMENT_ENV]: SENTRY_DSN_ALL,
    [TEST_ENV]: SENTRY_DSN_ALL,
    [PRODUCTION_ENV]: SENTRY_DSN_ALL,
  },
};

// Turns
// ENV_KEY: {
//   DEVELOPMENT_ENV: 'SECRET',
//   TEST_ENV: 'SECRET',
//   PRODUCTION_ENV: 'SECRET',
// },
// ...rest

// Into
// {
//   DEVELOPMENT_ENV: { ENV_KEY: 'SECRET', ...rest },
//   TEST_ENV: { ENV_KEY: 'SECRET', ...rest },
//   PRODUCTION_ENV: { ENV_KEY: 'SECRET', ...rest },
// }
const configMappedToEnv = Object.keys(envConfigs).reduce((config, key) => {
  const envConfig = envConfigs[key];
  console.log('Writing envConfig', key);

  for (let env of Object.keys(envConfig)) {
    const value = envConfig[env];
    if (!value) {
      throw new Error(
        `Please export env key: ${key}, value: ${value}, env: ${env}`,
      );
    }
    config[env] = { ...config[env], [key]: value };
  }

  return config;
}, {});

const devConfig = configMappedToEnv[DEVELOPMENT_ENV];
const testConfig = configMappedToEnv[TEST_ENV];
const prodConfig = configMappedToEnv[PRODUCTION_ENV];

writeConfig(devConfig, './hedvig-redux/env-config.development.json');
writeConfig(testConfig, './hedvig-redux/env-config.test.json');
writeConfig(prodConfig, './hedvig-redux/env-config.production.json');
