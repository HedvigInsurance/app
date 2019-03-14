package com.hedvig.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;

import com.RNFetchBlob.RNFetchBlobPackage;
import com.airbnb.android.react.lottie.LottiePackage;
import com.brentvatne.react.ReactVideoPackage;
import com.dylanvann.fastimage.FastImageViewPackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.horcrux.svg.SvgPackage;
import com.imagepicker.ImagePickerPackage;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.leo_pharma.analytics.AnalyticsPackage;
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage;
import com.microsoft.codepush.react.CodePush;
import com.reactnativedocumentpicker.ReactNativeDocumentPicker;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.react.NavigationReactNativeHost;
import com.rnfs.RNFSPackage;
import com.rnim.rn.audio.ReactNativeAudioPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;
import com.zmxv.RNSound.RNSoundPackage;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.branch.referral.Branch;
import io.branch.rnbranch.RNBranchPackage;
import io.invertase.firebase.RNFirebasePackage;
import io.invertase.firebase.messaging.RNFirebaseMessagingPackage;
import io.invertase.firebase.notifications.RNFirebaseNotificationsPackage;
import io.sentry.RNSentryPackage;
import timber.log.Timber;

public class MainApplication extends NavigationApplication implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    protected ReactNativeHost createReactNativeHost() {
        return new NavigationReactNativeHost(this) {
            @Override
            public boolean getUseDeveloperSupport() {
                return isDebug();
            }

            @Override
            protected String getJSMainModuleName() {
                return "index";
            }

            @Nullable
            @Override
            protected String getJSBundleFile() {
                return CodePush.getJSBundleFile();
            }
        };
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    protected List<ReactPackage> getPackages() {
        return Arrays.asList(
                new MainReactPackage(),
            new DatePickerPackage(),
                new ReactNativeDocumentPicker(),
                new FastImageViewPackage(),
                new ImagePickerPackage(),
                new RNFSPackage(),
                new ReactVideoPackage(),
                new RNGestureHandlerPackage(),
                new RNDeviceInfo(),
                new SvgPackage(),
                new ReactNativeConfigPackage(),
                new RNFetchBlobPackage(),
                new CodePush(BuildConfig.CODE_PUSH_ANDROID_DEPLOYMENT_KEY, getApplicationContext(), isDebug()),
                new RNSoundPackage(),
                new RNSentryPackage(),
                new RNFirebasePackage(),
                new RNFirebaseNotificationsPackage(),
                new RNFirebaseMessagingPackage(),
                new RNBranchPackage(),
                new ReactNativeAudioPackage(),
                new AnalyticsPackage(),
                new LottiePackage(),
                new NativeRoutingPackage()
        );
    }

    @Override
    public List<ReactPackage> createAdditionalReactPackages() {
        return getPackages();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Branch.getAutoInstance(this);
        SoLoader.init(this, false);
        Timber.plant(new Timber.DebugTree());
        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
        registerExternalComponent("marketingScreen", new MarketingScreenComponentCreator());
        registerExternalComponent("logo", new LogoComponentCreator());
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
