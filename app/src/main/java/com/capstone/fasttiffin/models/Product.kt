package com.capstone.fasttiffin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
        val user_id: String = "",
        val user_name: String = "",
        val title: String = "",
        var price: String = "",
        val description: String = "",
        val stock_quantity: String = "",
        val image: String = "",
        var product_id: String = ""
): Parcelable
