package com.tomer.anibadi.modal

import com.google.gson.annotations.SerializedName

data class MotherRv(
    @SerializedName("ID")
    val ID: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("bg")
    val bgColor: Int,
    @SerializedName("ip")
    val isPreg: Boolean,
    @SerializedName("ik")
    val isLacta: Boolean,
    @SerializedName("dof")
    val dps: String
)
