package com.example.yummy.core.user.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.admin.home.adapter.HomeFragmentProductsAdapter
import com.example.yummy.core.admin.home.viewmodel.HomeViewModel
import com.example.yummy.core.user.home.adapter.UserHomeFragmentsProductAdapter
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentHomeBinding
import com.example.yummy.databinding.FragmentUserHomeBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.dialogs.NotificationSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment : BaseFragment<FragmentUserHomeBinding>(),
    NotificationSheetDialog.OnButtonsClickListener {
    private lateinit var binding: FragmentUserHomeBinding

    @Inject
    lateinit var userHomeFragmentsProductAdapter: UserHomeFragmentsProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private val homeViewModel by viewModels<HomeViewModel>()
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_user_home

    override fun getLayoutBinding(binding: FragmentUserHomeBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productsRecyclerView = binding.rvUserProducts

        homeViewModel.getAllProducts()
        subscribeToLiveData()
        setupProductsAdapter()

        binding.textLogout.setOnClickListener {
            Tools.showToast(requireContext(),"Log out!")
            firebaseAuth.signOut()
            requireActivity().finish()
        }
    }

    private fun showProducts(product: Product?) {
        userHomeFragmentsProductAdapter.submitList(listOf(product))
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
        productsRecyclerView.adapter = userHomeFragmentsProductAdapter

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
                    Tools.openSuccessErrorDialog(
                        requireActivity(),
                        response.message,
                        "Failed",
                        false,
                        false
                    )
                }

                else -> {}
            }
        }
    }


    companion object {

        fun newInstance(param1: String, param2: String) =
            UserHomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onNegativeClick() {
        TODO("Not yet implemented")
    }

    override fun onPositiveClick() {
        TODO("Not yet implemented")
    }
}