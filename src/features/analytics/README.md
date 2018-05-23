# Anlytics setup

We use the Segment SDK for all analytics tracking and Branch SDK for deep linking and ad attribution.

All events are configured and sent to Segment from `App.js` and triggered by Redux actions.

[Expo Segment SDK](https://github.com/expo/expo/blob/master/ios/Exponent/Versioned/Core/Api/EXSegment.m) calls are wrapped and also call Branch SDK where possible in `SegmentTracker.js`.

We're using Segment's [Semantic Events](https://segment.com/docs/spec/semantic/) which get mapped to Branch Standard Events where possible.

For example:

* `SegmentTracker.track('Order Completed', { revenue: 100, currency: 'SEK', tier: 'all-inclusive' })`
  * Transformed and sent to Branch: `Branch.userCompletedAction('PURCHASE', { revenue: 100, currency: 'SEK', customData: { tier: 'all-inclusive' } })`

## Why this madness?

We can't use the official Segment <> Branch destination as we don't have native access and would have to eject from Expo.

When configuring bundled destinations in the Segment dashboard (e.g. Branch), it only provides the native Branch-Segment-iOS/Android SDK with the Branch key. Setting up this destination without installing the SDK does nothing!

To get around the fact that we can't use the native SDK until ejecting we have wrapped Segment and transformating and forwarding events to Branch.

Our custom Segment > Brach link is set up in `SegmentTracker.js` (following what is done in [Segment-Branch-iOS](https://github.com/BranchMetrics/Segment-Branch-iOS/blob/master/Pod/Classes/BNCBranchIntegration.m)

## Ad attribution

1.  Branch ad link is created in the Branch dashboard (https://dashboard.branch.io/ads/links/ad-links)
2.  Link is used in ad on Twitter, AdWords, Facebook
3.  When clicking the link, the user is first redirected to Branch servers where the device is fingerprinted/identified before opening the app (if present), or opening the app store
4.  After install the Branch SDK recognises the device through the previous fingerprinting step and fetches the associated campaign parameters and exposes these through the SDK (e.g. Branch.subscribe or Branch.getFirstReferringParams())
5.  Any subsequent events are tracked against this user
6.  When tracking the special Branch PURCHASE event (on offer sign) with revenue this is reported back to AdWords and Facebook where we can see what ads are actually turning into paying customers

## Tracking users

1.  Segment assigns a random anonymous user id when first opening/installing the app
2.  Any subsequent events or `.identify` calls without a `userId` are recoreded against this anonymous id which is persisted until uninstall
3.  A randomly generated `trackingId` is generated and stored against personnummer when authing or signing with Bank ID
4.  `Segment.identify(currentUser.trackingId)` and `Branch.setIdentity(currentUser.trackingId)` are called
5.  Previously anonymous events and traits are merged with new identified user

## Website to ad tracking

* The website uses the [Branch Web SDK](https://github.com/BranchMetrics/web-branch-deep-linking) to
  generate deep links to the app dynamically on each visit
* When dynamically creating the link, any utm tags are automaitally set on the link (e.g. utm_campaign set
  from an ad)
  * Configured under Advanced Settings: https://dashboard.branch.io/link-settings

## Creating ad links

1.  Create ad links in Branch: https://dashboard.branch.io/ads/links/ad-links
2.  Use link in ad

### Guides for specific networks

* Twitter: https://docs.branch.io/pages/deep-linked-ads/twitter/
* AdWords: https://docs.branch.io/pages/deep-linked-ads/google-ads-overview/
* Facebok: https://docs.branch.io/pages/deep-linked-ads/facebook-ads-overview/

### Ad network setup

* https://dashboard.branch.io/ads/partner-management

## Debugging in test and development

All analytics events (and triggering action) are logged in console.

### View test events:

* Branch
  * Events dashboard: https://dashboard.branch.io/liveview/events (Enable TEST in top left)
* Segment (iOS)
  * Debugger: https://app.segment.com/hedvig/sources/ios_test/debugger
* Segment (iOS)
  * Debugger: https://app.segment.com/hedvig/sources/android_test/debugger
