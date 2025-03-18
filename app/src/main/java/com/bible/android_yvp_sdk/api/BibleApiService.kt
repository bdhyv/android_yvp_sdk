package com.bible.android_yvp_sdk.api

import com.bible.android_yvp_sdk.model.BibleVerseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BibleApiService {
    @GET("{reference}")
    fun getVerse(@Path("reference") reference: String): Call<BibleVerseResponse>
} 