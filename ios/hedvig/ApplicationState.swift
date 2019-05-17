//
//  ApplicationState.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-05-16.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Flow
import Foundation
import Presentation

struct ApplicationState {
    enum Screen: String {
        case onboardingChat, offer
    }

    private static let key = "applicationState"

    static func preserveState(_ screen: Screen) {
        UserDefaults.standard.set(screen.rawValue, forKey: key)
    }

    static func presentRootViewController(_ window: UIWindow) -> Disposable? {
        guard
            let applicationStateRawValue = UserDefaults.standard.value(forKey: key) as? String,
            let applicationState = Screen(rawValue: applicationStateRawValue)
        else { return nil }

        switch applicationState {
        case .onboardingChat:
            return window.present(OnboardingChat(intent: .onboard), options: [.defaults], animated: false)
        case .offer:
            return window.present(
                Offer(),
                options: [.defaults, .prefersNavigationBarHidden(true)],
                animated: false
            )
        }
    }
}
