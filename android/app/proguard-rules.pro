# okio and okhttp
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# react-native-branch
-dontwarn io.branch.**

# react-native-analytics-segment-io
-dontwarn com.segment.analytics.android.integrations.**

# react-native-svg
-dontwarn com.caverock.androidsvg.**

# Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-printmapping mapping.txt

# react-native
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

# react-native-config
-keep class com.hedvig.app.BuildConfig { *; }

# Gson
-keep class com.hedvig.app.feature.chat.dto.** { *; }