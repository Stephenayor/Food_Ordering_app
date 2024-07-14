package com.example.yummy.utils.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.yummy.R
import com.example.yummy.databinding.FragmentSuccessBinding
import com.example.yummy.utils.NavigateTo
import com.example.yummy.utils.base.BaseFragment


class SuccessFragment : BaseFragment<FragmentSuccessBinding>() {
    private lateinit var binding: FragmentSuccessBinding
    private val args by navArgs<SuccessFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_success

    override fun getLayoutBinding(binding: FragmentSuccessBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionBtn.text = args.buttonText
        binding.actionTopTitle.text = args.title
        binding.actionSubTitle.text = args.subtitle
        binding.actionBtn.setOnClickListener {
            if (args.whereToNaviagateText?.equals(NavigateTo.LOGIN.toString()) == true){
                val action = SuccessFragmentDirections.actionSuccessFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            when (args.whereToNaviagateText) {
                NavigateTo.LOGIN.equals(args.whereToNaviagateText).toString() -> {

                }
            }

        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SuccessFragment().apply {

            }
    }
}