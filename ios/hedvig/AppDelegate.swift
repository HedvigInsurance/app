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
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    let bag = DisposeBag()
    var rootWindow = UIWindow(frame: UIScreen.main.bounds)
    var splashWindow: UIWindow? = UIWindow(frame: UIScreen.main.bounds)

    let hasFinishedLoading = ReadWriteSignal<Bool>(false)
    private let applicationWillTerminateCallbacker = Callbacker<Void>()
    let applicationWillTerminateSignal: Signal<Void>
    let gcmMessageIDKey = "gcm.message_id"

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

        let launch = Launch(
            hasLoadedSignal: hasFinishedLoading.filter { $0 }.toVoid()
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
        Messaging.messaging().delegate = self

        if #available(iOS 10, *) {
            UNUserNotificationCenter.current().delegate = self
        }

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
                    self.hasFinishedLoading.value = true
                    return
                }

                self.bag += rootNavigationController.present(Marketing()).disposable
                self.hasFinishedLoading.value = true
            }

        RNBranch.initSession(launchOptions: launchOptions, isReferrable: true)

        return true
    }

    func applicationWillTerminate(_: UIApplication) {
        applicationWillTerminateCallbacker.callAll()
    }

    func logout() {
        RCTAsyncLocalStorage().clearAllData()
        ReactNativeContainer.shared.bridge.reload()
        bag.dispose()
        ApplicationState.preserveState(.marketing)
        bag += ApplicationState.presentRootViewController(rootWindow)
    }

    func getTopMostViewController() -> UIViewController? {
        guard let rootViewController = rootWindow.rootViewController else {
            return nil
        }

        var topController = rootViewController

        while let newTopController = topController.presentedViewController {
            topController = newTopController
        }

        return topController
    }

    func messaging(_: Messaging, didReceiveRegistrationToken fcmToken: String) {
        ApolloContainer.shared.client.perform(mutation: RegisterPushTokenMutation(pushToken: fcmToken)).onValue { result in
            if result.data?.registerPushToken != nil {
                log.info("Did register push token for user")
            } else {
                log.info("Failed to register push token for user")
            }
        }
    }

    func registerForPushNotifications() {
        if #available(iOS 10.0, *) {
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: { _, _ in }
            )
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            UIApplication.shared.registerUserNotificationSettings(settings)
        }

        UIApplication.shared.registerForRemoteNotifications()
    }

    func userNotificationCenter(_: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        guard let notificationType = userInfo["TYPE"] as? String else { return }

        if response.actionIdentifier == UNNotificationDefaultActionIdentifier {
            if notificationType == "NEW_MESSAGE" {
                let chatOverlay = DraggableOverlay(presentable: FreeTextChat())

                bag += hasFinishedLoading.atOnce().filter { $0 }.delay(by: 0.5).onValue { _ in
                    self.getTopMostViewController()?.present(
                        chatOverlay,
                        style: .default,
                        options: [.prefersNavigationBarHidden(false)]
                    )
                }
            }
        }

        completionHandler()
    }

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

        bag += hasFinishedLoading.atOnce().filter { $0 }.delay(by: 0.5).onValue { _ in
            self.getTopMostViewController()?.present(
                ReferralsReceiverConsent(),
                style: .modal,
                options: [.prefersNavigationBarHidden(true)]
            ).onValue { result in
                if result == .accept {
                    self.bag += self.rootWindow.rootViewController?.present(
                        OnboardingChat(intent: .onboard),
                        options: [.prefersNavigationBarHidden(false)]
                    ).disposable
                }
            }
        }

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
