package com.example.yummy.utils.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.yummy.R
import com.example.yummy.utils.Tools
import com.example.yummy.utils.dialogs.ProgressDialogFragment

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    private var progressDialogFragment: ProgressDialogFragment? = null

    private lateinit var binding: B
    private val activity: Activity? = null

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getLayoutBinding(binding: B)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        getLayoutBinding(binding)
        return binding.root
    }

    fun hideLoading() {
        progressDialogFragment?.dismiss()
    }

    fun showLoading(topTitle: Int, bottomTitle: Int) {
        progressDialogFragment = Tools.showProgressDialog(
            requireActivity(),
            topTitle, bottomTitle
        )
    }

    fun getBaseActivity(): Activity? {
        return activity
    }

      fun initToolbar(baseActivity: AppCompatActivity, toolbar: Toolbar) {
          baseActivity.setSupportActionBar(toolbar)
          baseActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
          baseActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
    }


}