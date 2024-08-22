package com.example.yummy.data.repository.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep


data class Product(
    val productImage: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productImage)
        parcel.writeString(productName)
        parcel.writeDouble(productPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
