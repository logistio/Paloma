package ie.logistio.paloma.mock

/**
 *
 */
open class NetworkEmulator {

    private val lock = java.lang.Object()

    var networkDelayMillis = 0L

    open fun simulateNetworkDelay() {
        if (networkDelayMillis > 0) {
            try {
                synchronized(this) {
                    lock.wait(networkDelayMillis)
                }
            }
            catch (e: InterruptedException) {
                // TODO: Handle thread interruption.
                /*
                Timber.e( e.message)
                e.printStackTrace()
                */
            }
        }
    }

}