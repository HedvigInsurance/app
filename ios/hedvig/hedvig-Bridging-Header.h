//
//  Use this file to import your target's public headers that you would like to expose to Swift.
//

#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>
#import <React/RCTAsyncLocalStorage.h>
#import <react-native-branch/RNBranch.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
#import <Lottie/Lottie.h>
#import "ReactNativeConfig.h"

@interface RCTAsyncLocalStorage (Private)
- (void)multiGet:(NSArray<NSString *> *)keys callback:(RCTResponseSenderBlock)callback;
- (void)multiSet:(NSArray<NSArray<NSString *> *> *)kvPairs callback:(RCTResponseSenderBlock)callback;
@end

@interface LocalStorage : RCTAsyncLocalStorage
@end
