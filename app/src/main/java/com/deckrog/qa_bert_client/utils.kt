package com.deckrog.qa_bert_client

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

fun sendPost(
    urlTail: String,
    header: Pair<String, String>,
    vararg pairs: Pair<String, Any>
): JSONObject? {
    class AsyncRequest :
        AsyncTask<String?, Int?, String>() {
        private fun sendPost(str: String): String {
            val url = URL("https://25.109.57.183:8000/$urlTail")
            val urlConnection = url.openConnection() as HttpsURLConnection
//            Log.e("sending:", str)
            if (header.first.isNotBlank())
                urlConnection.setRequestProperty(header.first, header.second)
            urlConnection.setRequestProperty("Content-type", "application/json")
            var answer = ""
            try {
                urlConnection.doOutput = true
                urlConnection.doInput = true
                val out = BufferedOutputStream(urlConnection.outputStream)
                val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
                writer.write(str)
                writer.flush()
                writer.close()
                out.close()
                urlConnection.connect()
                val input = if (urlConnection.responseCode / 100 == 2)
                    urlConnection.inputStream else urlConnection.errorStream
                val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
                answer = reader.readText()
                reader.close()
                input.close()
            } finally {
                urlConnection.disconnect()
            }
//            Log.e("RESPONSE: ", answer)
            return answer
        }

        override fun doInBackground(vararg arg: String?): String {
            return sendPost(jsonObject(*pairs).toString())
        }
    }

    val answer = AsyncRequest().execute().get()
    return if (answer.startsWith('{') && answer.endsWith('}') && answer.length > 2)
        JSONObject(answer) else null
}

private fun jsonObject(vararg pairs: Pair<String, Any>): JSONObject {
    return JSONObject(pairs.toMap())
}

fun logout(token: String) {
    sendPost(
        "auth/token/logout",
        "Authorization" to "Token $token"
    )
}

class SelfSignedTruster : TrustManager, X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(xcs: Array<X509Certificate>, string: String) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(xcs: Array<X509Certificate>, string: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }

    companion object {
        fun trust() {
            val trustAllCerts = arrayOf<TrustManager>(AllCertificatesAndHostsTruster())
            try {
                val context = SSLContext.getInstance("SSL")
                context.init(null, trustAllCerts, SecureRandom())
                HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
                HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
            } catch (e: Exception) {
                Log.e(
                    "Truster", "Unable to initialize the Trust Manager to trust all the "
                            + "SSL certificates and HTTPS hosts.", e
                )
            }
        }
    }
}
