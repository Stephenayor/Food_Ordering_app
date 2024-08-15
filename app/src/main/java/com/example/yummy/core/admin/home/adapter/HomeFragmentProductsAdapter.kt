package com.example.yummy.core.admin.home.adapter

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


class HomeFragmentProductsAdapter(
    private val context: Context,
//    private val clickListener: (Product) -> Unit
) :
    ListAdapter<Product, HomeFragmentProductsAdapter.ProductsViewHolder>(CountryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.home_products_item, parent, false)
        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = getItem(position)
        Glide
            .with(context)
            .load(product?.productImage)
            .centerCrop()
            .into(holder.productImageView)
        holder.nameTextView.text = product.productName
        holder.priceTextView.text = Tools.formatToCommaNaira(context, product.productPrice)
//        holder.itemView.setOnClickListener { clickListener(product) }
    }

    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.product_name_item_text)
        val priceTextView: TextView = itemView.findViewById(R.id.product_price_text)
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