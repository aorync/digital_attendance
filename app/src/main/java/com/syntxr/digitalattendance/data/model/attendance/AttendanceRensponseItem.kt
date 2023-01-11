package com.syntxr.digitalattendance.data.model.attendance


import com.google.gson.annotations.SerializedName

data class AttendanceRensponseItem(
    @SerializedName("longitude_in")
    val longitude : Double,
    @SerializedName("latitude_in")
    val latitude : Double,
    @SerializedName("image_url")
    val imageUrl : String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("in")
    val inX: String,
    @SerializedName("out")
    val `out`: Any,
    @SerializedName("user_id")
    val userId: Int
)