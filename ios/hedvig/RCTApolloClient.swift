//
//  RCTApolloClient.swift
//  hedvig
//
//  Created by Sam Pettersson on 2019-01-29.
//  Copyright © 2019 Hedvig AB. All rights reserved.
//

import Apollo
import Firebase
import Flow
import Foundation

struct RCTApolloClient {
    static func restoreState() -> CoreSignal<Finite, Void> {
        return getClient()
            .valueSignal
            .withLatestFrom(RCTApolloClient.getToken().valueSignal)
            .mapLatestToFuture { _, token -> Future<Void> in
                if token != nil, !ApplicationState.hasPreviousState() {
                    log.info("Backfilling previous state")

                    return Future { completion in
                        let bag = DisposeBag()

                        let statusFuture = ApolloContainer
                            .shared
                            .client
                            .fetch(query: InsuranceStatusQuery())
                            .map { $0.data?.insurance.status }

                        let priceFuture =
                            ApolloContainer
                            .shared
                            .client
                            .fetch(query: InsurancePriceQuery())
                            .map { $0.data?.insurance.monthlyCost }

                        bag += join(statusFuture, priceFuture)
                            .valueThenEndSignal
                            .debug()
                            .onValue { status, price in
                                guard let status = status else {
                                    ApplicationState.preserveState(.marketing)
                                    completion(.success)
                                    return
                                }

                                switch status {
                                case .active, .inactiveWithStartDate, .inactive, .terminated:
                                    ApplicationState.preserveState(.loggedIn)
                                case .pending:
                                    if price != 0 {
                                        ApplicationState.preserveState(.offer)
                                    } else {
                                        ApplicationState.preserveState(.onboardingChat)
                                    }
                                case .__unknown:
                                    ApplicationState.preserveState(.marketing)
                                }

                                completion(.success)
                            }

                        return bag
                    }
                }

                return Future(result: .success)
            }
    }

    static func getToken() -> Future<String?> {
        return Future<String?> { completion in
            let rctSenderBlock = { response in
                if let nativeToken = ApolloContainer.shared.retreiveToken() {
                    completion(.success(nativeToken.token))
                    return
                }

                guard let response = response else {
                    completion(.success(nil))
                    return
                }
                var value = ""

                if response.count > 1 {
                    var response1 = response[1] as! [Any]
                    if response1.count > 0 {
                        var response2 = response1[0] as! [Any]

                        if response2.count > 1 {
                            if let string = response2[1] as? String {
                                value = string
                            }
                        }
                    }
                }

                if value.count == 0 {
                    completion(.success(nil))
                } else {
                    ApolloContainer.shared.saveToken(token: value)
                    completion(.success(value))
                }
            } as RCTResponseSenderBlock

            RCTAsyncLocalStorage().multiGet(["@hedvig:token"], callback: rctSenderBlock)

            return NilDisposer()
        }
    }

    static func getClient() -> Future<Void> {
        let environment = ApolloEnvironmentConfig(
            endpointURL: URL(string: ReactNativeConfig.env(for: "GRAPHQL_URL"))!,
            wsEndpointURL: URL(string: ReactNativeConfig.env(for: "WS_GRAPHQL_URL"))!,
            assetsEndpointURL: URL(string: ReactNativeConfig.env(for: "ASSETS_GRAPHQL_URL"))!
        )

        ApolloContainer.shared.environment = environment

        let tokenFuture = RCTApolloClient.getToken()

        // we get a black screen flicker without the delay
        let clientFuture = tokenFuture.flatMap { token -> Future<Void> in
            guard let token = token else {
                let initClient = ApolloContainer.shared.initClient()

                // set the new token created by initClient in React Native's async storage
                // so that we don't create another session later on
                initClient.onValue { _ in
                    guard let token = ApolloContainer.shared.retreiveToken() else {
                        return
                    }

                    let rctSenderBlock = { _ in } as RCTResponseSenderBlock
                    RCTAsyncLocalStorage().multiSet(
                        [["@hedvig:token", token.token]],
                        callback: rctSenderBlock
                    )
                }

                return initClient
            }

            return Future { completion in
                ApolloContainer.shared.createClient(
                    token: token
                )
                completion(.success)
                return NilDisposer()
            }
        }

        clientFuture.onValue { _ in
            ApolloContainer.shared.client.fetch(query: MemberIdQuery()).onValue { response in
                if let memberId = response.data?.member.id {
                    Analytics.setUserID(memberId)
                }
            }
        }

        return clientFuture
    }
}
