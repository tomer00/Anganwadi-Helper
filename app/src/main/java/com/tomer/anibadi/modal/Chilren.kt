package com.tomer.anibadi.modal

import com.google.gson.annotations.SerializedName

data class Chilren(
    @SerializedName("name")
    val name: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("isb")
    val isBoy: Boolean
)
