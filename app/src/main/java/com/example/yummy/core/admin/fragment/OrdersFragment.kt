package com.example.yummy.core.admin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yummy.R
import com.example.yummy.databinding.FragmentOrdersBinding
import com.example.yummy.utils.base.BaseFragment


class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {
    private lateinit var binding: FragmentOrdersBinding
    override fun getLayoutId(): Int = R.layout.fragment_orders

    override fun getLayoutBinding(binding: FragmentOrdersBinding) {
        this.binding = binding
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {

            }
    }
}