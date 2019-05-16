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
    RCT_EXTERN_METHOD(registerExternalComponentId: (NSString)componentId componentName: (NSString)componentName)
@end
