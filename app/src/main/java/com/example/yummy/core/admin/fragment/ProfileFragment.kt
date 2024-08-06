package com.example.yummy.core.admin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yummy.R
import com.example.yummy.databinding.FragmentProfileBinding
import com.example.yummy.utils.base.BaseFragment


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private lateinit var binding: FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun getLayoutBinding(binding: FragmentProfileBinding) {
        this.binding = binding
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {

            }
    }
}