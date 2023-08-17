package com.tomer.anibadi.modal

import com.google.gson.annotations.SerializedName

data class WidgetMod(
    @SerializedName("name")
    val name: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("ic")
    val icon: Int,
    @SerializedName("mna")
    val Moname: String,
    @SerializedName("phno")
    val phno: String,
    @SerializedName("adno")
    val aadNo: String
)