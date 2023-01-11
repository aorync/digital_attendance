package com.syntxr.digitalattendance.data.model.update

import com.google.gson.annotations.SerializedName

data class Update(
    @SerializedName("address")
    val address: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone_number")
    val phoneNumber: String
)
