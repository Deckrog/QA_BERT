package com.deckrog.qa_bert_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_qa.*

class QAActivity : AppCompatActivity() {
    lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qa)
        token = intent.extras?.get("token")!! as String
        askButton.setOnClickListener {
            ask()
        }

    }

    override fun onStop() {
        super.onStop()
        logout(token)
    }

    fun ask() {
        val question = messageField.text.toString()
        if (question.isBlank())
            return
        sendPost(
            "ask/",
            "Authorization" to "Token $token",
            "question" to question
        )?.let { response ->
            answerText.text = (response["answer"] as String)
        }
    }
}