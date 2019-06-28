import { DEBUG_SCREEN } from './';

export const getDebugLayout = () => ({
  root: {
    stack: {
      children: [DEBUG_SCREEN],
    },
  },
});

export const openDebugLayout = () => {
  setTimeout(() => {
    Navigation.setRoot(getDebugLayout());
  }, 100);
};
