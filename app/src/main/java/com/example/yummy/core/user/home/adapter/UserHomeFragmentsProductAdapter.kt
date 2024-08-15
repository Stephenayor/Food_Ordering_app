package com.example.yummy.core.user.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yummy.R
import com.example.yummy.data.repository.model.Product
import com.example.yummy.utils.Tools

class UserHomeFragmentsProductAdapter (private val context: Context
) : ListAdapter<Product, UserHomeFragmentsProductAdapter.UserProductsViewHolder>(CountryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProductsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_products_item, parent, false)
        return UserProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserProductsViewHolder, position: Int) {
        val product = getItem(position)
        Glide
            .with(context)
            .load(product?.productImage)
            .centerCrop()
            .into(holder.productImageView)
        holder.userProductNameTextView.text = product.productName
        holder.userPriceTextView.text = Tools.formatToCommaNaira(context, product.productPrice)
//        holder.itemView.setOnClickListener { clickListener(product) }
    }

    class UserProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProductNameTextView: TextView = itemView.findViewById(R.id.user_product_name_item_text)
        val userPriceTextView: TextView = itemView.findViewById(R.id.retail_product_price_text)
        val productImageView: ImageView = itemView.findViewById(R.id.product_image)
    }


    class CountryDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            // Compare IDs to determine if items are the same
            return oldItem.productName == newItem.productName
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // Compare the content of items
            return oldItem == newItem
        }
    }

}