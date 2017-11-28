package ie.logistio.paloma

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ie.logistio.paloma.adapters.InstantJsonAdapter
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.Instant


/**
 *
 */
class ApiServiceBuilder {

    private val timeConveter = MutableInstantConverter()

    private val dateTimeAdapter = InstantJsonAdapter(timeConveter)

    private var baseUrl: String = "http://localhost/"

    private var gson = buildDefaultGson()

    private var logger: HttpLoggingInterceptor? = null

    private var mockerRegistry: MockApiRegistry? = null

    fun setBaseUrl(domainUrl: String): ApiServiceBuilder {
        baseUrl = domainUrl
        return this
    }

    fun buildDefaultGson(): Gson {
        return GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Instant::class.java, dateTimeAdapter)
                .create()
    }

    /**
     * Sets the global time converter used by [DateTime]
     */
    fun setTimeConverter(instantConverter: InstantJsonAdapter.InstantConverter): ApiServiceBuilder {
        timeConveter.converter = instantConverter
        return this
    }

    fun setLogger(loggingInterceptor: HttpLoggingInterceptor): ApiServiceBuilder {
        logger = loggingInterceptor
        return this
    }

    fun setMockerRegistry(mockApiRegistry: MockApiRegistry): ApiServiceBuilder {
        mockerRegistry = mockApiRegistry
        return this
    }

    /**
     * Add verbose logging to all requests.
     */
    fun setupVerboseLogger(logHandler: (String) -> Unit): ApiServiceBuilder {

        val loggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message ->
                    logHandler.invoke("OkHttp:$message")
                })

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        setLogger(loggingInterceptor)
        return this
    }


    /**
     * Builds an [ApiService] with the provided setup.
     */
    fun buildApiService(): ApiService {
        return ApiService(
                gson = gson,
                baseUrl = baseUrl,
                logger = logger,
                mockerRegistry = this.mockerRegistry
        )
    }

    //----------------------------------------------------------------------------------------------
    /**
     * Holds a reference to an InstantConverter
     */
    private class MutableInstantConverter : InstantJsonAdapter.InstantConverter {

        var converter: InstantJsonAdapter.InstantConverter? = null

        override fun convertFromInstantToTimestamp(instant: Instant): String {
            val currentConverter = converter

            if (currentConverter == null) {
                throwTimeConverterNotSetException()
            }

            return currentConverter.convertFromInstantToTimestamp(instant)
        }

        override fun convertFromTimestampToInstant(timestamp: String): Instant {
            val currentConverter = converter

            if (currentConverter == null) {
                throwTimeConverterNotSetException()
            }

            return currentConverter.convertFromTimestampToInstant(timestamp)
        }

        private fun throwTimeConverterNotSetException(): Nothing {
            throw IllegalStateException("Global MutableInstantConverter for ApiServiceBuilder is not set.")
        }
    }

    public interface LogHandler {
        fun logMessage(message: String)
    }
}