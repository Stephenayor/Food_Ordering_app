package com.example.yummy.core.user.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.core.user.orders.adapter.UserOrdersAdapter
import com.example.yummy.core.user.orders.viewmodel.OrdersViewModel
import com.example.yummy.databinding.FragmentUserOrderBinding
import com.example.yummy.utils.AppConstants.CUSTOM_DATE
import com.example.yummy.utils.AppConstants.LAST_7_DAYS
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.example.yummy.utils.adapter.CustomSpinnerAdapter
import com.example.yummy.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.example.yummy.utils.AppConstants.SIMPLEDATEFORMATPATTERN
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import java.util.Calendar


@AndroidEntryPoint
class UserOrderFragment : BaseFragment<FragmentUserOrderBinding>() {
    private lateinit var binding: FragmentUserOrderBinding
    private lateinit var selectedDate: TextView
    private lateinit var datePicker: Button
    private var startDateInMillis: Long? = null
    private var endDateInMillis: Long? = null


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
        selectedDate = binding.selectedDate
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

        orderViewModel.filteredOrdersResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(R.string.please_wait_dots, R.string.please_wait_dots)
                }

                is Resource.Success -> {
                    hideLoading()
                    Log.d("FilterRe", response.data.toString())
                    userOrdersAdapter.submitList(response.data)
                    setupOrdersAdapter()
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
//                    orderViewModel.getAllOrders()
                }
            }
        })

        val spinner = binding.ordersFilterSpinner
        // Sample data for the dropdown
        val items = listOf("", "All Orders", "Last 7 days", "Custom date")
        val icons = listOf(R.drawable.octicon_filter_16, 0, 0, 0, 0)

        val adapter = CustomSpinnerAdapter(requireContext(), items, icons)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Get the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()
                when (selectedItem) {
                    CUSTOM_DATE -> startDatePickerDialog()

                    LAST_7_DAYS -> getLastSevenDaysOrders()

                    else -> {
                        selectedDate.text = ""
                        orderViewModel.getAllOrders()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected if needed
            }
        }

        // Create an ArrayAdapter using the custom layout for the spinner
//        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_selected_item, items)
//        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
    }

    private fun getLastSevenDaysOrders():  Pair<Long, Long>  {
        selectedDate.text = ""
        val calendar = Calendar.getInstance()
        // Current date as the end date (7th day)
        val endDateInMillis = calendar.timeInMillis
        // Subtract 6 days from the current date to get the start date (1st day)
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDateInMillis = calendar.timeInMillis
        orderViewModel.filterOrdersByDate(startDateInMillis, endDateInMillis)

        return Pair(startDateInMillis, endDateInMillis)
    }

    private fun startDatePickerDialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        val builder = Builder.dateRangePicker()

        builder.setTitleText("Select a date range")

        // Building the date picker dialog
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Retrieving the selected start and end dates
            val startDate = selection.first
            val endDate = selection.second

            // Formatting the selected dates as strings
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            // Creating the date range string
            val selectedDateRange = "$startDateString - $endDateString"

            // Displaying the selected date range in the TextView
            selectedDate.text = selectedDateRange

            startDateInMillis = convertDateToMillis(startDateString, SIMPLEDATEFORMATPATTERN)
            endDateInMillis = convertDateToMillis(endDateString, SIMPLEDATEFORMATPATTERN)
            orderViewModel.filterOrdersByDate(startDateInMillis!!, endDateInMillis!!)
        }

        // Showing the date picker dialog
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
    }

    private fun convertDateToMillis(dateString: String, pattern: String): Long {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date: Date? = sdf.parse(dateString)
            date?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L // Return 0 if parsing fails
        }
    }


    private fun setupOrdersAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        ordersRecyclerView.layoutManager = layoutManager
        ordersRecyclerView.adapter = userOrdersAdapter
        userOrdersAdapter.notifyDataSetChanged()

//        ordersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!recyclerView.canScrollVertically(1)) {
//                    orderViewModel.getAllOrders()
//                }
//            }
//        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserOrderFragment().apply {

            }
    }
}