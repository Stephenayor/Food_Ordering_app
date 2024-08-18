package com.example.yummy.core.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.yummy.BR
import com.example.yummy.R
import com.example.yummy.core.onboarding.activity.OnboardingActivity
import com.example.yummy.core.view.IntroSliderActivity
import com.example.yummy.databinding.ActivityAdminBinding
import com.example.yummy.databinding.ActivityUserBinding
import com.example.yummy.utils.base.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : BaseActivity<ActivityUserBinding>() {
    override val bindingVariable: Int
        get() = BR._all
    override val layoutId: Int
        get() = R.layout.activity_user

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, UserActivity::class.java)
            val bundle = Bundle()
            starter.putExtras(bundle)
            context.startActivity(starter)
        }
    }

    override val toolbarTitle: Int
        get() = R.string.yummy


    override fun initComponents() {
        val binding: ActivityUserBinding = getViewDataBinding()
        setupToolbar()
        val navView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

//        navView.setupWithNavController(navController)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        (this as? AppCompatActivity)?.setSupportActionBar(toolbar)
        // Optional: Set up navigation icon and listener
        (this as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (this as? AppCompatActivity)?.supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
           finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}