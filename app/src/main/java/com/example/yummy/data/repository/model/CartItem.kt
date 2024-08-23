package com.example.yummy.data.repository.model


data class CartItem (
    var id: String? = null,
    var product: Product? = null,
    val userId: String="",
    val creationDate: Long = 0,
    var quantity: String = ""
){
    constructor(): this(null, null, "", 0, "")
}