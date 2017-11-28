package ie.logistio.paloma.mock

import android.os.AsyncTask
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor


/**
 *
 */
abstract class ApiServiceMocker<ApiType> : ApiMocker<ApiType> {

    private var enqueueExecutor: Executor = AsyncExecutor()
    protected var networkEmulator: NetworkEmulator = NetworkEmulator()

    private var latestResponse: Response<*>? = null
    private var nextError: Throwable? = null

    fun <ResponseType> createApiCall(responseData: ResponseType): Call<ResponseType> {

        return object : Call<ResponseType> {

            var hasBeenCancelled = false
            var hasBeenExecuted = false

            override fun enqueue(callback: Callback<ResponseType>) {

                enqueueExecutor.execute {
                    try {
                        val error = nextError
                        if (error != null) {
                            nextError = null
                            callback.onFailure(this, error)
                        }
                        else {
                            val response = execute()
                            callback.onResponse(this, response)
                        }
                    }
                    catch (e: IOException) {
                        callback.onFailure(this, e)
                    }
                }

            }

            override fun execute(): Response<ResponseType> {
                networkEmulator.simulateNetworkDelay()

                // Create the API response:
                val response = Response.success(responseData)

                hasBeenExecuted = true

                // Record this response:
                latestResponse = response

                return response
            }

            override fun isExecuted(): Boolean = hasBeenExecuted

            override fun cancel() {
                hasBeenCancelled = true
            }

            override fun isCanceled(): Boolean = hasBeenCancelled

            override fun request(): Request {
                // There is no original HTTP request to return.
                throw UnsupportedOperationException("Cannot create an okhttp3.Request for a ApiServiceMocker.")
            }

            override fun clone(): Call<ResponseType> = createApiCall(responseData)

        }

    }

    /**
     * Sets up a network error for the next request.
     */
    fun queueNetworkError() {
        nextError = IOException("Mock network error")
    }

    override fun setEnqueueExecutor(executor: Executor) {
        this.enqueueExecutor = executor
    }

    override fun clearRequestHistory() {
        latestResponse = null
    }

    class AsyncExecutor : Executor {
        override fun execute(command: Runnable?) {
            AsyncTask.execute(command)
        }
    }

    class SyncronousExecutor : Executor {
        override fun execute(command: Runnable?) {
            command?.run()
        }
    }

}