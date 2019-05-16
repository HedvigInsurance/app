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

struct ClaimChat {
    let claimTypeId: GraphQLID?
    let client: ApolloClient

    init(claimTypeId: GraphQLID? = nil, client: ApolloClient = ApolloContainer.shared.client) {
        self.claimTypeId = claimTypeId
        self.client = client
    }
}

extension ClaimChat: Presentable {
    func materialize() -> (UIViewController, Future<Void>) {
        let bag = DisposeBag()

        let viewController = UIViewController()
        viewController.title = String(key: .CLAIMS_CHAT_TITLE)
        viewController.preferredContentSize = CGSize(width: 0, height: UIScreen.main.bounds.height - 80)

        let closeButton = UIBarButtonItem()
        closeButton.image = Asset.close.image
        closeButton.tintColor = .darkGray

        viewController.navigationItem.leftBarButtonItem = closeButton

        let view = UIView()
        view.backgroundColor = .offWhite

        let loaderBag = bag.innerBag()

        let loadingIndicator = LoadingIndicator(showAfter: 0)
        loaderBag += view.add(loadingIndicator)

        bag += client.perform(mutation: TriggerClaimChatMutation(claimTypeId: claimTypeId)).valueSignal.compactMap { $0.data?.triggerClaimChat }.onValue { _ in
            let jsCodeLocation = RCTBundleURLProvider.sharedSettings().jsBundleURL(forBundleRoot: "index", fallbackResource: nil)
            let reactView = RCTRootView(bundleURL: jsCodeLocation, moduleName: "ChatScreen", initialProperties: nil)

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

        viewController.view = view

        Chat.didOpen()

        bag += Disposer {
            Chat.didClose()
        }

        return (viewController, Future { completion in
            bag += closeButton.onValue { _ in
                completion(.success)
            }

            return DelayedDisposer(bag, delay: 1)
        })
    }
}
