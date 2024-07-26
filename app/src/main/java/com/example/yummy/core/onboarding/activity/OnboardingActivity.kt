package com.example.yummy.core.onboarding.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yummy.BR
import com.example.yummy.R
import com.example.yummy.core.view.IntroSliderActivity
import com.example.yummy.databinding.ActivityOnboardingBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseActivity
import com.example.yummy.utils.dialogs.BottomDialog2Options
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(),
    BottomDialog2Options.OnSelectMenuItemListener {

    companion object {
        private var IS_FOR_LOGIN_FRAGMENT =
            Companion::class.java.canonicalName?.plus("IS_FOR_LOGIN_FRAGMENT")

        fun start(context: Context, isForLoginFragment: Boolean) {
            val starter = Intent(context, OnboardingActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean(IS_FOR_LOGIN_FRAGMENT, isForLoginFragment)
            starter.putExtras(bundle)
            context.startActivity(starter)
        }
    }


    override val bindingVariable: Int
        get() = BR._all
    override val layoutId: Int
        get() = R.layout.activity_onboarding

    override val toolbarTitle: Int = R.string.yummy


    override fun initComponents() {
        val binding: ActivityOnboardingBinding = getViewDataBinding()
        val isForLoginFragment = intent.getBooleanExtra(IS_FOR_LOGIN_FRAGMENT, false)

        if (isForLoginFragment){
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.loginFragment)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            R.id.hamburger_icon -> {
                Tools.showToast(this, "Reaching")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onProceed() {

    }

    override fun onCancel() {

    }
}