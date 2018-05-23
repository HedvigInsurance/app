const EVENT = 'event';
const SCREEN = 'screen';
const RESET = 'reset';
const IDENTIFY = 'identify';

export const trackEvent = (eventDef) => (action, prevState, nextState) => {
  const event = eventDef(action, prevState, nextState);
  const { eventName, customProperties } = event;

  return {
    hitType: EVENT,
    eventName: eventName,
    customProperties,
  };
};

export const trackScreenView = (eventDef) => (action, prevState, nextState) => {
  const event = eventDef(action, prevState, nextState);
  const { screenName, customProperties } = event;

  return {
    hitType: SCREEN,
    screenName: screenName,
    customProperties,
  };
};

export const resetSession = () => () => {
  return {
    hitType: RESET,
  };
};

export const identify = (eventDef) => (action, prevState, nextState) => {
  const event = eventDef(action, prevState, nextState);
  const { userId, customTraits } = event;

  return {
    hitType: IDENTIFY,
    userId,
    customTraits,
  };
};

export const SegmentReduxTarget = (androidWriteKey, iosWriteKey, Segment) => {
  Segment.initialize({ androidWriteKey, iosWriteKey });

  return (events) => {
    events.forEach((event) => {
      switch (event.hitType) {
        case EVENT: {
          Segment.track(event.eventName, event.customProperties);
          break;
        }

        case SCREEN: {
          Segment.screen(event.screenName, event.customProperties);
          break;
        }

        case RESET: {
          Segment.reset();
          break;
        }

        case IDENTIFY: {
          Segment.identify(event.userId, event.customTraits);
          break;
        }

        default:
      }
    });
  };
};
