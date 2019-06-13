package com.hedvig.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.hedvig.app.feature.whatsnew.WhatsNewDialog

class DevelopmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_development)

        findViewById<Button>(R.id.openWhatsNew).setOnClickListener {
            WhatsNewDialog.newInstance("2.8.1").show(supportFragmentManager, "whats_new")
        }
    }
}
