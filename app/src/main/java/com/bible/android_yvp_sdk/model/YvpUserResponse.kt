package com.bible.android_yvp_sdk.model

import com.google.gson.annotations.SerializedName

data class YvpUserResponse(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("id") val id: Long
) 