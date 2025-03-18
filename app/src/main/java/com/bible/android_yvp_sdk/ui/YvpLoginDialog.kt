package com.bible.android_yvp_sdk.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bible.android_yvp_sdk.R
import com.bible.android_yvp_sdk.repository.YvpBibleVerseRepository
import com.google.android.material.textfield.TextInputEditText

/**
 * A dialog for handling YouVersion authentication.
 */
class YvpLoginDialog(
    context: Context,
    private val repository: YvpBibleVerseRepository,
    private val onLoginSuccess: (String) -> Unit
) : Dialog(context) {

    companion object {
        private const val TAG = "YvpLoginDialog"
    }

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.yvp_login_dialog, null)
        setContentView(dialogView)

        etUsername = dialogView.findViewById(R.id.etUsername)
        etPassword = dialogView.findViewById(R.id.etPassword)
        btnLogin = dialogView.findViewById(R.id.btnLogin)
        btnCancel = dialogView.findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        Log.d(TAG, "Attempting login for user: $username")

        repository.authenticate(username, password, object : YvpBibleVerseRepository.AuthCallback {
            override fun onSuccess(token: String) {
                Log.d(TAG, "Login successful")
                btnLogin.isEnabled = true
                dismiss()
                onLoginSuccess(token)
            }

            override fun onError(message: String) {
                Log.e(TAG, "Login failed: $message")
                btnLogin.isEnabled = true
                Toast.makeText(context, "Login failed: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 