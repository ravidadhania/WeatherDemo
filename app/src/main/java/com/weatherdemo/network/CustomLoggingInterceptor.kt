package com.weatherdemo.network;

import com.weatherdemo.utils.LogUtils
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.Companion.INFO
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class CustomLoggingInterceptor @JvmOverloads constructor(private val logger: Logger = Logger.DEFAULT) : Interceptor {

    @Volatile
    private var level = Level.NONE

    enum class Level { NONE, HEADERS, BODY }

    interface Logger {
        fun log(message: String)

        companion object {

            val DEFAULT: Logger = object : Logger {
                override fun log(message: String) {
                    Platform.get().log( message,INFO, null)
                }
            }
        }
    }

    fun setLevel(level: Level?): CustomLoggingInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    fun getLevel(): Level {
        return level
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body
        val hasRequestBody = requestBody != null

        LogUtils.i("OKHTTP_REQUEST_URL", request.url.toString())

        if (logHeaders) {
            val headers = request.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                    LogUtils.i("OKHTTP_REQUEST_HEADER", name + ": " + headers.value(i))
                }
                i++
            }

            if (!logBody || !hasRequestBody) {
                // nothing
            } else if (bodyEncoded(request.headers)) {
                // nothing
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset: Charset? = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                if (isPlaintext(buffer)) {
                    LogUtils.i("OKHTTP_REQUEST_BODY", buffer.readString(charset!!))
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            LogUtils.i("OKHTTP_RESPONSE_ERROR", e.toString())
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()

        LogUtils.i("OKHTTP_RESPONSE_STATUS", response.code.toString() + " " + response.message + " " + response.request.url + " (" + tookMs + "ms)")

        if (logHeaders) {


            if (!logBody) {
                // nothing
            } else if (bodyEncoded(response.headers)) {
                // nothing
            } else {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset? = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                if (!isPlaintext(buffer)) {
                    return response
                }

                if (contentLength != 0L) {
                    LogUtils.i("OKHTTP_RESPONSE_BODY", buffer.clone().readString(charset!!))
                }
            }
        }

        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false // Truncated UTF-8 sequence.
        }

    }

    companion object {

        private val UTF8 = Charset.forName("UTF-8")
    }
}
