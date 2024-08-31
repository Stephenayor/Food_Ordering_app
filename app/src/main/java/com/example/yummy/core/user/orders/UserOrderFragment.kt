package com.example.yummy.core.user.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.user.orders.adapter.CartItemAdapter
import com.example.yummy.core.user.orders.adapter.UserOrdersAdapter
import com.example.yummy.core.user.orders.viewmodel.OrdersViewModel
import com.example.yummy.databinding.FragmentUserOrderBinding
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserOrderFragment : BaseFragment<FragmentUserOrderBinding>() {
    private lateinit var binding: FragmentUserOrderBinding
    @Inject
    lateinit var userOrdersAdapter: UserOrdersAdapter
    private lateinit var ordersRecyclerView: RecyclerView
    private val orderViewModel by viewModels<OrdersViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int = R.layout.fragment_user_order

    override fun getLayoutBinding(binding: FragmentUserOrderBinding) {
        this.binding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ordersRecyclerView = binding.rvMyOrders
        setupOrdersAdapter()
        orderViewModel.getAllOrders(true)
        setupClickListeners()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        orderViewModel.ordersResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.please_wait_dots, R.string.loading_orders)
                }

                is Resource.Success -> {
                    hideLoading()
                    userOrdersAdapter.submitList(response.data)
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

    private fun setupClickListeners() {
        // Implement pagination when reaching the end of the list
        ordersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    // Reached the end of the list
                    orderViewModel.getAllOrders()
                }
            }
        })
    }

    private fun setupOrdersAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        ordersRecyclerView.layoutManager = layoutManager
        ordersRecyclerView.adapter = userOrdersAdapter
        ordersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    orderViewModel.getAllOrders()
                }
            }
        })


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserOrderFragment().apply {

            }
    }
}