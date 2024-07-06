package com.example.yummy.onboarding.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.yummy.R
import com.example.yummy.utils.dialogs.BottomDialog2Options
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : FragmentActivity(), BottomDialog2Options.OnSelectMenuItemListener  {

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

    override fun onProceed() {

    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }
}