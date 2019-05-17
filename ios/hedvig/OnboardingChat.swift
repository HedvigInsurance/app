//
//  Chat.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-02-18.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Apollo
import Flow
import Form
import Presentation
import UIKit

struct Chat {}

struct OnboardingChat {
    enum Intent: String {
        case onboard, login
    }

    let client: ApolloClient
    let intent: Intent

    init(intent: Intent, client: ApolloClient = ApolloContainer.shared.client) {
        self.intent = intent
        self.client = client
    }
}

extension OnboardingChat: Presentable {
    func materialize() -> (UIViewController, Disposable) {
        let bag = DisposeBag()

        ApplicationState.preserveState(.onboardingChat)

        let viewController = UIViewController()
        viewController.navigationItem.hidesBackButton = true

        viewController.preferredContentSize = CGSize(
            width: 0,
            height: UIScreen.main.bounds.height - 80
        )

        let restartButton = UIBarButtonItem()
        restartButton.image = Asset.restart.image
        restartButton.tintColor = .darkGray

        bag += restartButton.onValue { _ in
            let nativeRouting = ReactNativeContainer.shared.bridge.module(for: NativeRouting.self) as! NativeRouting
            nativeRouting.sendRestartChat()
        }

        viewController.navigationItem.rightBarButtonItem = restartButton

        let titleHedvigLogo = UIImageView()
        titleHedvigLogo.image = Asset.wordmark.image
        titleHedvigLogo.contentMode = .scaleAspectFit

        viewController.navigationItem.titleView = titleHedvigLogo

        titleHedvigLogo.snp.makeConstraints { make in
            make.width.equalTo(80)
        }

        let view = UIView()
        view.backgroundColor = .offWhite

        let reactView = RCTRootView(
            bridge: ReactNativeContainer.shared.bridge,
            moduleName: "ChatScreen",
            initialProperties: ["intent": intent.rawValue]
        )

        if let reactView = reactView {
            view.addSubview(reactView)
            reactView.snp.makeConstraints { make in
                make.width.equalToSuperview()
                make.height.equalToSuperview()
                make.center.equalToSuperview()
            }
        }

        Chat.didOpen()

        bag += Disposer {
            Chat.didClose()
        }

        viewController.view = view

        return (viewController, bag)
    }
}
