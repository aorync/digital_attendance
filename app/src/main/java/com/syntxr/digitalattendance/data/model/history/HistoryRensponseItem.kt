package com.syntxr.digitalattendance.data.model.history


import com.google.gson.annotations.SerializedName

data class HistoryRensponseItem(
    @SerializedName("attendance_id")
    val attendanceId: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("in")
    val inX: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("out")
    val `out`: String,
    @SerializedName("image_url")
    val imgUrl : String,
    @SerializedName("latitude_in")
    val latitudeIn : Double,
    @SerializedName("longitude_in")
    val longitudeIn : Double,
    @SerializedName("latitude_out")
    val latitudeOut : Double,
    @SerializedName("longitude_out")
    val longitudeOut : Double,
    @SerializedName("users_id")
    val usersId: Int
)