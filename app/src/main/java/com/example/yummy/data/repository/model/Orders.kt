package com.example.yummy.data.repository.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Orders(
    val creationDate: Long = 0,
    val dateOfOrder: Long = 0,
    var id: String? = null,
    var product: Product? = null,
    var quantity: String = ""
) {
    constructor() : this(0, 0, null, null, "")
}
