package com.example.yummy.core.user.productdetails

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.yummy.R
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentProductDetailsBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding>() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var toolbar: Toolbar
    private lateinit var productImageView: ImageView
    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private var product: Product? = null
    private var quantity = 1
    private lateinit var quantityTextView: TextView
    private lateinit var plusProductQuantityButton: ImageButton
    private lateinit var minusProductQuantityButton: ImageButton
    private val productDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private var isProductInCart: Boolean = false

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    override fun getLayoutId(): Int = R.layout.fragment_product_details

    override fun getLayoutBinding(binding: FragmentProductDetailsBinding) {
        this.binding = binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productImageView = binding.productImage
        productNameTextView = binding.productDetailsNameText
        productPriceTextView = binding.productPriceText
        quantityTextView = binding.tvQuantity
        plusProductQuantityButton = binding.btnPlus
        minusProductQuantityButton = binding.btnMinus


        product = args.product!!
        toolbar = binding.toolbar
        initToolbar(requireActivity() as AppCompatActivity, toolbar)
        firebaseAuth.currentUser?.uid?.let { userId ->
            product?.productName?.let { productName ->
                productDetailsViewModel.getExistingItemInCart(
                    productName,
                    userId
                )
            }
        }
        displayProducts()
        setupClickListeners()
        subscribeToLiveData()
    }

    private fun setupClickListeners() {
        plusProductQuantityButton.setOnClickListener {
            if (quantity < 99) { // Maximum quantity limit
                quantity++
                updateQuantityText()
            }
        }

        minusProductQuantityButton.setOnClickListener {
            if (quantity > 1) { // Minimum quantity limit
                quantity--
                updateQuantityText()
            }
        }

        binding.btnAddToOrder.setOnClickListener {
            if (isProductInCart) {
                val action =
                    ProductDetailsFragmentDirections.actionProductDetailsFragmentToCompleteOrdersFragment(
                        null,
                        null
                    )
                findNavController().navigate(action)
            } else {
                product?.let { product1 ->
                    productDetailsViewModel.addProductsToCart(product1, quantity.toString())
                }
            }
        }

    }

    private fun updateQuantityText() {
        quantityTextView.text = quantity.toString()
    }

    private fun displayProducts() {
        Glide
            .with(requireContext())
            .load(product?.productImage)
            .centerCrop()
            .into(productImageView)
        productNameTextView.text = product?.productName
        productPriceTextView.text =
            product?.productPrice?.let { Tools.formatToCommaNaira(requireContext(), it) }
    }

    private fun subscribeToLiveData() {
        productDetailsViewModel.addProductToCartResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.adding_product, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    val action =
                        ProductDetailsFragmentDirections.actionProductDetailsFragmentToCompleteOrdersFragment(
                            product,
                            quantity.toString()
                        )
                    findNavController().navigate(action)
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

        productDetailsViewModel.isProductInCart.observe(viewLifecycleOwner) { response ->
            if (response) {
                isProductInCart = response
                binding.btnAddToOrder.text = "Continue to Cart"
            }
        }


        handleLoadingState()
    }

    private fun handleLoadingState() {
        productDetailsViewModel.getExistingItemsInCart.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.get_product_details, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                }

                is Resource.Error -> {
                    hideLoading()
                }

                else -> {}
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cart_icon -> {
                val action =
                    ProductDetailsFragmentDirections.actionProductDetailsFragmentToCompleteOrdersFragment(
                        null,
                        null
                    )
                findNavController().navigate(action)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailsFragment().apply {

            }
    }
}