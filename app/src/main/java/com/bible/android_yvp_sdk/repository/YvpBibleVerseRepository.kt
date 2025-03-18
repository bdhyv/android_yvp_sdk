package com.bible.android_yvp_sdk.repository

import android.util.Log
import com.bible.android_yvp_sdk.api.YvpApiService
import com.bible.android_yvp_sdk.model.YvpAuthResponse
import com.bible.android_yvp_sdk.model.YvpBibleVerseResponse
import com.bible.android_yvp_sdk.model.YvpUserResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for handling authentication and fetching Bible verses from the YVP API.
 */
class YvpBibleVerseRepository {
    companion object {
        private const val TAG = "YvpBibleVerseRepository"
        private const val BASE_URL = "https://biblesdk-web-890431326916.us-central1.run.app/"
        // Static token for fetching verses
        private const val STATIC_TOKEN = "TDi7NrW8uwy2puh0mVLLMmwt2qTXluhk"
    }

    private val apiService: YvpApiService
    var authToken: String? = null
        private set
    
    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
            
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        apiService = retrofit.create(YvpApiService::class.java)
    }
    
    /**
     * Authenticate with the YVP service.
     *
     * @param username The user's email address
     * @param password The user's password
     * @param callback The callback to receive the result
     */
    fun authenticate(username: String, password: String, callback: AuthCallback) {
        Log.d(TAG, "Authenticating user: $username")
        
        apiService.authenticate(username, password).enqueue(object : Callback<YvpAuthResponse> {
            override fun onResponse(call: Call<YvpAuthResponse>, response: Response<YvpAuthResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Authentication successful: ${it.status}")
                        authToken = it.token
                        callback.onSuccess(it.token)
                    } ?: run {
                        Log.e(TAG, "Authentication response body is null")
                        callback.onError("Authentication failed: Empty response")
                    }
                } else {
                    Log.e(TAG, "Authentication failed: ${response.code()}")
                    callback.onError("Authentication failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<YvpAuthResponse>, t: Throwable) {
                Log.e(TAG, "Authentication network failure", t)
                callback.onError("Network error: ${t.message}")
            }
        })
    }
    
    /**
     * Get user information from the YouVersion API.
     *
     * @param token The authentication token
     * @param callback The callback to receive the user info
     */
    fun getUserInfo(token: String, callback: UserInfoCallback) {
        Log.d(TAG, "Getting user info with token")
        
        apiService.getUserInfo(token).enqueue(object : Callback<YvpUserResponse> {
            override fun onResponse(call: Call<YvpUserResponse>, response: Response<YvpUserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "User info fetched successfully: ${it.firstName} ${it.lastName}")
                        callback.onSuccess(it)
                    } ?: run {
                        Log.e(TAG, "User info response body is null")
                        callback.onError("Failed to get user info: Empty response")
                    }
                } else {
                    Log.e(TAG, "Failed to get user info: ${response.code()}")
                    callback.onError("Failed to get user info: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<YvpUserResponse>, t: Throwable) {
                Log.e(TAG, "User info network failure", t)
                callback.onError("Network error: ${t.message}")
            }
        })
    }
    
    /**
     * Get verse of the day using the static token.
     * This doesn't require authentication.
     *
     * @param translationId The translation ID to use
     * @param callback The callback to receive the result
     */
    fun getVerseOfTheDayWithStaticToken(translationId: Int = 111, callback: VerseCallback) {
        Log.d(TAG, "Fetching verse of the day with static token")
        
        apiService.getVerseOfTheDay(STATIC_TOKEN, translationId).enqueue(object : Callback<YvpBibleVerseResponse> {
            override fun onResponse(call: Call<YvpBibleVerseResponse>, response: Response<YvpBibleVerseResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Verse fetched successfully with static token: ${it.usfm}")
                        callback.onSuccess(it)
                    } ?: run {
                        Log.e(TAG, "Verse response body is null")
                        callback.onError("Failed to get verse: Empty response")
                    }
                } else {
                    Log.e(TAG, "Failed to get verse: ${response.code()}")
                    callback.onError("Failed to get verse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<YvpBibleVerseResponse>, t: Throwable) {
                Log.e(TAG, "Verse network failure", t)
                callback.onError("Network error: ${t.message}")
            }
        })
    }
    
    /**
     * Fetch the verse of the day using the stored authentication token.
     *
     * @param translationId The ID of the translation to use
     * @param callback The callback to receive the result
     */
    fun getVerseOfTheDay(translationId: Int = 111, callback: VerseCallback) {
        val token = authToken
        if (token == null) {
            Log.e(TAG, "Cannot get verse: Not authenticated")
            callback.onError("Not authenticated")
            return
        }
        
        Log.d(TAG, "Fetching verse of the day with token: $token")
        
        apiService.getVerseOfTheDay(token, translationId).enqueue(object : Callback<YvpBibleVerseResponse> {
            override fun onResponse(call: Call<YvpBibleVerseResponse>, response: Response<YvpBibleVerseResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Verse fetched successfully: ${it.usfm}")
                        callback.onSuccess(it)
                    } ?: run {
                        Log.e(TAG, "Verse response body is null")
                        callback.onError("Failed to get verse: Empty response")
                    }
                } else {
                    Log.e(TAG, "Failed to get verse: ${response.code()}")
                    callback.onError("Failed to get verse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<YvpBibleVerseResponse>, t: Throwable) {
                Log.e(TAG, "Verse network failure", t)
                callback.onError("Network error: ${t.message}")
            }
        })
    }
    
    /**
     * Check if the repository has a valid authentication token.
     *
     * @return True if authenticated, false otherwise
     */
    fun isAuthenticated(): Boolean {
        return authToken != null
    }
    
    /**
     * Clear the current authentication token.
     */
    fun logout() {
        authToken = null
    }
    
    /**
     * Callback for authentication operations.
     */
    interface AuthCallback {
        fun onSuccess(token: String)
        fun onError(message: String)
    }
    
    /**
     * Callback for verse operations.
     */
    interface VerseCallback {
        fun onSuccess(verse: YvpBibleVerseResponse)
        fun onError(message: String)
    }
    
    /**
     * Callback for user info operations.
     */
    interface UserInfoCallback {
        fun onSuccess(userInfo: YvpUserResponse)
        fun onError(message: String)
    }
} 