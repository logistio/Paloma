package ie.logistio.paloma

import com.google.gson.Gson
import ie.logistio.paloma.json.AuthCredentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 */
class ApiService(
        private val gson: Gson,
        private val baseUrl: String,
        private val logger: HttpLoggingInterceptor? = null,
        private val mockerRegistry: MockApiRegistry? = null
) {

    private val headerInterceptor = HeaderInterceptor()
    private val okHttpClient: OkHttpClient
    private val retrofit: Retrofit

    init {
        okHttpClient = setupOkHttp()

        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .client(okHttpClient)
                // GsonConverterFactory must always be the last ConverterFactory registered.
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    private fun setupOkHttp(): OkHttpClient {

        val builder = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)

        if (logger != null) {
            // Add the logging interceptor to OkHttp:
            builder.addInterceptor(logger)

            /*
            // Add verbose logging to all requests:
            val loggingInterceptor = HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message ->
                        Timber.d("[ApiService] OkHttp:" + message)
                    })


            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            */

        }

        return builder.build()
    }

    //----------------------------------------------------------------------------------------------

    fun <T> createApi(apiType: Class<T>): T {
        if (true == mockerRegistry?.isRegistered(apiType)) {
            return mockerRegistry.find(apiType)
        }
        else {
            return retrofit.create(apiType)
        }
    }

    fun setupAuth(auth: AuthCredentials) {
        headerInterceptor.appendHeader("Authorization", "${auth.tokenType} ${auth.accessToken}")
    }

}