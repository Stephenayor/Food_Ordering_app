package com.example.yummy.data.repository.model

import android.os.Parcel
import android.os.Parcelable


data class Orders(
    val creationDate: Long = 0,
    val dateOfOrder: Long = 0,
    var id: String? = null,
    var product: Product? = null,
    var quantity: String = ""
) {
    constructor() : this(0, 0, null, null, "")
}
