import { ChangeOverlay } from 'src/features/new-offer/components/features-bubbles/bubbles/start-date/change-overlay';

export const CHANGE_START_DATE_COMPONENT = {
  name: 'ChangeStartDate',
};

export const register = (
  registerComponent: (
    changeStartDateComponentName: string,
    getChangeStartDate: () => typeof ChangeOverlay,
  ) => void,
) => registerComponent(CHANGE_START_DATE_COMPONENT.name, () => ChangeOverlay);
