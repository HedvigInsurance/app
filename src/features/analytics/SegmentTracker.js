import { Segment, DangerZone } from 'expo';
const { Branch } = DangerZone;

// Segment Semantic Events (does not cover all events!)
// https://segment.com/docs/spec/semantic/
export const SemanticEvents = {
  Ecommerce: {
    CheckoutStarted: 'Checkout Started',
    CheckoutStepViewed: 'Checkout Step Viewed',
    CheckoutStepCompleted: 'Checkout Step Completed',
    PaymentInfoEntered: 'Payment Info Entered',
    OrderCompleted: 'Order Completed',
  },
  Mobile: {
    ApplicationCrashed: 'Application Crashed',
    InstallAttributed: 'Install Attributed',
    DeepLinkOpened: 'Deep Link Opened',
  },
};

// Checkout Started
// {
//   checkout_id: 123,
//   order_id: 123,
//   revenue: 25,
//   value: 25,
//   currency: 'SEK',
// }

// Checkout Step Viewed
// {
//   checkout_id: 123,
//   order_id: 123,
//   step: Number
// }

// Checkout Step Completed
// {
//   checkout_id: 123,
//   order_id: 123,
//   step: Number
// }

// Order Completed
// {
//   checkout_id: 123,
//   order_id: 123,
//   step: Number
// }

// Payment Info Entered
// {
//   checkout_id: 123,
//   order_id: 123,
//   payment_method: 'Trustly'
// }

// Deep Link Opened
// {
//   provider: 'Branch Metrics',
//   url: 'app://landing'
// }

// Install Attributed
// {
//   provider: 'Branch Metrics',
//   campaign: {
//     source: 'Network/FB/AdWords/MoPub/Source',
//     name: 'Campaign Name',
//     content: 'Organic Content Title',
//     ad_creative: 'Red Hello World Ad',
//     ad_group: 'Red Ones'
//   }
// }

// Map Segment Semantic events to Branch Events (does not cover all events!)
// https://docs.branch.io/pages/exports/event_ontology_data_schema/#events-included

// https://github.com/expo/expo/blob/master/ios/Exponent/Versioned/Core/Api/EXSegment.m
// https://github.com/BranchMetrics/Segment-Branch-iOS/blob/master/Pod/Classes/BNCBranchIntegration.m
const branchEventFromSegmentEvent = (event, properties) => {
  const mapSegmentToBranchEvent = {
    [SemanticEvents.Ecommerce.CheckoutStarted]: 'INITIATE_PURCHASE',
    [SemanticEvents.Ecommerce.PaymentInfoEntered]: 'ADD_PAYMENT_INFO',
    [SemanticEvents.Ecommerce.OrderCompleted]: 'PURCHASE',
  };

  // Top level event attributes in Branch, rest in `customData`
  // https://github.com/BranchMetrics/react-native-branch-deep-linking#tracking-user-actions-and-events
  const mapSegmentToBranchKeys = {
    order_id: 'transactionID',
    affiliation: 'affiliation',
    revenue: 'revenue',
    shipping: 'shipping',
    tax: 'tax',
    coupon: 'coupon',
    currency: 'currency',
    query: 'searchQuery',
  };

  const mappedEvent = mapSegmentToBranchEvent[event];

  const branchProperties =
    properties &&
    Object.keys(properties).reduce((acc, segmentKey) => {
      const branchKey = mapSegmentToBranchKeys[segmentKey];
      const value = properties[segmentKey];
      if (branchKey) {
        acc[branchKey] = value;
      } else {
        acc.customData = { ...acc.customData, [segmentKey]: value };
      }
      return acc;
    }, mappedEvent ? { description: event } : {});

  const branchEventName = mappedEvent || event;

  return [branchEventName, branchProperties];
};

export const SegmentTracker = {
  initialize: (options) => Segment.initialize(options),
  identify: (userId, traits) => {
    // userId is optional in Segment
    if (userId) {
      Branch.setIdentity(userId);
    }
    if (traits) {
      return Segment.identifyWithTraits(userId, traits);
    }
    return Segment.identify(userId);
  },
  screen: (name, properties) => {
    if (properties) {
      return Segment.screenWithProperties(name, properties);
    }
    return Segment.screen(name);
  },
  track: (event, properties) => {
    const [branchEventName, branchProperties] = branchEventFromSegmentEvent(
      event,
      properties,
    );
    Branch.userCompletedAction(branchEventName, branchProperties);
    if (properties) {
      return Segment.trackWithProperties(event, properties);
    }
    return Segment.track(event);
  },
  group: (groupId, traits) => {
    if (traits) {
      return Segment.groupWithTraits(groupId, traits);
    }
    return Segment.group(groupId);
  },
  flush: () => Segment.flush(),
  reset: () => {
    Branch.logout();
    return Segment.reset();
  },
};
