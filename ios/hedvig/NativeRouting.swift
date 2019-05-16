//
//  NativeRouting.swift
//  hedvig
//
//  Created by Sam Pettersson on 2018-12-06.
//  Copyright © 2018 Hedvig AB. All rights reserved.
//

import Firebase
import Flow
import Foundation

struct MarketingResultEventBody: Encodable {
    var marketingResult: String?
    var componentId: String?
}

@objc(NativeRouting)
class NativeRouting: RCTEventEmitter {
    let appHasLoadedCallbacker: Callbacker<Void>
    let appHasLoadedSignal: Signal<Void>
    var componentIds: [(componentId: String, componentName: String)] = []
    let bag = DisposeBag()
    var hasOpenedChat = false

    override init() {
        appHasLoadedCallbacker = Callbacker<Void>()
        appHasLoadedSignal = appHasLoadedCallbacker.signal()
        super.init()
    }

    override func supportedEvents() -> [String]! {
        return [
            "NativeRoutingMarketingResult",
            "NativeRoutingAppHasLoaded",
            "NativeRoutingOpenFreeTextChat",
            "NativeRoutingClearDirectDebitStatus",
            "NativeRoutingRestartChat"
        ]
    }

    func sendMarketingResult(marketingResult: MarketingResult) {
        let marketingResultString = marketingResult == .onboard ? "onboard" : "login"

        let tuple = componentIds.filter { (_: String, componentName: String) -> Bool in
            if componentName == "marketingScreen" {
                return true
            }

            return false
        }.last

        if let componentId = tuple?.componentId {
            sendEvent(withName: "NativeRoutingMarketingResult", body: [
                "marketingResult": marketingResultString,
                "componentId": componentId
            ])
        }
    }

    func sendClearDirectDebitStatus() {
        sendEvent(withName: "NativeRoutingClearDirectDebitStatus", body: [])
    }

    func sendOpenFreeTextChat() {
        sendEvent(withName: "NativeRoutingOpenFreeTextChat", body: [])
    }

    func sendRestartChat() {
        sendEvent(withName: "NativeRoutingRestartChat", body: [])
    }

    @objc func appHasLoaded() {
        appHasLoadedCallbacker.callAll()
    }

    @objc func showOffer() {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                return
            }

            var topController = rootViewController

            while let newTopController = topController.presentedViewController {
                topController = newTopController
            }

            self.bag += topController.present(
                Offer(),
                style: .default,
                options: [.prefersNavigationBarHidden(true)]
            )
        }
    }

    @objc func openChat() {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController, !self.hasOpenedChat else {
                return
            }

            var topController = rootViewController

            while let newTopController = topController.presentedViewController {
                topController = newTopController
            }

            self.hasOpenedChat = true

            let chatOverlay = DraggableOverlay(presentable: FreeTextChat())
            topController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)]).onResult { _ in
                self.hasOpenedChat = false
            }
        }
    }

    @objc func userDidSign() {
        guard let invitedByMemberId = UserDefaults.standard.string(
            forKey: "referral_invitedByMemberId"
        ) else { return }
        guard let incentive = UserDefaults.standard.string(
            forKey: "referral_incentive"
        ) else { return }

        RCTApolloClient.getClient().onValue { _ in
            self.bag += ApolloContainer.shared.client.fetch(query: MemberIdQuery()).valueSignal.compactMap {
                $0.data?.member.id
            }.onValue { memberId in
                let db = Firestore.firestore()

                Analytics.logEvent("referrals_sign", parameters: [
                    "invitedByMemberId": invitedByMemberId,
                    "memberId": memberId,
                    "incentive": incentive
                ])

                db.collection("referrals").addDocument(data: [
                    "invitedByMemberId": invitedByMemberId,
                    "memberId": memberId,
                    "incentive": incentive,
                    "timestamp": Date().timeIntervalSince1970
                ]) { _ in
                }
            }
        }
    }

    @objc func registerExternalComponentId(_ componentId: String, componentName componentNameString: String) {
        componentIds.append((componentId: componentId, componentName: componentNameString))
    }
}
