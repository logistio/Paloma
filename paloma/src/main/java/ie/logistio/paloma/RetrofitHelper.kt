package ie.logistio.paloma

import java.io.IOException

/**
 *
 */
object RetrofitHelper {

    fun isNetworkError(retrofitError: Throwable) = retrofitError is IOException

}