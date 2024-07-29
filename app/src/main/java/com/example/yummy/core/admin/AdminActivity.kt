package com.example.yummy.core.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yummy.BR
import com.example.yummy.R
import com.example.yummy.core.onboarding.activity.OnboardingActivity
import com.example.yummy.core.view.IntroSliderActivity
import com.example.yummy.databinding.ActivityAdminBinding
import com.example.yummy.databinding.ActivityOnboardingBinding
import com.example.yummy.utils.base.BaseActivity

class AdminActivity : BaseActivity<ActivityAdminBinding>() {


    override val bindingVariable: Int
        get() = BR._all
    override val layoutId: Int
        get() = R.layout.activity_admin

    companion object {
        private var IS_FOR_LOGIN_FRAGMENT =
            Companion::class.java.canonicalName?.plus("IS_FOR_LOGIN_FRAGMENT")

        fun start(context: Context) {
            val starter = Intent(context, AdminActivity::class.java)
            val bundle = Bundle()
            starter.putExtras(bundle)
            context.startActivity(starter)
        }
    }

    override val toolbarTitle: Int
        get() = R.string.yummy

    override fun initComponents() {
        val binding: ActivityAdminBinding = getViewDataBinding()
        setupToolbar()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        (this as? AppCompatActivity)?.setSupportActionBar(toolbar)
        // Optional: Set up navigation icon and listener
        (this as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (this as? AppCompatActivity)?.supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            IntroSliderActivity.start(this)
        }
    }
}