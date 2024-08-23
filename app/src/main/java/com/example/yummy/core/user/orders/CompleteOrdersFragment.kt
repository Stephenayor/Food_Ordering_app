package com.example.yummy.core.user.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.user.orders.adapter.CartItemAdapter
import com.example.yummy.core.user.orders.viewmodel.CompleteOrderViewModel
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.databinding.FragmentCompleteOrdersBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompleteOrdersFragment : BaseFragment<FragmentCompleteOrdersBinding>() {
    private lateinit var binding: FragmentCompleteOrdersBinding
    private val completeFragmentArgs by navArgs<CompleteOrdersFragmentArgs>()
    private lateinit var toolbar: Toolbar
    private val completeOrderViewModel by viewModels<CompleteOrderViewModel>()
    private var itemPosition: Int? = 0
    private var isUpdate: Boolean = false
    private lateinit var totalAmountText: TextView

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
        totalAmountText = binding.totalAmount

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
            val action =
                CompleteOrdersFragmentDirections.actionCompleteOrdersFragmentToNavigationUserHome()
            findNavController().navigate(action)
        }

        binding.deleteButton.setOnClickListener {
            completeOrderViewModel.deleteProductsInCart()
        }

        handleCartAdapterItemClickListener()
    }

    private fun handleCartAdapterItemClickListener() {
        cartItemAdapter.setOnProductClickListener(object :
            CartItemAdapter.OnCartItemProductClickListener {
            override fun onProductClick(product: CartItem, position: Int) {
                itemPosition = position
                val quantity = product.quantity.toInt()
                product.quantity = (quantity + 1).toString()
                completeOrderViewModel.updateProductsInCart(product)
            }

            override fun onMinusButtonProductClick(product: CartItem, position: Int) {
                itemPosition = position
                val quantity = product.quantity.toInt()
                product.quantity = (quantity - 1).toString()
                completeOrderViewModel.updateProductsInCart(product)
            }
        })
    }

    private fun subscribeToLiveData() {
        completeOrderViewModel.getAllCartItemResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showCartItems(response.data!!)
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

        completeOrderViewModel.updateProductInCartResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    isUpdate = response.data == true

                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Tools.showToast(requireContext(), "Please try Again")
                }

                else -> {}
            }
        }

        completeOrderViewModel.deleteProductInCartResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE

                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Tools.openSuccessErrorDialog(
                        requireActivity(),
                        "Unable to Delete Product, Please try Again",
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
        if (isUpdate) {
            itemPosition?.let { cartItemAdapter.notifyItemChanged(it) }
        }
        totalAmountText.text = Tools.formatToCommaNaira(
            requireContext(),
            calculateTotalAmount(cartItems)
        )
    }

    private fun calculateTotalAmount(cartItems: List<CartItem>): Double {
        return cartItems.sumOf { cartItem ->
            val price = cartItem.product?.productPrice ?: 0.0
            val quantity = cartItem.quantity.toDoubleOrNull() ?: 0.0
            price * quantity
        }
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