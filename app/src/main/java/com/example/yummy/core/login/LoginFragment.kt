package com.example.yummy.core.login

import android.os.Bundle
import com.example.yummy.R
import com.example.yummy.databinding.FragmentLoginBinding
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    private lateinit var binding: FragmentLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_login

    override fun getLayoutBinding(binding: FragmentLoginBinding) {
        this.binding = binding
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {

            }
    }
}