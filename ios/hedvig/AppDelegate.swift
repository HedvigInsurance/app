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

extension Sequence where Element == PresentableIdentifier {
    var hasChatScreen: Bool {
        let onboardingChat = OnboardingChat(intent: .onboard)
        let onboardingChatIdentifier = PresentableIdentifier("\(type(of: onboardingChat))")

        return contains(onboardingChatIdentifier)
    }
}

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    let bag = DisposeBag()
    var rootWindow = UIWindow(frame: UIScreen.main.bounds)
    var splashWindow: UIWindow? = UIWindow(frame: UIScreen.main.bounds)
    var toastWindow: UIWindow?
    let toastSignal = ReadWriteSignal<Toast?>(nil)

    let hasFinishedLoading = ReadWriteSignal<Bool>(false)
    private let applicationWillTerminateCallbacker = Callbacker<Void>()
    let applicationWillTerminateSignal: Signal<Void>
    let gcmMessageIDKey = "gcm.message_id"

    var screenStack: [PresentableIdentifier] = []

    override init() {
        applicationWillTerminateSignal = applicationWillTerminateCallbacker.signal()
        super.init()
        toastWindow = createToastWindow()
    }

    func createToastWindow() -> UIWindow {
        let window = PassTroughWindow(frame: UIScreen.main.bounds)
        window.isOpaque = false
        window.backgroundColor = UIColor.transparent

        let toasts = Toasts(toastSignal: toastSignal)

        bag += window.add(toasts) { toastsView in
            bag += toastSignal.onValue { _ in
                window.makeKeyAndVisible()

                toastsView.snp.remakeConstraints { make in
                    let position: CGFloat = 69
                    if #available(iOS 11.0, *) {
                        let hasModal = self.rootWindow.rootViewController?.presentedViewController != nil
                        let safeAreaBottom = self.rootWindow.rootViewController?.view.safeAreaInsets.bottom ?? 0
                        let extraPadding: CGFloat = hasModal ? 0 : position
                        make.bottom.equalTo(-(safeAreaBottom + extraPadding))
                    } else {
                        make.bottom.equalTo(-position)
                    }

                    make.centerX.equalToSuperview()
                }
            }
        }

        bag += toasts.idleSignal.onValue { _ in
            self.toastSignal.value = nil
            self.rootWindow.makeKeyAndVisible()
        }

        return window
    }

    func createToast(
        symbol: ToastSymbol,
        body: String,
        textColor: UIColor = UIColor.offBlack,
        backgroundColor: UIColor = UIColor.white,
        duration: TimeInterval = 5.0
    ) {
        bag += Signal(after: 0).withLatestFrom(toastSignal.atOnce().plain()).onValue(on: .main) { _, previousToast in
            let toast = Toast(
                symbol: symbol,
                body: body,
                textColor: textColor,
                backgroundColor: backgroundColor,
                duration: duration
            )

            if toast != previousToast {
                self.toastSignal.value = toast
            }
        }
    }

    func application(
        _: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> BooleanLiteralType {
        honestyPledgeOpenClaimsFlow = { viewController in
            viewController.present(ClaimChat(), style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        commonClaimEmergencyOpenFreeTextChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: FreeTextChat(), adjustsToKeyboard: false)
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        dashboardOpenFreeTextChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: FreeTextChat(), adjustsToKeyboard: false)
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        commonClaimEmergencyOpenCallMeChat = { viewController in
            let chatOverlay = DraggableOverlay(presentable: CallMeChat(), adjustsToKeyboard: false)
            viewController.present(chatOverlay, style: .default, options: [.prefersNavigationBarHidden(false)])
        }

        presentablePresentationEventHandler = { (event: () -> PresentationEvent, file, function, line) in
            let presentationEvent = event()
            let message: String
            var data: String?

            switch presentationEvent {
            case let .willEnqueue(presentableId, context):
                message = "\(context) will enqueue modal presentation of \(presentableId)"
            case let .willDequeue(presentableId, context):
                message = "\(context) will dequeue modal presentation of \(presentableId)"
            case let .willPresent(presentableId, context, styleName):
                message = "\(context) will '\(styleName)' present: \(presentableId)"
            case let .didCancel(presentableId, context):
                message = "\(context) did cancel presentation of: \(presentableId)"
            case let .didDismiss(presentableId, context, result):
                switch result {
                case let .success(result):
                    message = "\(context) did end presentation of: \(presentableId)"
                    data = "\(result)"
                case let .failure(error):
                    message = "\(context) did end presentation of: \(presentableId)"
                    data = "\(error)"
                }
            #if DEBUG
                case let .didDeallocate(presentableId, context):
                    message = "\(presentableId) was deallocated after presentation from \(context)"
                case let .didLeak(presentableId, context):
                    message = "WARNING \(presentableId) was NOT deallocated after presentation from \(context)"
            #endif
            }

            switch presentationEvent {
            case let .willPresent(presentableId, _, _):
                self.screenStack.append(presentableId)
            case let .didDismiss(presentableId, _, _):
                if let index = self.screenStack.lastIndex(of: presentableId) {
                    self.screenStack.remove(at: index)
                }
            default:
                break
            }

            presentableLogPresentation(message, data, file, function, line)
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
            if let localizationKey = title.localizationKey?.description {
                Analytics.logEvent("tap_\(localizationKey)", parameters: nil)
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

        UNUserNotificationCenter.current().delegate = self

        // Set the last seen news version to current if we dont have a previous state indicating this is a first launch
        if !ApplicationState.hasPreviousState() {
            ApplicationState.setLastNewsSeen()
        }

        bag += RCTApolloClient.restoreState()
            .delay(by: 0.1)
            .onValue { _ in
                self.bag += ApplicationState.presentRootViewController(self.rootWindow)
                self.hasFinishedLoading.value = true
            }

        RNBranch.initSession(launchOptions: launchOptions, isReferrable: true)

        return true
    }

    func applicationWillTerminate(_: UIApplication) {
        applicationWillTerminateCallbacker.callAll()
    }

    func logout() {
        ApolloContainer.shared.deleteToken()
        RCTAsyncLocalStorage().clearAllData()

        bag += Signal(after: 0.2).onValue {
            self.bag += RCTApolloClient.getClient().onValue { _ in
                ReactNativeContainer.shared.bridge.reload()
                self.bag.dispose()
                ApplicationState.preserveState(.marketing)
                self.bag += ApplicationState.presentRootViewController(self.rootWindow)
            }
        }
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
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )

        UIApplication.shared.registerForRemoteNotifications()
    }

    func userNotificationCenter(_: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        guard let notificationType = userInfo["TYPE"] as? String else { return }

        if response.actionIdentifier == UNNotificationDefaultActionIdentifier {
            if notificationType == "NEW_MESSAGE" {
                let chatOverlay = DraggableOverlay(presentable: FreeTextChat())
                let chatOverlayIdentifier = PresentableIdentifier("\(type(of: chatOverlay))")
                let onboardingChat = OnboardingChat(intent: .onboard)
                let onboardingChatIdentifier = PresentableIdentifier("\(type(of: onboardingChat))")

                guard !screenStack.contains(chatOverlayIdentifier), !screenStack.contains(onboardingChatIdentifier) else {
                    return
                }

                bag += hasFinishedLoading.atOnce().filter { $0 }.delay(by: 0.5).onValue { _ in
                    self.getTopMostViewController()?.present(
                        chatOverlay,
                        style: .default,
                        options: [.prefersNavigationBarHidden(false)]
                    )
                }
            } else if notificationType == "REFERRAL_SUCCESS" {
                guard let incentiveString = userInfo["DATA_MESSAGE_REFERRED_SUCCESS_INCENTIVE_AMOUNT"] as? String else { return }
                guard let name = userInfo["DATA_MESSAGE_REFERRED_SUCCESS_NAME"] as? String else { return }

                let incentive = Int(Double(incentiveString) ?? 0)

                let referralsNotification = ReferralsNotification(incentive: incentive, name: name)
                let referralsNotificationIdentifier = PresentableIdentifier("\(type(of: referralsNotification))")

                guard !screenStack.contains(referralsNotificationIdentifier) else {
                    return
                }

                bag += hasFinishedLoading.atOnce().filter { $0 }.delay(by: 0.5).onValue { _ in
                    self.getTopMostViewController()?.present(
                        referralsNotification,
                        style: .modally(presentationStyle: .formSheetOrOverFullscreen, transitionStyle: nil, capturesStatusBarAppearance: nil),
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

        if let referralCode = queryItems?.filter({ item in item.name == "code" }).first?.value {
            bag += hasFinishedLoading.atOnce().filter { $0 }.delay(by: 0.5).onValue { _ in
                self.getTopMostViewController()?.present(
                    ReferralsReceiverConsent(referralCode: referralCode),
                    style: .modally(presentationStyle: .formSheetOrOverFullscreen, transitionStyle: nil, capturesStatusBarAppearance: nil),
                    options: [.prefersNavigationBarHidden(true)]
                ).onValue { result in
                    if result == .accept, !self.screenStack.hasChatScreen {
                        self.bag += self.rootWindow.rootViewController?.present(
                            OnboardingChat(intent: .onboard),
                            options: [.prefersNavigationBarHidden(false)]
                        ).disposable
                    }
                }
            }

            Analytics.logEvent("referrals_open", parameters: [
                "code": referralCode
            ])

            return true
        }

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
