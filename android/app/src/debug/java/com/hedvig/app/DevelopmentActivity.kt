package com.hedvig.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.hedvig.app.feature.whatsnew.WhatsNewActivity

class DevelopmentActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_development)

        findViewById<Button>(R.id.openWhatsNew).setOnClickListener {
            startActivity(Intent(this, WhatsNewActivity::class.java))
        }
    }
}
