package com.example.yummy.core.admin

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
import com.example.yummy.core.view.IntroSliderActivity
import com.example.yummy.databinding.ActivityAdminBinding
import com.example.yummy.utils.base.BaseActivity
import com.example.yummy.utils.dialogs.NotificationSheetDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : BaseActivity<ActivityAdminBinding>(),
    NotificationSheetDialog.ActionButtonClickedListener,
    NotificationSheetDialog.OnButtonsClickListener{


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
        val navView: BottomNavigationView = binding.bottomNavigation

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
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

    override fun onNotificationActionButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onNegativeClick() {
        TODO("Not yet implemented")
    }

    override fun onPositiveClick() {
        TODO("Not yet implemented")
    }
}