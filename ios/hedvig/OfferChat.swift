//
//  OfferChat.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-05-16.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Apollo
import Flow
import Form
import Presentation
import UIKit

struct OfferChat {
    let client: ApolloClient

    init(client: ApolloClient = ApolloContainer.shared.client) {
        self.client = client
    }
}

extension OfferChat: Presentable {
    func materialize() -> (UIViewController, Future<Void>) {
        let bag = DisposeBag()

        let viewController = UIViewController()

        viewController.preferredContentSize = CGSize(
            width: 0,
            height: UIScreen.main.bounds.height - 80
        )

        let closeButton = UIBarButtonItem()
        closeButton.image = Asset.close.image
        closeButton.tintColor = .darkGray

        viewController.navigationItem.leftBarButtonItem = closeButton

        let restartButton = UIBarButtonItem()
        restartButton.image = Asset.restart.image
        restartButton.tintColor = .darkGray

        func restartChat() {
            RCTAsyncLocalStorage().clearAllData()
            ApolloContainer.shared.deleteToken()

            bag += Signal(after: 0.2).onValue { _ in
                bag += RCTApolloClient.getClient().onValue { _ in
                    ReactNativeContainer.shared.bridge.reload()
                    ApplicationState.preserveState(.onboardingChat)
                    let appDelegate = UIApplication.shared.appDelegate
                    appDelegate.bag.dispose()
                    appDelegate.bag += UIApplication.shared.appDelegate.rootWindow.present(
                        OnboardingChat(intent: .onboard),
                        options: [.defaults],
                        animated: true
                    )
                }
            }
        }

        bag += restartButton.onValueDisposePrevious { _ in
            let innerBag = bag.innerBag()

            let okAction = Alert.Action(title: String(key: .RESTART_OFFER_CHAT_BUTTON_CONFIRM)) { () -> Void in
                restartChat()
            }

            let cancelAction = Alert.Action(title: String(key: .RESTART_OFFER_CHAT_BUTTON_DISMISS), style: .cancel) {}

            let alert = Alert(
                title: String(key: .RESTART_OFFER_CHAT_TITLE),
                message: String(key: .RESTART_OFFER_CHAT_PARAGRAPH),
                actions: [okAction, cancelAction]
            )

            innerBag += viewController.present(alert)
            return innerBag
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

        bag += client.perform(mutation: OfferClosedMutation()).onValue { _ in
            let reactView = RCTRootView(
                bridge: ReactNativeContainer.shared.bridge,
                moduleName: "ChatScreen",
                initialProperties: [:]
            )

            if let reactView = reactView {
                view.addSubview(reactView)
                reactView.snp.makeConstraints { make in
                    make.width.equalToSuperview()
                    make.height.equalToSuperview()
                    make.center.equalToSuperview()
                }
            }
        }

        Chat.didOpen()

        bag += Disposer {
            Chat.didClose()
        }

        viewController.view = view

        return (viewController, Future { completion in
            bag += closeButton.onValue { _ in
                completion(.success)
            }

            return bag
        })
    }
}
