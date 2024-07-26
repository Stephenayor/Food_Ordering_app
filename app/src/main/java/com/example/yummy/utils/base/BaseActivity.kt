package com.example.yummy.utils.base

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.dialogs.ProgressDialogFragment


abstract class BaseActivity<T : ViewDataBinding?> : AppCompatActivity() {


    private var progressDialogFragment: ProgressDialogFragment? = null
    private var viewDataBinding: T? = null
    protected var toolbar: Toolbar? = null


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
//    abstract fun getViewModel(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        performDataBinding()
//        initToolbar()
        initComponents()
    }


    private fun performDependencyInjection() {
        try {
//            AndroidInjection.inject(this)
        } catch (e: Exception) {

        }
    }

//    fun hideKeyboard() {
//        KeyboardUtils.hideSoftInput(this.currentFocus)
//    }


    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
//        viewModel = if (viewModel == null) getViewModel() else viewModel
//        viewDataBinding?.setVariable(bindingVariable, viewModel)
        viewDataBinding?.executePendingBindings()
    }

    protected abstract val toolbarTitle: Int

    protected abstract fun initComponents()
    private fun initToolbar() {
        toolbar = findViewById(com.example.yummy.R.id.toolbar)
        if (toolbar != null) {
            setTitle(getResources().getString(toolbarTitle))
//            try {
//                setSupportActionBar(toolbar)
//                if (supportActionBar != null) {
//                    setDisplayHomeEnabled(true)
//                }
//                setTitle(getResources().getString(toolbarTitle))
//            } catch (e: Exception) {
//                e.printStackTrace()
//                finish()
//            }
        }

    }

    private fun setDisplayHomeEnabled(b: Boolean) {
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(b)
        }
    }

    fun hideLoading() {
        progressDialogFragment?.dismiss()
    }

    fun showLoading(topTitle: Int, bottomTitle: Int) {
        progressDialogFragment = Tools.showProgressDialog(
            this,
            topTitle, bottomTitle
        )
    }

    fun showLoading(topTitle: Int, bottomTitle: Int, isCancellable: Boolean) {
        showLoading(topTitle, bottomTitle)
        progressDialogFragment?.setCancelable(isCancellable)
    }

    fun getViewDataBinding(): T {
        return viewDataBinding!!
    }


    override fun finish() {
        super.finish()

    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}

