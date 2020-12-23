package com.deckrog.qa_bert_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONArray

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        createUserButton.setOnClickListener {
            register()
        }
    }

    fun register() {
        val username = usernameRegisterField.text.toString()
        val email = emailRegisterField.text.toString()
        val password = passwordRegisterField.text.toString()

        when {
            username.isBlank() -> {
                showError(getString(R.string.empty_username))
                return
            }
            email.isBlank() -> {
                showError(getString(R.string.empty_email))
                return
            }
            password.isBlank() -> {
                showError(getString(R.string.empty_password))
                return
            }
        }
        sendPost(
            "auth/users/",
            "" to "",
            "username" to username,
            "password" to password,
            "email" to email
        )?.let { response ->
            when {
                response.has("id") -> succeed()
                response.has("username") -> showError(
                    (response["username"] as JSONArray).join("\n").filter { it != '\"' })

                response.has("email") -> showError(
                    (response["email"] as JSONArray).join("\n").filter { it != '\"' })

                response.has("password") ->
                    showError((response["password"] as JSONArray).join("\n").filter { it != '\"' })
            }
        }
    }

    fun showError(error: String) {
        registerErrorText.let {
            it.setTextColor(0xFFCC0000.toInt())
            it.text = error
        }
    }

    fun succeed() {
        registerErrorText.let {
            it.setTextColor(0xFF669900.toInt())
            it.text = getText(R.string.registration_succeed)
        }
    }
}