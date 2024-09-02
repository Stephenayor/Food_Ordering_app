package com.example.yummy.core.user.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.admin.home.viewmodel.HomeViewModel
import com.example.yummy.core.user.home.adapter.UserHomeFragmentsProductAdapter
import com.example.yummy.data.repository.model.Product
import com.example.yummy.databinding.FragmentUserHomeBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import com.example.yummy.utils.dialogs.NotificationSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
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
    private lateinit var toolbar: Toolbar
    private lateinit var cartIcon: ImageView
    private var productsList: List<Product>? = null
    private lateinit var searchProductsEditText: EditText

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
        cartIcon = binding.cartIcon
        searchProductsEditText = binding.searchProducts

        homeViewModel.getAllProducts()
        subscribeToLiveData()
        setupProductsAdapter()

        binding.textLogout.setOnClickListener {
            Tools.showToast(requireContext(), "Log out!")
            firebaseAuth.signOut()
            requireActivity().finish()
        }

        setClickListeners()
    }

    private fun setClickListeners() {
        userHomeFragmentsProductAdapter.setOnProductClickListener(object :
            UserHomeFragmentsProductAdapter.OnProductClickListener {
            override fun onProductClick(product: Product) {
                // Handle the click event
                val action =
                    UserHomeFragmentDirections.actionNavigationUserHomeToProductDetailsFragment(
                        product
                    )
                findNavController().navigate(action)
            }
        })

        cartIcon.setOnClickListener {
            val action =
                UserHomeFragmentDirections.actionNavigationUserHomeToCompleteOrdersFragment(
                    null,
                    null
                )
            findNavController().navigate(action)
        }

        searchProductsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGiftCardCategoriesList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun showProducts(product: List<Product>?) {
        userHomeFragmentsProductAdapter.submitList((product))
    }

    private fun setupProductsAdapter() {
        val numberOfColumns = 3
        val layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        productsRecyclerView.layoutManager = layoutManager
        productsRecyclerView.adapter = userHomeFragmentsProductAdapter

    }

    private fun filterGiftCardCategoriesList(query: String) {
        val filteredProductsList = ArrayList<Product>()
        if (productsList?.isNotEmpty() == true) {
            for (product in productsList!!) {
                if (product.productName.lowercase(Locale.getDefault())
                        .contains(query.lowercase(Locale.getDefault()))
                ) {
                    filteredProductsList.add(product)
                }
            }
            updateProductsListBasedOnQuery(filteredProductsList)
        }
    }

    private fun updateProductsListBasedOnQuery(searchedProductsList: List<Product>) {
        userHomeFragmentsProductAdapter.submitList(searchedProductsList)
        userHomeFragmentsProductAdapter.notifyDataSetChanged()
    }

    private fun subscribeToLiveData() {
        homeViewModel.getAllAvailableProductsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.getting_product, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    productsList = response.data
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