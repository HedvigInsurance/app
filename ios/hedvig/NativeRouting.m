//
//  NativeRouting.m
//  hedvig
//
//  Created by Sam Pettersson on 2018-12-06.
//  Copyright © 2018 Hedvig AB. All rights reserved.
//

#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(NativeRouting, RCTEventEmitter)
    RCT_EXTERN_METHOD(appHasLoaded)
    RCT_EXTERN_METHOD(userDidSign)
    RCT_EXTERN_METHOD(openChat)
    RCT_EXTERN_METHOD(showOffer)
    RCT_EXTERN_METHOD(presentLoggedIn)
    RCT_EXTERN_METHOD(showPeril: (NSString)categoryTitle idString: (NSString)idString title: (NSString)title description: (NSString)description)
    RCT_EXTERN_METHOD(logEcommercePurchase)
    RCT_EXTERN_METHOD(showFileUploadOverlay:
                      (BOOL) value
                      resolver:(RCTPromiseResolveBlock)resolve
                      rejecter:(RCTPromiseRejectBlock)reject
    )
    RCT_EXTERN_METHOD(registerExternalComponentId: (NSString)componentId componentName: (NSString)componentName)
@end
