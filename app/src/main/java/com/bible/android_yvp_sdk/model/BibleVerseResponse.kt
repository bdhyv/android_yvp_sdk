package com.bible.android_yvp_sdk.model

import com.google.gson.annotations.SerializedName

data class BibleVerseResponse(
    @SerializedName("reference") val reference: String,
    @SerializedName("verses") val verses: List<Verse>,
    @SerializedName("text") val text: String,
    @SerializedName("translation_id") val translationId: String,
    @SerializedName("translation_name") val translationName: String,
    @SerializedName("translation_note") val translationNote: String
) {
    data class Verse(
        @SerializedName("book_id") val bookId: String,
        @SerializedName("book_name") val bookName: String,
        @SerializedName("chapter") val chapter: Int,
        @SerializedName("verse") val verse: Int,
        @SerializedName("text") val text: String
    )
} 