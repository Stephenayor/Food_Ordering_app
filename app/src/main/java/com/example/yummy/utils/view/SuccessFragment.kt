package com.example.yummy.utils.view

import android.os.Bundle
import android.view.View
import com.example.yummy.R
import com.example.yummy.databinding.FragmentSuccessBinding
import com.example.yummy.utils.base.BaseFragment


class SuccessFragment : BaseFragment<FragmentSuccessBinding>() {
    private lateinit var binding: FragmentSuccessBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_success

    override fun getLayoutBinding(binding: FragmentSuccessBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.actionBtn.text = args.onboardingStatus.buttonText
//        binding.actionTopTitle.text = args.onboardingStatus.title
//        binding.actionSubTitle.text = args.onboardingStatus.subtitle
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SuccessFragment().apply {

            }
    }
}