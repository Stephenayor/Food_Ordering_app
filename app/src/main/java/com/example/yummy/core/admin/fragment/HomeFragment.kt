package com.example.yummy.core.admin.fragment

import android.os.Bundle
import com.example.yummy.R
import com.example.yummy.databinding.FragmentHomeBinding
import com.example.yummy.utils.base.BaseFragment


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun getLayoutBinding(binding: FragmentHomeBinding) {
        this.binding = binding
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}