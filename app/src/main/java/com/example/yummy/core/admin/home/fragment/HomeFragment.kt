package com.example.yummy.core.admin.home.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.admin.home.adapter.HomeFragmentProductsAdapter
import com.example.yummy.core.admin.home.viewmodel.HomeViewModel
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentHomeBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()
    @Inject
    lateinit var homeFragmentProductsAdapter: HomeFragmentProductsAdapter
    private lateinit var productsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun getLayoutBinding(binding: FragmentHomeBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsRecyclerView = binding.rvProducts

        homeViewModel.getAllProducts()
        subscribeToLiveData()
        setupProductsAdapter()
    }

    private fun subscribeToLiveData() {
        homeViewModel.getAllAvailableProductsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.getting_product, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    showProducts(response.data)
                }

                is Resource.Error -> {
                    hideLoading()
//                    Tools.openSuccessErrorDialog(
//                        requireActivity(),
//                        response.message,
//                        "Failed",
//                        false,
//                        false
//                    )
                }

                else -> {}
            }
        }
    }

    private fun showProducts(product: Product?) {
        homeFragmentProductsAdapter.submitList(listOf(product))
    }

    private fun setupProductsAdapter() {
        val numberOfColumns = 3
        val layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        productsRecyclerView.layoutManager = layoutManager
        productsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                GridLayoutManager.HORIZONTAL
            )
        )
        productsRecyclerView.adapter = homeFragmentProductsAdapter
//        homeFragmentProductsAdapter = HomeFragmentProductsAdapter(requireContext()) {
//            // Handle click event
//        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}