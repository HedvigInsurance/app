import Expo from 'expo';
import developmentConfig from './env-config.development.json';
import testConfig from './env-config.test.json';
import productionConfig from './env-config.production.json';

const DEVELOPMENT = 'development';
const TEST = 'test';
const PRODUCTION = 'production';

// releaseChannel is undefined in development
const { releaseChannel = DEVELOPMENT } = Expo.Constants.manifest;

export const envConfig = {
  [DEVELOPMENT]: { ...developmentConfig },
  [TEST]: { ...testConfig },
  [PRODUCTION]: { ...productionConfig },
}[releaseChannel];

if (!envConfig) {
  throw new Error(
    `Unkown Expo releaseChannel! Must be one of: [${TEST}, ${PRODUCTION}]`,
  );
}
