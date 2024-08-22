package com.example.yummy.data.repository.model


data class CartItem (
    var product: Product? = null,
    val userId: String="",
    val creationDate: Long = 0,
    val quantity: String = ""
){
    constructor(): this(null, "", 0, "")
}