const OFF = 'off';
const ERROR = 'error';
const WARN = 'warn';

module.exports = {
  parser: 'babel-eslint',
  env: {
    browser: true,
    jest: true,
    node: true,
  },
  plugins: ['react'],
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:prettier/recommended',
  ],
  parserOptions: {
    ecmaVersion: 2016,
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  rules: {
    'no-undef': ERROR,
    'no-unused-vars': WARN,
    'react/prop-types': OFF,
    'react/display-name': OFF,
    'no-debugger': OFF,
  },
  globals: {
    __DEV__: false,
    Promise: false,
  },
};
