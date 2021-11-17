package com.example.gagyeboost.model.remote

import com.example.gagyeboost.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestWithHeaders = originRequest.newBuilder()
            .header("Accept-Language", getLanguage())
            .header("Authorization", "KakaoAK ${BuildConfig.kakao_api_key}")
            .build()

        return chain.proceed(requestWithHeaders)
    }

    private fun getLanguage() = Locale.getDefault().language
}
