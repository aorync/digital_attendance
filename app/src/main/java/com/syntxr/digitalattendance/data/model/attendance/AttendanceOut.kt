package com.syntxr.digitalattendance.data.model.attendance

import com.google.gson.annotations.SerializedName

data class AttendanceOut(
    @SerializedName("out")
    val `out`: String,

    @SerializedName("latitude_out")
    val latitude : Double?,

    @SerializedName("longitude_out")
    val longitude : Double?

)
