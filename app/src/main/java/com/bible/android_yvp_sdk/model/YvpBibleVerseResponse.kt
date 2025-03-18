package com.bible.android_yvp_sdk.model

import com.google.gson.annotations.SerializedName

data class YvpBibleVerseResponse(
    @SerializedName("text") val text: String,
    @SerializedName("translationId") val translationId: Int,
    @SerializedName("usfm") val usfm: String
) 