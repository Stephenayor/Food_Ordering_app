package com.example.yummy.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.yummy.R

class OnboardingActivity : FragmentActivity() {

    companion object {
        fun start(context: Context) {
            val starter = Intent(
                context,
                OnboardingActivity::class.java
            )
            context.startActivity(starter)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)


    }
}