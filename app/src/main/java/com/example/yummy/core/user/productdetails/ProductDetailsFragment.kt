package com.example.yummy.core.user.productdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import com.example.yummy.R
import com.example.yummy.databinding.FragmentProductDetailsBinding
import com.example.yummy.databinding.FragmentUserHomeBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.view.SuccessFragmentArgs

class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding>() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var toolbar: Toolbar


    override fun getLayoutId(): Int = R.layout.fragment_product_details

    override fun getLayoutBinding(binding: FragmentProductDetailsBinding) {
        this.binding = binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = binding.toolbar
        initToolbar(requireActivity() as AppCompatActivity, toolbar)
        Tools.showToast(requireContext(), args.product?.productName)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailsFragment().apply {

            }
    }
}