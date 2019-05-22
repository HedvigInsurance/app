package com.hedvig.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.whenApiVersion
import com.ice.restring.Restring
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {

    val firebaseAnalytics: FirebaseAnalytics by inject()

    val disposables = CompositeDisposable()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Restring.wrapContext(newBase))
        SplitCompat.install(this)
    }

    override fun onStart() {
        super.onStart()

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener { pendingDynamicLinkData ->
            if (pendingDynamicLinkData != null && pendingDynamicLinkData.link != null) {
                val link = pendingDynamicLinkData.link
                val referee = link.getQueryParameter("memberId")
                val incentive = link.getQueryParameter("incentive")
                if (referee != null && incentive != null) {
                    getSharedPreferences("referrals", Context.MODE_PRIVATE).edit().putString("referee", referee)
                        .putString("incentive", incentive).apply()

                    val b = Bundle()
                    b.putString("invitedByMemberId", referee)
                    b.putString("incentive", incentive)

                    FirebaseAnalytics.getInstance(this).logEvent("referrals_open", b)
                }
            }
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Exponent_Light)
        super.onCreate(savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.M) {
            window.statusBarColor = compatColor(R.color.off_white)
        }
    }

}
