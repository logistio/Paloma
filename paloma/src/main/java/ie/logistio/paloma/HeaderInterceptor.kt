package ie.logistio.paloma

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 */
class HeaderInterceptor : Interceptor {

    private val headerBuilder = Headers.Builder()


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .headers(headerBuilder.build())
                .build()

        return chain.proceed(request)
    }

    fun appendHeader(name: String, value: String) {
        headerBuilder.set(name, value)
    }
}