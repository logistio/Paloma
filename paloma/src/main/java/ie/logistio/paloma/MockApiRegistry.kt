package ie.logistio.paloma

import ie.logistio.paloma.mock.ApiMocker

/**
 * A registry of API implementations to use in place of Retrofit-generated APIs.
 */
class MockApiRegistry {

    private val registeredMockers: MutableMap<Class<*>, ApiMocker<*>> = HashMap()


    fun register(apiMocker: ApiMocker<*>) {
        registeredMockers.put(apiMocker.type, apiMocker)
    }

    fun <ApiType> find(apiType: Class<ApiType>): ApiType {
        if(isRegistered(apiType)) {
            return registeredMockers[apiType]?.createMockApi() as ApiType
        }
        else {
            throw IllegalArgumentException("There is no mocker registered for type $apiType")
        }
    }

    fun <ApiType> isRegistered(apiType: Class<ApiType>) = registeredMockers.containsKey(apiType)


    companion object {
        fun createWith(vararg services: ApiMocker<*>): MockApiRegistry {
            val reconcillor = MockApiRegistry()
            for (service in services) {
                reconcillor.register(service)
            }
            return reconcillor
        }

    }


}
