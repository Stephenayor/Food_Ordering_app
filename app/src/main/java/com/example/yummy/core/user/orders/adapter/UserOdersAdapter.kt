package com.example.yummy.core.user.orders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.yummy.R
import com.example.yummy.data.repository.model.Orders
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class UserOrdersAdapter(
//    private val context: Context
//) : ListAdapter<Orders, UserOrdersAdapter.UserOrdersViewHolder>(OrderDiffCallBack()) {
//    private var currentDate: Long = 0
//    private var currentDateAsString: String? = null
//    private var lastdate = "133333".toLong()
//
//
//    class UserOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var detailsTextView: TextView
//        var orderName: TextView
//        var timeTextView: TextView
//        var dateHeaderTextView: TextView
//        var senderAccountNumberTextView: TextView? = null
//        var dayConstraintLayout: ConstraintLayout
//
//        init {
//            dateHeaderTextView = itemView.findViewById(R.id.date__header_tv)
//            detailsTextView = itemView.findViewById(R.id.description)
//            orderName = itemView.findViewById(R.id.user_order_product_name_tv)
//            timeTextView = itemView.findViewById(R.id.time_tv)
//            dayConstraintLayout = itemView.findViewById(R.id.item_bbg_date_header)
//        }
//    }
//
//    class OrderDiffCallBack : DiffUtil.ItemCallback<Orders>() {
//        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
//            // Compare IDs to determine if items are the same
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
//            // Compare the content of items
//            return oldItem == newItem
//        }
//
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrdersViewHolder {
//        val itemView =
//            LayoutInflater.from(parent.context).inflate(R.layout.user_orders_item, parent, false)
//        return UserOrdersViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: UserOrdersViewHolder, position: Int) {
//        val order = getItem(position)
//        val orderDate = order.dateOfOrder
//
//        currentDate = order.dateOfOrder
//        currentDateAsString = java.lang.String.valueOf(orderDate)
//        showHideDateHeaderTextView(holder, orderDate)
//        holder.orderName.text = order.product?.productName
//        holder.detailsTextView.text = "Quantity: " + order.quantity
//    }
//
//    private fun showHideDateHeaderTextView(
//        holder: UserOrdersViewHolder,
//        orderDate: Long
//    ) {
//        if (currentDate != lastdate && currentDateAsString!!.length > 1 &&
//            convertToStandardDateFormat(currentDate) != convertToStandardDateFormat(lastdate)
//        ) {
//            holder.dayConstraintLayout.visibility = View.VISIBLE
//            val dateInMillis: Long = orderDate
//            lastdate = orderDate
//            holder.dateHeaderTextView.text = convertToStandardDateFormat(dateInMillis)
//        } else {
//            holder.dayConstraintLayout.visibility = View.GONE
//        }
//    }
//
//    private fun convertToStandardDateFormat(dateInMillis: Long): String {
//        val dateFormat = SimpleDateFormat("dd MMM, yyyy")
//        // Convert the long value to a Date object
//        val date = Date(dateInMillis)
//        return dateFormat.format(date)
//    }
//
//}

class UserOrdersAdapter(
    private val context: Context
) : ListAdapter<Orders, UserOrdersAdapter.UserOrdersViewHolder>(OrderDiffCallBack()) {

    class UserOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val detailsTextView: TextView = itemView.findViewById(R.id.description)
        val orderName: TextView = itemView.findViewById(R.id.user_order_product_name_tv)
        val timeTextView: TextView = itemView.findViewById(R.id.time_tv)
        val dateHeaderTextView: TextView = itemView.findViewById(R.id.date__header_tv)
        val dayConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.item_bbg_date_header)
    }

    class OrderDiffCallBack : DiffUtil.ItemCallback<Orders>() {
        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOrdersViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_orders_item, parent, false)
        return UserOrdersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserOrdersViewHolder, position: Int) {
        val order = getItem(position)
        val orderDate = order.dateOfOrder

        val previousOrderDate = if (position > 0) getItem(position - 1).dateOfOrder else null

        showHideDateHeaderTextView(holder, orderDate, previousOrderDate)
        holder.orderName.text = order.product?.productName
        holder.detailsTextView.text = "Quantity: ${order.quantity}"
    }

    private fun showHideDateHeaderTextView(
        holder: UserOrdersViewHolder,
        orderDate: Long,
        previousOrderDate: Long?
    ) {
        if (previousOrderDate == null || convertToStandardDateFormat(orderDate) != convertToStandardDateFormat(previousOrderDate)) {
            holder.dayConstraintLayout.visibility = View.VISIBLE
            holder.dateHeaderTextView.text = convertToStandardDateFormat(orderDate)
        } else {
            holder.dayConstraintLayout.visibility = View.GONE
        }
    }

    private fun convertToStandardDateFormat(dateInMillis: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return dateFormat.format(Date(dateInMillis))
    }


}
