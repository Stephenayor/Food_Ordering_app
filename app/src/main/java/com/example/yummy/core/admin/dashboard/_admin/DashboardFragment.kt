package com.example.yummy.core.admin.dashboard._admin

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.yummy.R
import com.example.yummy.core.login.LoginFragmentDirections
import com.example.yummy.databinding.FragmentDashboardBinding
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    private lateinit var binding: FragmentDashboardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_dashboard

    override fun getLayoutBinding(binding: FragmentDashboardBinding) {
        this.binding = binding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addProductFab.setOnClickListener {
            val action =
                DashboardFragmentDirections.actionNavigationDashboardToAddProductFragment()
            findNavController().navigate(action)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {

            }
    }
}