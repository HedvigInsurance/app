import Apollo
import CommonCrypto
import Firebase
import FirebaseRemoteConfig
import Flow
import Form
import Foundation
import Presentation
import UIKit

let log = Logger.self

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    let bag = DisposeBag()
    var rootWindow = UIWindow(frame: UIScreen.main.bounds)
    var splashWindow: UIWindow? = UIWindow(frame: UIScreen.main.bounds)
    private let applicationWillTerminateCallbacker = Callbacker<Void>()
    let applicationWillTerminateSignal: Signal<Void>

    override init() {
        applicationWillTerminateSignal = applicationWillTerminateCallbacker.signal()
        super.init()
    }

    func application(
        _: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> BooleanLiteralType {
        honestyPledgeOpenClaimsFlow = { viewController in
            viewController.present(ClaimChat(), style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        commonClaimEmergencyOpenFreeTextChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: FreeTextChat())
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        dashboardOpenFreeTextChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: FreeTextChat())
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        commonClaimEmergencyOpenCallMeChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: CallMeChat())
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        viewControllerWasPresented = { viewController in
            let mirror = Mirror(reflecting: viewController)
            Analytics.setScreenName(
                viewController.debugPresentationTitle,
                screenClass: String(describing: mirror.subjectType)
            )

            if viewController.debugPresentationTitle == "LoggedIn" {
                Analytics.setUserProperty("true", forName: "isMember")
            }
        }

        alertActionWasPressed = { _, title in
            if let localizationKey = title.localizationKey?.toString() {
                Analytics.logEvent("alert_action_tap_\(localizationKey)", parameters: nil)
            }
        }

        let rootNavigationController = UINavigationController()
        rootNavigationController.setNavigationBarHidden(true, animated: false)
        rootWindow.rootViewController = rootNavigationController
        rootWindow.windowLevel = .normal
        rootWindow.backgroundColor = UIColor.white
        rootWindow.makeKeyAndVisible()

        let splashNavigationController = UINavigationController()
        splashWindow?.rootViewController = splashNavigationController
        splashNavigationController.setNavigationBarHidden(true, animated: false)
        splashNavigationController.view = { () -> UIView in
            let view = UIView()
            view.backgroundColor = UIColor.clear
            return view
        }()
        splashWindow?.isOpaque = false
        splashWindow?.backgroundColor = UIColor.clear
        splashWindow?.windowLevel = .alert
        splashWindow?.makeKeyAndVisible()

        let hasLoadedCallbacker = Callbacker<Void>()
        let launch = Launch(
            hasLoadedSignal: hasLoadedCallbacker.signal()
        )

        let launchPresentation = Presentation(
            launch,
            style: .modally(
                presentationStyle: .overCurrentContext,
                transitionStyle: .none,
                capturesStatusBarAppearance: true
            ),
            options: [.unanimated, .prefersNavigationBarHidden(true)]
        )

        DefaultStyling.installCustom()

        bag += splashNavigationController.present(launchPresentation).onValue { _ in
            self.splashWindow = nil
        }

        FirebaseApp.configure()

        bag += RCTApolloClient
            .getClient()
            .valueSignal
            .withLatestFrom(RCTApolloClient.getToken().valueSignal)
            .mapLatestToFuture { _, token -> Future<Void> in
                if token != nil, !ApplicationState.hasPreviousState() {
                    log.info("Backfilling previous state")

                    return Future { completion in
                        let innerBag = self.bag.innerBag()

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

                        innerBag += join(statusFuture, priceFuture)
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

                        return innerBag
                    }
                }

                return Future(result: .success)
            }
            .delay(by: 0.1)
            .onValue { _ in
                if let disposable = ApplicationState.presentRootViewController(self.rootWindow) {
                    self.bag += disposable
                    hasLoadedCallbacker.callAll()
                    return
                }

                self.bag += rootNavigationController.present(Marketing()).disposable
                hasLoadedCallbacker.callAll()
            }

        RNBranch.initSession(launchOptions: launchOptions, isReferrable: true)

        return true
    }

    func applicationWillTerminate(_: UIApplication) {
        applicationWillTerminateCallbacker.callAll()
    }

    func logout() {
        ReactNativeContainer.shared.bridge.reload()
        bag.dispose()
        RCTAsyncLocalStorage().clearAllData()
    }

    // func application(_: UIApplication, didReceive notification: UILocalNotification) {
    //    RNFirebaseNotifications.instance().didReceive(notification)
    // }

    // func application(_: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
    //    RNFirebaseNotifications.instance().didReceiveRemoteNotification(userInfo, fetchCompletionHandler: completionHandler)
    // }

    // func application(_: UIApplication, didRegister notificationSettings: UIUserNotificationSettings) {
    //    RNFirebaseMessaging.instance().didRegister(notificationSettings)
    // }

    func handleDynamicLink(_ dynamicLink: DynamicLink?) -> Bool {
        guard let dynamicLink = dynamicLink else { return false }
        guard let deepLink = dynamicLink.url else { return false }
        let queryItems = URLComponents(url: deepLink, resolvingAgainstBaseURL: true)?.queryItems

        guard let invitedByMemberId = queryItems?.filter({ item in item.name == "invitedBy" }).first?.value else {
            return false
        }
        guard let incentive = queryItems?.filter({ item in item.name == "incentive" }).first?.value else {
            return false
        }

        Analytics.logEvent("referrals_open", parameters: [
            "invitedByMemberId": invitedByMemberId,
            "incentive": incentive
        ])

        UserDefaults.standard.set(invitedByMemberId, forKey: "referral_invitedByMemberId")
        UserDefaults.standard.set(incentive, forKey: "referral_incentive")

        return true
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any] = [:]) -> BooleanLiteralType {
        if DynamicLinks.dynamicLinks().shouldHandleDynamicLink(fromCustomSchemeURL: url) {
            let dynamicLink = DynamicLinks.dynamicLinks().dynamicLink(fromCustomSchemeURL: url)
            return handleDynamicLink(dynamicLink)
        }

        if !RNBranch.branch.application(app, open: url, options: options) {
            return true
        }

        return true
    }

    func application(_: UIApplication, continue userActivity: NSUserActivity, restorationHandler _: @escaping ([UIUserActivityRestoring]?) -> Void) -> BooleanLiteralType {
        DynamicLinks.dynamicLinks().handleUniversalLink(userActivity.webpageURL!) { dynamicLink, _ in
            _ = self.handleDynamicLink(dynamicLink)
        }

        return RNBranch.continue(userActivity)
    }
}
