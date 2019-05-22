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
import Photos
import Presentation

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

    @objc func logEcommercePurchase() {
        let bag = DisposeBag()

        bag += ApolloContainer.shared.client.fetch(query: InsurancePriceQuery())
            .valueSignal
            .compactMap { $0.data?.insurance.monthlyCost }
            .onValue { monthlyCost in
                bag.dispose()
                Analytics.logEvent("ecommerce_purchase", parameters: [
                    "transaction_id": UUID().uuidString,
                    "value": monthlyCost,
                    "currency": "SEK"
                ])
            }
    }

    @objc func showPeril(_: String, idString: String, title: String, description: String) {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                return
            }

            var topController = rootViewController

            while let newTopController = topController.presentedViewController {
                topController = newTopController
            }

            let perilInformation = PerilInformation(
                title: title,
                description: description,
                icon: Peril.iconAsset(for: idString)
            )
            let overlay = DraggableOverlay(
                presentable: perilInformation,
                presentationOptions: [.defaults, .prefersNavigationBarHidden(true)]
            )
            self.bag += topController.present(
                overlay,
                style: .default,
                options: []
            ).disposable
        }
    }

    @objc func showFileUploadOverlay(_ _: Bool, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter _: RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                return
            }

            var topController = rootViewController

            while let newTopController = topController.presentedViewController {
                topController = newTopController
            }

            let imageOrVideoAction = Alert.Action(title: "Bild eller film", style: .default) { _ -> Void in
                topController.present(ImagePicker(), options: []).onValue { url in
                    resolve([url.absoluteString])
                }
            }
            let fileAction = Alert.Action(title: "Fil", style: .default) { _ -> Void in
                topController.present(DocumentPicker(), options: []).onValue { urls in
                    resolve(urls.map { $0.absoluteString })
                }
            }
            let cancelAction = Alert.Action(title: "Stäng", style: .cancel) {}

            let alert = Alert(title: "Vad vill du skicka?", actions: [imageOrVideoAction, fileAction, cancelAction])

            self.bag += topController.present(alert, style: .sheet()).disposable
        }
    }

    @objc func presentLoggedIn() {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                return
            }

            var topController = rootViewController

            while let newTopController = topController.presentedViewController {
                topController = newTopController
            }

            self.bag += topController.present(
                LoggedIn(),
                style: .default,
                options: [.prefersNavigationBarHidden(true)]
            )
        }
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

    @objc func requestCameraPermissions(_ _: Bool, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter _: RCTPromiseRejectBlock) {
        PHPhotoLibrary.requestAuthorization { status in
            resolve(status == .authorized)
        }
    }
}
