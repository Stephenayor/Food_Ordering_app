package com.example.yummy.core.user.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yummy.R
import com.example.yummy.databinding.FragmentCompleteOrdersBinding
import com.example.yummy.databinding.FragmentProductDetailsBinding
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteOrdersFragment : BaseFragment<FragmentCompleteOrdersBinding>() {
    private lateinit var binding: FragmentCompleteOrdersBinding



    override fun getLayoutId(): Int = R.layout.fragment_complete_orders

    override fun getLayoutBinding(binding: FragmentCompleteOrdersBinding) {
        this.binding = binding
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompleteOrdersFragment().apply {

            }
    }
}