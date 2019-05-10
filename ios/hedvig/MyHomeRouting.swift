//
//  MyHomeRouting.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-02-18.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Foundation

struct MyHomeRouting {
    static func openChat(viewController: UIViewController) {
        let chatOverlay = DraggableOverlay(presentable: Chat())
        viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
    }
}
