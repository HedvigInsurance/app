//
//  FreeTextChat.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-05-15.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Apollo
import Flow
import Form
import Foundation
import Presentation
import UIKit

struct FreeTextChat {
    let client: ApolloClient

    init(client: ApolloClient = ApolloContainer.shared.client) {
        self.client = client
    }
}

extension FreeTextChat: Presentable {
    func materialize() -> (UIViewController, Future<Void>) {
        let bag = DisposeBag()

        let viewController = UIViewController()
        viewController.preferredContentSize = CGSize(width: 0, height: UIScreen.main.bounds.height - 80)

        let closeButton = UIBarButtonItem()
        closeButton.image = Asset.close.image
        closeButton.tintColor = .darkGray

        viewController.navigationItem.leftBarButtonItem = closeButton

        let titleHedvigLogo = UIImageView()
        titleHedvigLogo.image = Asset.wordmark.image
        titleHedvigLogo.contentMode = .scaleAspectFit

        viewController.navigationItem.titleView = titleHedvigLogo

        titleHedvigLogo.snp.makeConstraints { make in
            make.width.equalTo(80)
        }

        let view = UIView()
        view.backgroundColor = .offWhite

        let loaderBag = bag.innerBag()

        let loadingIndicator = LoadingIndicator(showAfter: 0)
        loaderBag += view.add(loadingIndicator)

        bag += client.perform(mutation: TriggerFreeTextChatMutation()).valueSignal.compactMap { $0.data?.triggerFreeTextChat }.onValue { _ in
            let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(
                forBundleRoot: "index",
                fallbackResource: nil
            )
            let reactView = RCTRootView(
                bundleURL: jsCodeLocation,
                moduleName: "ChatScreen",
                initialProperties: nil
            )

            if let reactView = reactView {
                view.addSubview(reactView)
                reactView.snp.makeConstraints { make in
                    make.width.equalToSuperview()
                    make.height.equalToSuperview()
                    make.center.equalToSuperview()
                }
            }

            loaderBag.dispose()
        }

        Chat.didOpen()

        bag += Disposer {
            Chat.didClose()
        }

        viewController.view = view

        return (viewController, Future { completion in
            bag += closeButton.onValue {
                completion(.success)
            }

            return bag
        })
    }
}
