package com.syntxr.digitalattendance.data.model.attendance

import com.google.gson.annotations.SerializedName

data class ImageAttendance(
    @SerializedName("image_url")
    val imageUrl: String?
)