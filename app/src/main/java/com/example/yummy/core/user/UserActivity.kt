package com.example.yummy.core.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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
        val bottomNavView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.productDetailsFragment ->
                    bottomNavView.visibility = View.GONE
                R.id.completeOrdersFragment ->
                    bottomNavView.visibility = View.GONE
                R.id.paymentFragment ->
                    bottomNavView.visibility = View.GONE
                R.id.navigation_user_orders ->
                    bottomNavView.visibility = View.GONE
                R.id.navigation_user_account ->
                    bottomNavView.visibility = View.GONE
                R.id.navigation_user_categories ->
                    bottomNavView.visibility = View.GONE
                else -> bottomNavView.visibility = View.VISIBLE
            }
        }

        bottomNavView.setupWithNavController(navController)
//        val appBarConfiguration = AppBarConfiguration(
//            topLevelDestinationIds = setOf(),
//            fallbackOnNavigateUpListener = ::onSupportNavigateUp
//        )
//        findViewById<Toolbar>(R.id.toolbar).setupWithNavController(navController, appBarConfiguration)
//        setupActionBarWithNavController(navController)
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