package com.syntxr.digitalattendance.data.model.users

import com.google.gson.annotations.SerializedName

data class ImageProfile (
    @SerializedName("img_url")
    val imgUrl : String
        )