package com.example.yummy.core.user.productdetails

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.yummy.R
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentProductDetailsBinding
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment

class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding>() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var toolbar: Toolbar
    private lateinit var productImageView: ImageView
    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView:TextView
    private  var product: Product? = null
    private var quantity = 1
    private lateinit var quantityTextView: TextView
    private lateinit var plusProductQuantityButton: ImageButton
    private lateinit var minusProductQuantityButton: ImageButton


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


        if (args.product != null) {
            product = args.product!!
        }

        toolbar = binding.toolbar
        initToolbar(requireActivity() as AppCompatActivity, toolbar)
        Tools.showToast(requireContext(), args.product?.productName)
        displayProducts()
        setupClickListeners()
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


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailsFragment().apply {

            }
    }
}