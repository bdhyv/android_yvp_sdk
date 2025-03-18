package com.bible.android_yvp_sdk.model

import com.google.gson.annotations.SerializedName

data class YvpAuthResponse(
    @SerializedName("lat") val token: String,
    @SerializedName("status") val status: String
) 