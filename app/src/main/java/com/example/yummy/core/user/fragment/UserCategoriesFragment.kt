package com.example.yummy.core.user.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yummy.R
import com.example.yummy.databinding.FragmentUserCategoriesBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment


class UserCategoriesFragment : BaseFragment<FragmentUserCategoriesBinding>() {
    private lateinit var binding: FragmentUserCategoriesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_categories

    override fun getLayoutBinding(binding: FragmentUserCategoriesBinding) {
        this.binding = binding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.breakfastLayout.setOnClickListener {
            Tools.openComingSoonDialog(requireActivity())
        }
        binding.lunchLayout.setOnClickListener {
            Tools.openComingSoonDialog(requireActivity())
        }
        binding.dinnerLayout.setOnClickListener {
            Tools.openComingSoonDialog(requireActivity())
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserCategoriesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}