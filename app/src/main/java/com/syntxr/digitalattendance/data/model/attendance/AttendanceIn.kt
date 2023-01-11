package com.syntxr.digitalattendance.data.model.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceIn(
    @SerializedName("date")
    val date: String,

    @SerializedName("latitude_out")
    val latitudeOut : Double?,

    @SerializedName("longitude_out")
    val longitudeOut : Double?,

    @SerializedName("latitude_in")
    val latitudeIn : Double?,

    @SerializedName("longitude_in")
    val longitudeIn : Double?,

    @SerializedName("in")
    val inx: String,

    @SerializedName("out")
    val `out`: String?,

    @SerializedName("image_url")
    val imgUrl : String,

    @SerializedName("user_id")
    val userId: Int
)
