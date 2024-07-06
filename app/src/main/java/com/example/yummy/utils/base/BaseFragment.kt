package com.example.yummy.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.yummy.utils.Tools
import com.example.yummy.utils.dialogs.ProgressDialogFragment

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    private var progressDialogFragment: ProgressDialogFragment? = null

    private lateinit var binding: B

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


}