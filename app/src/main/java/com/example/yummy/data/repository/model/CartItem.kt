package com.example.yummy.data.repository.model

import android.os.Parcel
import android.os.Parcelable


data class CartItem (
    var id: String? = null,
    var product: Product? = null,
    val userId: String="",
    val creationDate: Long = 0,
    var quantity: String = ""
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Product::class.java.classLoader),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString()
    ) {
    }

    constructor(): this(null, null, "", 0, "")

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}