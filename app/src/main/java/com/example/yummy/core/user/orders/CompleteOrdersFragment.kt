package com.example.yummy.core.user.orders

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.admin.home.viewmodel.HomeViewModel
import com.example.yummy.core.user.home.adapter.UserHomeFragmentsProductAdapter
import com.example.yummy.core.user.orders.adapter.CartItemAdapter
import com.example.yummy.core.user.orders.viewmodel.CompleteOrderViewModel
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.databinding.FragmentCompleteOrdersBinding
import com.example.yummy.databinding.FragmentProductDetailsBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompleteOrdersFragment : BaseFragment<FragmentCompleteOrdersBinding>() {
    private lateinit var binding: FragmentCompleteOrdersBinding
    private val completeFragmentArgs by navArgs<CompleteOrdersFragmentArgs>()
    private lateinit var toolbar: Toolbar
    private val completeOrderViewModel by viewModels<CompleteOrderViewModel>()

    @Inject
    lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var cartProductsRecyclerView: RecyclerView


    override fun getLayoutId(): Int = R.layout.fragment_complete_orders

    override fun getLayoutBinding(binding: FragmentCompleteOrdersBinding) {
        this.binding = binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = binding.toolbar
        initToolbar(requireActivity() as AppCompatActivity, toolbar)
        cartProductsRecyclerView = binding.rvCartItemProducts

        setupCartProductsAdapter()
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            completeOrderViewModel.getAllItemInCart(
                userId
            )
        }
        setClickListeners()
        subscribeToLiveData()
    }

    private fun setClickListeners() {
        binding.imgBackButton.setOnClickListener {
            val action = CompleteOrdersFragmentDirections.
            actionCompleteOrdersFragmentToNavigationUserHome()
            findNavController().navigate(action)
        }
    }

    private fun subscribeToLiveData() {
//        completeOrderViewModel.addProductToCartResponse.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is Resource.Loading -> {
//                    showLoading(R.string.adding_product, R.string.please_wait_dots)
//                }
//
//                is Resource.Success -> {
//                    hideLoading()
//                    binding.progressBar.visibility = View.VISIBLE
//                    FirebaseAuth.getInstance().currentUser?.uid?.let {
//                        completeOrderViewModel.getAllItemInCart(
//                            it
//                        )
//                    }
//                    Snackbar.make(
//                        binding.snackbarView,
//                        "Successfully Added to Cart",
//                        Snackbar.LENGTH_SHORT
//                    )
//                        .show()
//                    showCartItems()
//                }
//
//                is Resource.Error -> {
//                    hideLoading()
//                    Tools.openSuccessErrorDialog(
//                        requireActivity(),
//                        response.message,
//                        "Failed",
//                        false,
//                        false
//                    )
//                }
//
//                else -> {}
//            }
//        }

        completeOrderViewModel.getAllCartItemResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Tools.showToast(requireContext(), response.data?.get(0)?.product?.productName)
                    showCartItems(response.data!!)
                    Log.d("size 1", response.data[0].product?.productName.toString())
                    Log.d("size 2", response.data[1].product?.productName.toString())
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
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

    private fun showCartItems(cartItems: List<CartItem>) {
        cartItemAdapter.submitList(cartItems)
    }

    private fun setupCartProductsAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        cartProductsRecyclerView.layoutManager = layoutManager
        cartProductsRecyclerView.adapter = cartItemAdapter

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompleteOrdersFragment().apply {

            }
    }
}