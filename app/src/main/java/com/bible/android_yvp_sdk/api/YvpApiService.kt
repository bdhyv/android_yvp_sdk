package com.bible.android_yvp_sdk.api

import com.bible.android_yvp_sdk.model.YvpAuthResponse
import com.bible.android_yvp_sdk.model.YvpBibleVerseResponse
import com.bible.android_yvp_sdk.model.YvpUserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface YvpApiService {
    @FormUrlEncoded
    @POST("auth/setup")
    fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<YvpAuthResponse>
    
    @GET("votd/today")
    fun getVerseOfTheDay(
        @Query("lat") token: String,
        @Query("translationId") translationId: Int = 111
    ): Call<YvpBibleVerseResponse>
    
    @GET("auth/me")
    fun getUserInfo(
        @Query("lat") token: String,
        @Query("translationId") translationId: Int = 111
    ): Call<YvpUserResponse>
} 