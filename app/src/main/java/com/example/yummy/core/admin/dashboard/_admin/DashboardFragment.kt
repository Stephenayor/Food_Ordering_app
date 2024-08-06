package com.example.yummy.core.admin.dashboard._admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.yummy.R
import com.example.yummy.core.admin.dashboard._admin.viewmodel.DashboardViewModel
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentDashboardBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    private lateinit var binding: FragmentDashboardBinding
    private val dashboardViewModel by viewModels<DashboardViewModel>()


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

        dashboardViewModel.getProducts()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        dashboardViewModel.getProductsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.getting_latest_product, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    showProduct(response.data)

                }

                is Resource.Error -> {
                    hideLoading()
                    Tools.showToast(requireContext(), "Error Uploading Image to Cloud Storage")
                }

                else -> {}
            }
        }
    }

    private fun showProduct(product: Product?) {
        binding.productNameText.text = product?.productName
        Glide
            .with(requireContext())
            .load(product?.productImage)
            .centerCrop()
            .into(binding.productImage);
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {

            }
    }
}