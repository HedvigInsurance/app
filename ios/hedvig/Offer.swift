//
//  Offer.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-05-16.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Apollo
import Flow
import Foundation
import Presentation

struct Offer {
    let client: ApolloClient

    init(client: ApolloClient = ApolloContainer.shared.client) {
        self.client = client
    }
}

extension Offer {
    func addNavigationBar(
        _ view: UIView,
        _ viewController: UIViewController
    ) -> (Disposable, UINavigationBar) {
        let bag = DisposeBag()

        let navigationBar = UINavigationBar()
        navigationBar.barTintColor = .darkPurple
        navigationBar.isTranslucent = false

        let item = UINavigationItem()

        let chatButton = UIBarButtonItem()
        chatButton.image = Asset.chat.image
        chatButton.tintColor = .white

        bag += chatButton.onValue { _ in
            let chatOverlay = DraggableOverlay(presentable: OfferChat())
            bag += viewController.present(chatOverlay).onValue { _ in }
        }

        item.leftBarButtonItem = chatButton

        let signButtonView = RCTRootView(
            bridge: ReactNativeContainer.shared.bridge,
            moduleName: "OfferSignButton",
            initialProperties: [:]
        )
        signButtonView?.backgroundColor = .clear

        signButtonView!.snp.makeConstraints { make in
            make.width.equalTo(80)
            make.height.equalTo(30)
        }

        let signButton = UIBarButtonItem(customView: signButtonView!)
        item.rightBarButtonItem = signButton

        let titleViewContainer = UIStackView()
        titleViewContainer.isLayoutMarginsRelativeArrangement = true
        titleViewContainer.edgeInsets = UIEdgeInsets(horizontalInset: 0, verticalInset: 5)

        let titleView = UIStackView()
        titleView.axis = .vertical
        titleView.spacing = 0
        titleView.alignment = .center
        titleView.distribution = .fillProportionally

        titleView.addArrangedSubview(UILabel(value: "Offer", style: .bodyWhite))

        let addressLabel = UILabel(value: "", style: .navigationSubtitleWhite)
        titleView.addArrangedSubview(addressLabel)

        titleViewContainer.addArrangedSubview(titleView)

        bag += titleViewContainer.didMoveToWindowSignal.take(first: 1).onValue { _ in
            titleViewContainer.snp.makeConstraints { make in
                make.top.bottom.equalToSuperview()
            }
        }

        bag += client.fetch(query: OfferQuery()).valueSignal.compactMap { $0.data?.insurance }.onValue { insurance in
            addressLabel.text = insurance.address
        }

        item.titleView = titleViewContainer

        navigationBar.items = [item]

        view.addSubview(navigationBar)

        navigationBar.snp.makeConstraints { make in
            if #available(iOS 11.0, *) {
                make.top.equalTo(view.safeAreaLayoutGuide.snp.topMargin)
                make.trailing.equalTo(view.safeAreaLayoutGuide.snp.trailingMargin)
                make.leading.equalTo(view.safeAreaLayoutGuide.snp.leadingMargin)
            } else {
                make.top.equalToSuperview()
                make.trailing.equalToSuperview()
                make.leading.equalToSuperview()
            }
        }

        return (bag, navigationBar)
    }
}

extension Offer: Presentable {
    func materialize() -> (UIViewController, Disposable) {
        let bag = DisposeBag()
        let viewController = LightContentViewController()
        viewController.title = "Offer"

        ApplicationState.preserveState(.offer)

        let view = UIView()
        view.backgroundColor = .darkPurple

        let (navigationBarBag, navigationBar) = addNavigationBar(view, viewController)
        bag += navigationBarBag

        viewController.view = view

        let reactView = RCTRootView(
            bridge: ReactNativeContainer.shared.bridge,
            moduleName: "OfferScreen",
            initialProperties: [:]
        )

        if let reactView = reactView {
            reactView.backgroundColor = .darkPurple
            view.addSubview(reactView)
            reactView.snp.makeConstraints { make in
                if #available(iOS 11.0, *) {
                    make.top.equalTo(navigationBar.snp.bottom)
                    make.trailing.leading.bottom.equalToSuperview()
                } else {
                    // Fallback on earlier versions
                }
            }
        }

        return (viewController, bag)
    }
}
