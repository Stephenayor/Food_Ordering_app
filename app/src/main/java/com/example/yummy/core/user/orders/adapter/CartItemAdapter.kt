package com.example.yummy.core.user.orders.adapter

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
import com.example.yummy.data.repository.model.CartItem
import com.example.yummy.utils.Tools

class CartItemAdapter(
    private val context: Context
) : ListAdapter<CartItem, CartItemAdapter.CartItemViewHolder>(CartItemDiffCallback()) {

    private var onProductClickListener: OnCartItemProductClickListener? = null

    fun setOnProductClickListener(listener: OnCartItemProductClickListener) {
        this.onProductClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = getItem(position)
        val product = getItem(position)?.product
        Glide
            .with(context)
            .load(cartItem?.product?.productImage)
            .centerCrop()
            .into(holder.cartItemImageView)
        holder.cartProductText.text = cartItem?.product?.productName
        holder.itemPrice.text =
            cartItem?.product?.productPrice?.let { Tools.formatToCommaNaira(context, it) }
        holder.itemQuantityText.text = cartItem?.quantity
        holder.minusButton.setOnClickListener {
            onProductClickListener?.onMinusButtonProductClick(cartItem, position)
        }
        holder.plusButton.setOnClickListener {
            if (cartItem != null) {
                onProductClickListener?.onProductClick(cartItem, position)
            }
        }
    }

    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartItemImageView: ImageView = itemView.findViewById(R.id.cart_iv_logo)
        val cartProductText: TextView = itemView.findViewById(R.id.tv_cart_product_name)
        val itemPrice: TextView = itemView.findViewById(R.id.tv_price)
        val minusButton: ImageView = itemView.findViewById(R.id.cart_btn_minus)
        val plusButton: ImageView = itemView.findViewById(R.id.cart_btn_plus)
        val itemQuantityText: TextView = itemView.findViewById(R.id.tv_cart_item_quantity)
    }


    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Compare IDs to determine if items are the same
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Compare the content of items
            return oldItem == newItem
        }


    }

    interface OnCartItemProductClickListener {
        fun onProductClick(product: CartItem, position: Int)
        fun onMinusButtonProductClick(product: CartItem, position: Int)
    }


}
