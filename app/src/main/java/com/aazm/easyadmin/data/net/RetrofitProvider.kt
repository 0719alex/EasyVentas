package com.aazm.easyadmin.data.net

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AuthTokenStore { @Volatile var token: String? = null }

class BearerInterceptor(private val store: AuthTokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val t = store.token
        val base = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
        if (!t.isNullOrBlank()) {
            base.addHeader("Authorization", "Bearer $t")
        }
        return chain.proceed(base.build())
    }
}

object RetrofitProvider {
    private val store = AuthTokenStore()
    fun tokenStore(): AuthTokenStore = store

    fun fmRetrofit(): Retrofit {
        val ok = InsecureHttp.createTrustAllClient().newBuilder()
            .addInterceptor(BearerInterceptor(store))
            .build()

        return Retrofit.Builder()
            .baseUrl("${FmConfig.FM_HOST}/fmi/data/vLatest/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(ok)
            .build()
    }
}
