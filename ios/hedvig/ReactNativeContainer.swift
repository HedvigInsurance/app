//
//  ReactNativeContainer.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-05-16.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Foundation

class ReactNativeContainer {
    static let shared = ReactNativeContainer()

    var bridge: RCTBridge

    init() {
        let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(
            forBundleRoot: "index",
            fallbackResource: nil
        )

        bridge = RCTBridge(
            bundleURL: jsCodeLocation,
            moduleProvider: nil,
            launchOptions: [:]
        )
    }
}
