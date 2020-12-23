package com.deckrog.qa_bert_client

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        SelfSignedTruster.trust()
        loginButton.setOnClickListener {
            logIn()
        }
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    fun logIn() {
        val username = usernameField.text.toString()
        val password = passwordField.text.toString()
        when {
            username.isBlank() -> {
                showError(getString(R.string.empty_username))
                return
            }
            password.isBlank() -> {
                showError(getString(R.string.empty_password))
                return
            }
        }
        sendPost(
            "auth/token/login",
            "Content-type" to "application/json",
            "username" to usernameField.text.toString(),
            "password" to passwordField.text.toString()
        )?.let { response ->
            if (response.has("auth_token")) {
                showError("")
                startActivity(Intent(this, QAActivity::class.java).apply {
                    putExtra("token", response["auth_token"] as String)
                })
            } else if (response.has("non_field_errors"))
                showError(getString(R.string.wrong_username_or_password))
        }
    }

    fun showError(error: String) {
        loginErrorText.let {
            it.setTextColor(0xFFCC0000.toInt())
            it.text = error
        }
    }
}