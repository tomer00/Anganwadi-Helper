package com.tomer.anibadi.modal

import com.google.gson.annotations.SerializedName

data class Mother(
    @SerializedName("ID")
    val ID: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("hname")
    val hName: String,
    @SerializedName("phno")
    val phno: String,
    @SerializedName("addno")
    val aadNo: String,
    @SerializedName("dol")
    val doLastP: String,
    @SerializedName("dolp")
    val doLaDeli: String,
    @SerializedName("isp")
    val isPreg: Boolean,
    @SerializedName("isl")
    val isLactating: Boolean,
    @SerializedName("childs")
    val childs: List<Chilren>
)
